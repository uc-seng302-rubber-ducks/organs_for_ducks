package seng302.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import seng302.model.datamodel.MedicationInteractionsResponse;

import java.io.IOException;
import java.util.*;

/**
 * Class deals with using APIs for functionality within the application
 */
public class HttpRequester {

    /**
     * uses ehealthme api to get interactions between two drugs
     *
     * @param drugOneName string name of first drug
     * @param drugTwoName string name of second drug
     * @param client      http client to be used. use `new OkHttpClient()` if you have no preference
     * @return json formatted string containing the interactions between the two drugs
     * @throws IOException caused by error with server connection
     */
    public static String getDrugInteractions(String drugOneName, String drugTwoName,
                                             OkHttpClient client) throws IOException {

        String url =
                "https://www.ehealthme.com/api/v1/drug-interaction/" + drugOneName + "/" + drugTwoName
                        + "/";
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        String result = response.body().string();
        if (result != null) {
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
     * @param client      http client to be used. use `new OkHttpClient()` if you have no preference
     * @return set containing strings of format interaction (duration). e.g. pneumonia (5 - 10 years)
     */
    public static Set<String> getDrugInteractions(String drugOneName, String drugTwoName,
                                                  String gender, int age, OkHttpClient client) {

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
            rawResponse = getDrugInteractions(drugOneName, drugTwoName, client);

        } catch (IOException e) {
            Log.warning("failed to get interactions", e);
        }
        Gson gson = new GsonBuilder().create();
        MedicationInteractionsResponse responseObject = gson.fromJson(rawResponse, MedicationInteractionsResponse.class);
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
    private static Set<String> interpretInteractions(MedicationInteractionsResponse response, String ageRange, String gender) {
        //init and age interactions
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
     * @param input  string to be auto completed
     * @param client client to make the request on
     * @return String containing the results
     * @throws IOException thrown when IO fails
     */
    public static String getSuggestedDrugs(String input, OkHttpClient client) throws IOException {

        String url = "http://mapi-us.iterar.co/api/autocomplete?query=" + input;
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        JSONObject suggestions;
        try {
            assert response.body() != null;
            suggestions = (JSONObject) new JSONParser().parse(response.body().string());
            return suggestions.get("suggestions").toString();
        } catch (ParseException e) {
            Log.severe("HttpRequester parseexception in getSuggestedDrugs", e);
            return "";
        }

    }

    /**
     * sends a request to http://mapi-us.iterar.co/api/ to get a json file containing active
     * ingredients in a given drug
     *
     * @param drug   the drug to be checked e.g. xanax, reserpine, etc.
     * @param client http client to be used. use new OkHttpClient() if you have no preference
     * @return string array of each active ingredient
     * @throws IOException thrown when IO fails
     */
    public static String[] getActiveIngredients(String drug, OkHttpClient client) throws IOException {
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

    public static void main(String[] args) {
        getDrugInteractions("coumadin", "acetaminophen", "m", 30, new OkHttpClient());
    }
}
