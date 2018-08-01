package odms.utils;

import com.google.gson.Gson;
import odms.bridge.TransplantBridge;
import odms.commons.exception.ApiException;
import odms.commons.model._enum.Environments;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.TransplantDetails;
import odms.commons.utils.Log;
import odms.controller.AppController;
import okhttp3.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

    @Before
    public void setUp() {
        Log.setup(Environments.TEST);
        Log.clearDebugLogs();
        List<TransplantDetails> details = new ArrayList<>();
        details.add(new TransplantDetails("ABC1234", "Steve", Organs.HEART, LocalDate.now(), "there"));
        details.add(new TransplantDetails("ABC1235", "Frank", Organs.KIDNEY, LocalDate.now(), "there"));
        details.add(new TransplantDetails("ABC1236", "Geoff", Organs.HEART, LocalDate.now(), "there"));
        details.add(new TransplantDetails("ABC1237", "Jeff", Organs.HEART, LocalDate.now(), "canterbury"));
        details.add(new TransplantDetails("ABC1238", "Mattias", Organs.BONE_MARROW, LocalDate.now(), "yonder"));
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
    public void getWaitingListShouldThrowExceptionOnBadResponseCode() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockResponse.code()).thenReturn(404);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);

        bridge.getWaitingList(0, 10, "", "", new ArrayList<>());
    }

    @Test
    public void getWaitingListShouldReturnEmptyListWhenNoData() throws IOException {
        final String responseString = new Gson().toJson(new ArrayList<>());

        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);
        when(mockResponse.code()).thenReturn(200);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn(responseString);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);


        List<TransplantDetails> actual = bridge.getWaitingList(0, 10, "", "", new ArrayList<>());

        Assert.assertTrue(actual.isEmpty());
    }

    @Test
    public void getWaitingListShouldReturnEmptyListWhenNoResults() throws IOException {

        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);

        when(mockResponse.code()).thenReturn(200);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn("[]");
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        //returns a 200 code with body text of empty array

        List<TransplantDetails> actual = bridge.getWaitingList(0, 10, "", "", new ArrayList<>());

        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.isEmpty());
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
        Assert.assertEquals("http://localhost:4941/odms/v1/transplantList?count=10&startIndex=0", logs.get(0));
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
        Assert.assertEquals("http://localhost:4941/odms/v1/transplantList?count=10&startIndex=0&region=here", logs.get(0));
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

        Assert.assertEquals("http://localhost:4941/odms/v1/transplantList?count=10&startIndex=0&organs=LIVER&organs=LUNG", logs.get(0));
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

        Assert.assertEquals("http://localhost:4941/odms/v1/transplantList?count=54&startIndex=34", logs.get(0));

    }


}
