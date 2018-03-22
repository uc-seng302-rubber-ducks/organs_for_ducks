package seng302.Model;

import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class HttpRequester {

  /**
   * uses ehealthme api to get interactions between two drugs
   * @param drugOneName string name of first drug
   * @param drugTwoName string name of second drug
   * @return json formatted string containing the interactions between the two drugs
   * @throws IOException
   */
  public static String getDrugInteractions(String drugOneName, String drugTwoName) throws IOException {

    OkHttpClient client = new OkHttpClient();
    String url = "https://www.ehealthme.com/api/v1/drug-interaction/"+drugOneName+"/"+drugTwoName+"/";
    Request request = new Request.Builder().url(url).build();
    Response response = client.newCall(request).execute();
    try {
      return response.body().string();
    } catch (NullPointerException ex) {
      return "";
    }
  }
  public static String getDrugInteractions(String drugOneName, String drugTwoName, String gender, int Age) throws IOException {

    OkHttpClient client = new OkHttpClient();
    String url = "https://www.ehealthme.com/api/v1/drug-interaction/"+drugOneName+"/"+drugTwoName+"/";
    Request request = new Request.Builder().url(url).build();
    Response response = client.newCall(request).execute();

    try {
      String rawString = response.body().string();
      JSONParser parser = new JSONParser();
      JSONObject json = (JSONObject) parser.parse(rawString);
      JSONArray ageInteractions = (JSONArray) json.get("age_interaction");
    } catch (Exception ex) {
      return "";
    }
    return ""; //TODO change this
  }

  public static  void main(String[] args) {
    System.out.println("Please don't run me, this is for testing only");
    try {
      String res = getDrugInteractions("tramadol", "panadol");
      System.out.println(res);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
