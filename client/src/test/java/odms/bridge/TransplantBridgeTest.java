package odms.bridge;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import odms.commons.model._enum.Environments;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.TransplantDetails;
import odms.commons.utils.Log;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
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

public class TransplantBridgeTest extends BridgeTestBase {

    private TransplantBridge bridge;
    private String responseString;


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
        bridge = new TransplantBridge(mockClient);
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

        bridge.getWaitingList(0, 10, "", "", new ArrayList<>(), FXCollections.emptyObservableList());
        List<String> logs = Log.getDebugLogs();
        Assert.assertTrue(logs.get(0).endsWith("/transplantList?count=10&startIndex=0"));
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

        bridge.getWaitingList(0, 10, "", "here", new ArrayList<>(), FXCollections.emptyObservableList());
        List<String> logs = Log.getDebugLogs();
        Assert.assertTrue(logs.get(0).endsWith("/transplantList?count=10&startIndex=0&region=here"));
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

        bridge.getWaitingList(0, 10, "", "", new ArrayList<>(Arrays.asList(Organs.LIVER, Organs.LUNG)), FXCollections.emptyObservableList());
        List<String> logs = Log.getDebugLogs();

        Assert.assertTrue(logs.get(0).endsWith("/transplantList?count=10&startIndex=0&organs=LIVER&organs=LUNG"));
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

        bridge.getWaitingList(34, 54, "", "", new ArrayList<>(), FXCollections.emptyObservableList());
        List<String> logs = Log.getDebugLogs();

        Assert.assertTrue(logs.get(0).endsWith("/transplantList?count=54&startIndex=34"));

    }


}
