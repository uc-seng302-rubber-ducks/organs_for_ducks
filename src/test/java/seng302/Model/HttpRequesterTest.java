package seng302.Model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import org.junit.Test;

public class HttpRequesterTest {

  private OkHttpClient mockClient = mock(OkHttpClient.class);


  @Test
  public void ShouldSendDrugInteractionsRequest() throws IOException {
    //getDrugInteractions with two valid drug names
    String result = HttpRequester.getDrugInteractions("Panadol", "Tramadol", mockClient);
    Response mockResponse = mock(Response.class);
    when(mockClient.newCall(any(Request.class)).execute()).thenReturn(mockResponse);
    verify(mockClient, times(1)).newCall(any(Request.class)).execute();
  }

  @Test
  public void ShouldReturnBlankIfNoResponseBody() {
    //make mock return response.body == null
  }

  @Test
  public void ShouldRestrictResultsWhenAgeAndGenderPresent() {

  }

}
