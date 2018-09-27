package odms.bridge;

import com.google.gson.Gson;
import odms.commons.model.datamodel.BloodTest;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

;

public class BloodTestBridgeTest extends BridgeTestBase{

    private BloodTestBridge bloodTestBridge;

    @Before
    public void setup(){ bloodTestBridge = new BloodTestBridge(mockClient);}

    @Test
    public void testGoodGetBloodTest() throws IOException {
        BloodTest testBloodtest = new BloodTest();
        testBloodtest.setBloodTestId(1);
        Response mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(true);
        ResponseBody mockResponseBody = mock(ResponseBody.class);
        when(mockResponseBody.string()).thenReturn(new Gson().toJson(testBloodtest));
        when(mockResponse.body()).thenReturn(mockResponseBody);
        Call mockCall = mock(Call.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);

        BloodTest bt = bloodTestBridge.getBloodTest("1","ABC1234");

        Assert.assertTrue(bt.equals(testBloodtest));
    }

    @Test
    public void testBadGetBloodTest() throws IOException {
        BloodTest testBloodtest = new BloodTest();
        testBloodtest.setBloodTestId(1);
        Response mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(false);
        ResponseBody mockResponseBody = mock(ResponseBody.class);
        when(mockResponseBody.string()).thenReturn(new Gson().toJson(testBloodtest));
        when(mockResponse.body()).thenReturn(mockResponseBody);
        Call mockCall = mock(Call.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);

        BloodTest bt = bloodTestBridge.getBloodTest("1","ABC1234");

        Assert.assertTrue(bt == null);
    }
}
