package seng302.Model;

import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    return response.body().string();
  }

  public static  void main(String[] args) {
    System.out.println("Please don't run me, this is for testing only");
    try {
      String res = getDrugInteractions("panadol", "tramadol");
      System.out.println(res);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
