package seng302.Model;

import java.util.HashMap;
import java.util.Iterator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HttpRequester {

  /**
   * uses ehealthme api to get interactions between two drugs
   * @param drugOneName string name of first drug
   * @param drugTwoName string name of second drug
   * @param client http client to be used. use `new OkHttpClient()` if you have no preference
   * @return json formatted string containing the interactions between the two drugs
   * @throws IOException caused by error with server connection
   */
  public static String getDrugInteractions(String drugOneName, String drugTwoName, OkHttpClient client) throws IOException {

    String url = "https://www.ehealthme.com/api/v1/drug-interaction/"+drugOneName+"/"+drugTwoName+"/";
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
   * @param drugOneName name of the first drug
   * @param drugTwoName name of the second drug
   * @param gender gender of the user (anything starting with m or f will be taken as male or female)
   * @param age integer age of the user
   * @param client http client to be used. use `new OkHttpClient()` if you have no preference
   * @return set containing strings of format interaction (duration). e.g. pneumonia (5 - 10 years)
   * @throws IOException caused by error with server connection
   */
  public static Set<String> getDrugInteractions(String drugOneName, String drugTwoName, String gender, int age, OkHttpClient client) throws IOException {

    Set<String> results = new HashSet<>();
    Set<String> ageResults = new HashSet<>();
    Set<String> genderResults = new HashSet<>();
    Set<String> finalResults = new HashSet<>();
    String url = "https://www.ehealthme.com/api/v1/drug-interaction/"+drugOneName+"/"+drugTwoName+"/";
    Request request = new Request.Builder().url(url).build();
    Response response = client.newCall(request).execute();
    String ageRange;
    if (age > 59){
      ageRange = "60+";
    } else if(age < 10){
      results.add("Too young");
      return results;
    }
    else {
      ageRange = Integer.toString((age / 10) * 10) +"-" + Integer.toString((age / 10) * 10 + 9);
    }
    try {
      String rawString = response.body().string();
      if (rawString == null) {
        return new HashSet<>();
      }
      JSONParser parser = new JSONParser();
      JSONObject json = (JSONObject) parser.parse(rawString);
      JSONObject ageInteractions = (JSONObject) json.get("age_interaction");
      JSONObject genderInteractions = (JSONObject) json.get("gender_interaction");
      JSONObject durationInteractions = (JSONObject) json.get("duration_interaction");
      JSONArray ageProblems = (JSONArray) ageInteractions.get(ageRange);
      ageResults.addAll(ageProblems);

      if(gender.startsWith("m") || gender.startsWith("M")){
        JSONArray genderedInteractions  = (JSONArray) genderInteractions.get("male");
        genderResults.addAll(genderedInteractions);
      } else if (gender.startsWith("f") || gender.startsWith("F")){
        JSONArray genderedInteractions  = (JSONArray) genderInteractions.get("female");
        genderResults.addAll(genderedInteractions);
      } else {
        JSONArray genderedInteractions1  = (JSONArray) genderInteractions.get("female");
        genderResults.addAll(genderedInteractions1);
        JSONArray genderedInteractions  = (JSONArray) genderInteractions.get("male");
        genderResults.addAll(genderedInteractions);
      }
      ageResults.retainAll(genderResults);
      //cross current result set with durations to get symptom (duration) strings
      Set<String> durationKeySet = durationInteractions.keySet();
      HashMap<String, ArrayList<String>> temp = new HashMap<>();
      for(String duration: durationKeySet) {
        JSONArray afflictions = (JSONArray) durationInteractions.get(duration);
        for (Object affliction: afflictions) {
          String symptom = (String) affliction;
          if (ageResults.contains(symptom)) {
            //put into map
            if (temp.containsKey(symptom)) {
              ArrayList<String> newDurations = temp.get(symptom);
              newDurations.add(duration);
              temp.put(symptom, newDurations);
            }
            else {
              ArrayList<String> newDurations = new ArrayList<>();
              newDurations.add(duration);
              temp.put(symptom, newDurations);
            }
          }
        }
      }
      //cull duplicate symptoms with different duration entries
      //from temp
      Set<String> finalKeySet = temp.keySet();
      for (String key: finalKeySet) {
        //TODO currently gets first duration in array, not necessarily the shortest/longest
        String duration = temp.get(key).get(0);
        if (duration.equals("not specified") && temp.get(key).size() > 1) {
          duration = temp.get(key).get(1);
        }
        finalResults.add(key+" ("+duration+")");
      }

      return finalResults;
    } catch (Exception ex) {
      return new HashSet<>();
    }
  }

  public static String[] getSuggestedDrugs(String input) throws IOException {
    String[] list = new String[]{};
    OkHttpClient client = new OkHttpClient();
    String url = "mapi-us.iterar.co/api/autocomplete?query=" + input;
    Request request = new Request.Builder().url(url).build();
    Response responses = client.newCall(request).execute();
    //TODO: find a way to make the responses into a list to be sent back
    return list;
  }

  public static  void main(String[] args) {
    System.out.println("Please don't run me, this is for testing only");
    try {
      //String res = getDrugInteractions("coumadin", "Acetaminophen", new OkHttpClient());
      Set<String> res = getDrugInteractions("coumadin", "Acetaminophen","m",35, new OkHttpClient());
      System.out.println(res);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
