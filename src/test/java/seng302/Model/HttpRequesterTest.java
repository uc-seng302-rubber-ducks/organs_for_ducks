package seng302.Model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;
import org.junit.Test;

public class HttpRequesterTest {

  private OkHttpClient mockClient = mock(OkHttpClient.class);


  @Test
  public void ShouldSendDrugInteractionsRequest() throws IOException {
    //getDrugInteractions with two valid drug names
    Response mockResponse = mock(Response.class);
    //mock of the underlying classes not visible from source code
    Call mockCall = mock(Call.class);
    ResponseBody mockResponseBody = mock(ResponseBody.class);
    //set behaviours
    when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
    when(mockCall.execute()).thenReturn(mockResponse);
    when(mockResponse.body()).thenReturn(mockResponseBody);
    when(mockResponseBody.string()).thenReturn("test body");
    String result = HttpRequester.getDrugInteractions("Panadol", "Tramadol", mockClient);

    verify(mockCall, times(1)).execute();
    assert(result.equals("test body"));
  }

  @Test
  public void ShouldReturnBlankIfNoResponseBody() throws IOException {
    //getDrugInteractions with two valid drug names
    Response mockResponse = mock(Response.class);
    //mock of the underlying classes not visible from source code
    Call mockCall = mock(Call.class);
    ResponseBody mockResponseBody = mock(ResponseBody.class);
    //set behaviours
    when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
    when(mockCall.execute()).thenReturn(mockResponse);
    when(mockResponseBody.string()).thenReturn(null);
    when(mockResponse.body()).thenReturn(mockResponseBody);

    String result = HttpRequester.getDrugInteractions("water", "milk", mockClient);

    verify(mockCall, times(1)).execute();
    assert(result.equals(""));
  }

  @Test
  public void ShouldRestrictResultsWhenAgeAndGenderPresent() throws Exception{
    //getDrugInteractions with two valid drug names
    Response mockResponse = mock(Response.class);
    //mock of the underlying classes not visible from source code
    Call mockCall = mock(Call.class);
    ResponseBody mockResponseBody = mock(ResponseBody.class);

    //get json body from file
    JSONParser parser = new JSONParser();
    JSONObject responseBody = (JSONObject) parser.parse(new FileReader(
        "src/test/resources/httpResponses/coumadin-acetaminophen-interactions.json"));

    //set behaviours
    when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
    when(mockCall.execute()).thenReturn(mockResponse);
    when(mockResponseBody.string()).thenReturn(responseBody.toString());
    when(mockResponse.body()).thenReturn(mockResponseBody);
    Set<String> expected = new HashSet<>();
    expected.add("anxiety");
    expected.add("anaemia");
    expected.add("pneumonia");
    expected.add("injury");
    expected.add("nausea");

    Set<String> results = HttpRequester.getDrugInteractions("coumadin", "acetaminophen", "m", 36, mockClient);

    verify(mockCall, times(1)).execute();
    Assert.assertEquals(expected, results);
  }

}
