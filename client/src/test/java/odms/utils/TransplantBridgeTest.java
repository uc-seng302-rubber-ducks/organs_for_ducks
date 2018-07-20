package odms.utils;

import com.google.gson.Gson;
import odms.commons.exception.ApiException;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.TransplantDetails;
import odms.controller.AppController;
import okhttp3.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransplantBridgeTest {

    private TransplantBridge bridge;
    private OkHttpClient mockClient;
    private String responseString;

    @Before
    public void setUp() {
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

    @Test(expected = ApiException.class)
    public void getWaitingListShouldThrowExceptionOnBadResponseCode() throws IOException{
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockResponse.code()).thenReturn(404);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);

        bridge.getWaitingList(0, 10, "", "", new ArrayList<>());
    }

    @Test
    public void getWaitingListShouldReturnEmptyListWhenNoData() throws IOException{
        final String responseString = new Gson().toJson( new ArrayList<>());

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
    public void getWaitingListShouldReturnAllItemsWhenNoFilter() throws IOException{
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
    public void getWaitingListShouldFilterByRegion() {
        Assert.fail("not yet implemented");
    }

    @Test
    public void getWaitingListShouldFilterByOrgan() {
        Assert.fail("not yet implemented");
    }




}
