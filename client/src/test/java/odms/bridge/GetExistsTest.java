package odms.bridge;

import okhttp3.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * this is a parameterised test for the getExists method, identical in AdministratorBridge, ClinicianBridge, and UserBridge
 */
@RunWith(Parameterized.class)
public class GetExistsTest extends BridgeTestBase {
    private static OkHttpClient mockClient = mock(OkHttpClient.class);


    @Parameterized.Parameters
    public static Collection<RoleBridge> data() {
        staticBefore();
        return new ArrayList<>(Arrays.asList(new AdministratorBridge(mockClient), new ClinicianBridge(mockClient), new UserBridge(mockClient)));
    }

    @Parameterized.Parameter
    public RoleBridge bridge;



    @Test
    public void getExistsReturnsTrueWhenAdminExists() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);

        when(mockResponse.code()).thenReturn(200);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn("true");
        when(mockCall.execute()).thenReturn(mockResponse);

        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);

        Assert.assertEquals(true, bridge.getExists("fts34"));
    }

    @Test
    public void getExistsReturnsFalseWhenNoAdminExists() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);

        when(mockResponse.code()).thenReturn(200);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn("false");
        when(mockCall.execute()).thenReturn(mockResponse);

        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);

        Assert.assertEquals(false, bridge.getExists("fts34"));
    }

    @Test
    public void getExistsReturnsFalseWhenNoResponseBody() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);

        when(mockResponse.code()).thenReturn(200);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        //no response body handler - returns null
        when(mockCall.execute()).thenReturn(mockResponse);

        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);

        Assert.assertEquals(false, bridge.getExists("fts34"));

    }

    @Test
    public void getExistsReturnsFalseWhenIOException() throws IOException {

        Call mockCall = mock(Call.class);
        when(mockCall.execute()).thenThrow(new IOException());
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);

        Assert.assertEquals(false, bridge.getExists("fts34"));

    }
}
