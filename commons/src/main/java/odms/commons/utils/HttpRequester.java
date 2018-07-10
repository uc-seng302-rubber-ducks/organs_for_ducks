package odms.commons.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import odms.commons.model.CacheManager;
import odms.commons.model.MedicationInteractionCache;
import odms.commons.model.datamodel.MedicationInteractionsResponse;
import odms.commons.model.datamodel.SuggestedDrugsResponse;
import odms.commons.model.datamodel.TimedCacheValue;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;

/**
 * Class deals with using APIs for functionality within the application
 */
public class HttpRequester {

    private OkHttpClient client;
    private MedicationInteractionCache interactionCache;

    public HttpRequester(OkHttpClient client) {
        this.client = client;
        CacheManager cacheManager = CacheManager.getInstance();
        this.interactionCache = cacheManager.getInteractionCache();
    }

    /**
     * uses ehealthme api to get interactions between two drugs
     *
     * @param drugOneName string name of first drug
     * @param drugTwoName string name of second drug
     * @return json formatted string containing the interactions between the two drugs
     * @throws IOException caused by error with server connection
     */
    public String getDrugInteractions(String drugOneName, String drugTwoName) throws IOException {

        TimedCacheValue<String> cached = interactionCache.get(drugOneName, drugTwoName);
        if (cached != null) {
            return cached.getValue();
        }
        String url =
                "https://www.ehealthme.com/api/v1/drug-interaction/" + drugOneName + "/" + drugTwoName
                        + "/";
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        String result = response.body().string();
        if (result != null) {
            interactionCache.add(drugOneName, drugTwoName, result);
            return result;
        }
        return "";
    }


    /**
     * returns a set containing strings of format interaction (duration)
     *
     * @param drugOneName name of the first drug
     * @param drugTwoName name of the second drug
     * @param gender      gender of the user (anything starting with m or f will be taken as male or female)
     * @param age         integer age of the user
     * @return set containing strings of format interaction (duration). e.g. pneumonia (5 - 10 years)
     */
    public Set<String> getDrugInteractions(String drugOneName, String drugTwoName,
                                           String gender, int age) {

        //return empty set if drugs are null
        if (drugOneName == null || drugTwoName == null) {
            return new HashSet<>();
        }
        //ensures a result is given (for both genders if none is given)
        if (gender == null) {
            gender = "unknown";
        }

        String ageRange;
        if (age > 59) {
            ageRange = "60+";
        } else if (age < 10) {
            Set<String> results = new HashSet<>();
            results.add("Too young");
            return results;
        } else {
            ageRange = Integer.toString((age / 10) * 10) + "-" + Integer.toString((age / 10) * 10 + 9);
        }

        String rawResponse = null;
        try {
            rawResponse = getDrugInteractions(drugOneName, drugTwoName);

        } catch (IOException e) {
            Log.warning("failed to get interactions", e);
        }
        Gson gson = new GsonBuilder().create();
        MedicationInteractionsResponse responseObject = null;
        try {
            responseObject = gson.fromJson(rawResponse, MedicationInteractionsResponse.class);
        } catch (JsonSyntaxException ex) {
            Log.warning("could not interpret API response:\n" + rawResponse, ex);
        }
        if (responseObject == null) {
            return new HashSet<>();
        }

        return interpretInteractions(responseObject, ageRange, gender);
    }

    /**
     * takes result object extracts information relevant to the age range and gender
     *
     * @param ageRange string of format "x0-x9", "nan", or "60+" where x is any integer less than 6
     * @param gender   strings starting with m will be interpreted as male, f is female. anything else will return results for both
     * @return set of relevant effects caused by drugs interacting
     */
    private Set<String> interpretInteractions(MedicationInteractionsResponse response, String ageRange, String gender) {
        //init and age interactions
        if (response == null || response.getAgeInteractions() == null) {
            return new HashSet<>();
        }
        Set<String> interactions = new HashSet<>(response.getAgeInteractions().get(ageRange));

        if (gender.toLowerCase().startsWith("m")) {
            //male interactions
            interactions.addAll(response.getGenderInteractions().get("male"));
        } else if (gender.toLowerCase().startsWith("f")) {
            //female interactions
            interactions.addAll(response.getGenderInteractions().get("female"));
        } else {
            //all the interactions
            interactions.addAll(response.getGenderInteractions().get("male"));
            interactions.addAll(response.getGenderInteractions().get("female"));
        }
        //durations map of duration: [affliction]
        Map<String, List<String>> durations = response.getDurationInteractions();

        //temporary map of affliction: [duration]
        Map<String, List<String>> temp = new HashMap<>();
        for (String duration : durations.keySet()) {
            for (String affliction : durations.get(duration)) {
                //if affliction is relevant to this person
                if (interactions.contains(affliction)) {
                    if (temp.containsKey(affliction)) {
                        temp.get(affliction).add(duration);
                    } else {
                        List<String> newDurations = new ArrayList<>();
                        newDurations.add(duration);
                        temp.put(affliction, newDurations);
                    }
                }

            }
        }
        //create new set and give durations to the previous interactions set
        Set<String> finalInteractions = new HashSet<>();

        for (String interaction : interactions) {
            if (temp.get(interaction) != null) {
                String duration = temp.get(interaction).get(0);
                if (duration.equals("not specified") && temp.get(interaction).size() > 1) {
                    duration = temp.get(interaction).get(1);
                }
                finalInteractions.add(interaction + " (" + duration + ")");
            } else {
                finalInteractions.add(interaction + " (not specified)");
            }

        }

        return finalInteractions;
    }

    /**
     * Takes a string argument and provides auto completed results
     *
     * @param input string to be auto completed
     * @return String containing the results
     * @throws IOException thrown when IO fails
     */


    public String getSuggestedDrugs(String input) throws IOException {

        String url = "http://mapi-us.iterar.co/api/autocomplete?query=" + input;
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        Gson gson = new Gson();
        assert response.body() != null;
        SuggestedDrugsResponse suggestions = gson.fromJson(response.body().string(), SuggestedDrugsResponse.class);
        return suggestions.toString();
    }

    /**
     * sends a request to http://mapi-us.iterar.co/api/ to get a json file containing active
     * ingredients in a given drug
     *
     * @param drug the drug to be checked e.g. xanax, reserpine, etc.
     * @return string array of each active ingredient
     * @throws IOException thrown when IO fails
     */
    public String[] getActiveIngredients(String drug) throws IOException {
        String url = "http://mapi-us.iterar.co/api/" + drug + "/substances.json";
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        assert response.body() != null;
        String rawString = response.body().string();
        try {
            String formatted = rawString.substring(1, rawString.length() - 1);
            String[] array = formatted.split(",");
            for (int i = 0; i < array.length; i++) {
                array[i] = array[i].replace("\"", "");
            }
            return array;
        } catch (Exception ex) {
            return new String[]{};
        }
    }
}
