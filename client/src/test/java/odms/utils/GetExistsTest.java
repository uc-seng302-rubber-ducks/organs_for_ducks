package odms.utils;

import odms.bridge.AdministratorBridge;
import odms.bridge.ClinicianBridge;
import odms.bridge.RoleBridge;
import odms.bridge.UserBridge;
import odms.commons.config.ConfigPropertiesSession;
import okhttp3.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * this is a parameterised test for the getExists method, identical in AdministratorBridge, ClinicianBridge, and UserBridge
 */
@RunWith(Parameterized.class)
public class GetExistsTest {
    private static OkHttpClient mockClient = mock(OkHttpClient.class);

    @Before
    public void setUp() {
        ConfigPropertiesSession session = mock(ConfigPropertiesSession.class);
        ConfigPropertiesSession.setInstance(session);
        when(session.getProperty(eq("server.url"), anyString())).thenReturn("http://test.url/asdf");
    }

    @After
    public void tearDown() {
        ConfigPropertiesSession.setInstance(null);
    }

    @Parameterized.Parameters
    public static Collection<RoleBridge> data() {
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
