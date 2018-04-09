package seng302.Model;

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
import org.json.simple.parser.ParseException;

public class HttpRequester {

  /**
   * uses ehealthme api to get interactions between two drugs
   * @param drugOneName string name of first drug
   * @param drugTwoName string name of second drug
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


  public static Set<String> getDrugInteractions(String drugOneName, String drugTwoName, String gender, int age, OkHttpClient client) throws IOException {

    Set<String> results = new HashSet<>();
    Set<String> ageResults = new HashSet<>();
    Set<String> genderResults = new HashSet<>();
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
      return ageResults;
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

  public static String[] getActiveIngredients(String drug, OkHttpClient client) throws IOException {
    String url = "http://mapi-us.iterar.co/api/" + drug + "/substances.json";
    Request request = new Request.Builder().url(url).build();
    Response response = client.newCall(request).execute();
    String rawString = response.body().string();
    JSONParser parser = new JSONParser();
    try {
      String formatted = rawString.substring(1, rawString.length() - 1);
      return formatted.split(",");
    } catch (Exception ex) {
      return new String[]{};
    }
  }
  public static  void main(String[] args) {
    System.out.println("Please don't run me, this is for testing only");
    try {
      //String res = getDrugInteractions("coumadin", "Acetaminophen", new OkHttpClient());
      //Set<String> res = getDrugInteractions("coumadin", "Acetaminophen","male",36, new OkHttpClient());
      String[] res = getActiveIngredients("reserpine", new OkHttpClient());
      //System.out.println(res);
      for (String s : res) {
        System.out.println(s);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
