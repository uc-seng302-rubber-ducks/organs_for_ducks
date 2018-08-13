package odms.utils;

import com.google.gson.Gson;
import odms.bridge.TransplantBridge;
import odms.commons.exception.ApiException;
import odms.commons.model._enum.Environments;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.TransplantDetails;
import odms.commons.utils.ConfigPropertiesLoader;
import odms.commons.utils.Log;
import odms.controller.AppController;
import okhttp3.*;
import org.junit.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransplantBridgeTest {

    private TransplantBridge bridge;
    private OkHttpClient mockClient;
    private String responseString;
    private String serverUrl = new ConfigPropertiesLoader()
            .loadConfig("clientConfig.properties")
            .getProperty("server.url");

    @Before
    public void setUp() {
        Log.setup(Environments.TEST);
        Log.clearDebugLogs();
        List<TransplantDetails> details = new ArrayList<>();
        details.add(new TransplantDetails("ABC1234", "Steve", Organs.HEART, LocalDate.now(), "there", 0,"A+"));
        details.add(new TransplantDetails("ABC1235", "Frank", Organs.KIDNEY, LocalDate.now(), "there", 0,"A+"));
        details.add(new TransplantDetails("ABC1236", "Geoff", Organs.HEART, LocalDate.now(), "there", 0,"A+"));
        details.add(new TransplantDetails("ABC1237", "Jeff", Organs.HEART, LocalDate.now(), "canterbury", 0,"A+"));
        details.add(new TransplantDetails("ABC1238", "Mattias", Organs.BONE_MARROW, LocalDate.now(), "yonder", 0,"A+"));
        responseString = new Gson().toJson(details);

        mockClient = mock(OkHttpClient.class);
        AppController mockController = mock(AppController.class);
        when(mockController.getToken()).thenReturn("abcd");
        AppController.setInstance(mockController);
        bridge = new TransplantBridge(mockClient);
    }

    @After
    public void tearDown() {
        //force it to re-create singleton instance as in production
        AppController.setInstance(null);
    }

    @Test(expected = ApiException.class)
    @Ignore // Ignored because can't really test exceptions on separate threads/Unsure how to
    public void getWaitingListShouldThrowExceptionOnBadResponseCode() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockResponse.code()).thenReturn(404);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);

        bridge.getWaitingList(0, 10, "", "", new ArrayList<>());
    }

    @Test
    public void getWaitingListShouldHaveNoUrlFilter() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);

        when(mockResponse.code()).thenReturn(200);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn(responseString);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);

        bridge.getWaitingList(0, 10, "", "", new ArrayList<>());
        List<String> logs = Log.getDebugLogs();
        Assert.assertEquals(serverUrl + "/transplantList?count=10&startIndex=0", logs.get(0));
    }


    @Test
    public void getWaitingListShouldFilterByRegionInUrl() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);

        when(mockResponse.code()).thenReturn(200);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn(responseString);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);

        bridge.getWaitingList(0, 10, "", "here", new ArrayList<>());
        List<String> logs = Log.getDebugLogs();
        Assert.assertEquals(serverUrl + "/transplantList?count=10&startIndex=0&region=here", logs.get(0));
    }

    @Test
    public void getWaitingListShouldFilterByOrganInUrl() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);

        when(mockResponse.code()).thenReturn(200);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn(responseString);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);

        bridge.getWaitingList(0, 10, "", "", new ArrayList<>(Arrays.asList(Organs.LIVER, Organs.LUNG)));
        List<String> logs = Log.getDebugLogs();

        Assert.assertEquals(serverUrl + "/transplantList?count=10&startIndex=0&organs=LIVER&organs=LUNG", logs.get(0));
    }

    @Test
    public void getWaitingListShouldPaginateInUrl() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);

        when(mockResponse.code()).thenReturn(200);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn(responseString);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);

        bridge.getWaitingList(34, 54, "", "", new ArrayList<>());
        List<String> logs = Log.getDebugLogs();

        Assert.assertEquals(serverUrl + "/transplantList?count=54&startIndex=34", logs.get(0));

    }


}
