package odms.bridge;

import com.google.gson.Gson;
import odms.commons.model.Clinician;
import odms.controller.AppController;
import okhttp3.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ClinicianBridgeTest {

    private ClinicianBridge bridge;
    private OkHttpClient mockClient;
    private AppController mockController;

    @Before
    public void setUp() {
        mockClient = mock(OkHttpClient.class);
        mockController = mock(AppController.class);
        when(mockController.getToken()).thenReturn("abcd");
        AppController.setInstance(mockController);
        bridge = new ClinicianBridge(mockClient);
    }

    @After
    public void tearDown() {
        AppController.setInstance(null);
    }

    @Test
    public void getCliniciansShouldReturnListOfCliniciansOnSuccess() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn(new Gson().toJson(new Clinician[]{
                new Clinician("geoff", "0", "password")
        }));
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);


        bridge.getClinicians(0, 10, "hackerman", "here", "asdf");
        verify(mockCall).enqueue(callbackCaptor.capture());
        Callback callback = callbackCaptor.getValue();

        callback.onResponse(mockCall, mockResponse);

        verify(mockController, times(1)).addClinician(any(Clinician.class));

    }


    @Test
    public void postClinicianShouldCloseResponseOnSuccessfulPost() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);

        bridge.postClinician(new Clinician(), "abcd");
        verify(mockCall).enqueue(callbackCaptor.capture());
        Callback callback = callbackCaptor.getValue();

        callback.onResponse(mockCall, mockResponse);
        verify(mockResponse, times(1)).close();
    }


    @Test(expected = IOException.class)
    public void postClinicianShouldThrowExceptionOnBadResponseCode() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(false);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);

        bridge.postClinician(new Clinician(), "abcd");
        verify(mockCall).enqueue(callbackCaptor.capture());
        Callback callback = callbackCaptor.getValue();

        callback.onResponse(mockCall, mockResponse);
    }

    @Test(expected = IOException.class)
    public void putClinicianShouldThrowExceptionOnBadResponse() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(false);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);

        bridge.putClinician(new Clinician("steve", "0", "password"), "0", "tokenGoesHere");
        verify(mockCall).enqueue(callbackCaptor.capture());
        Callback callback = callbackCaptor.getValue();

        callback.onResponse(mockCall, mockResponse);

    }

    @Test
    public void putClinicianShouldCloseResponseOnSuccess() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);

        bridge.putClinician(new Clinician("steve", "0", "password"), "0", "tokenGoesHere");
        verify(mockCall).enqueue(callbackCaptor.capture());
        Callback callback = callbackCaptor.getValue();

        callback.onResponse(mockCall, mockResponse);

        verify(mockResponse, times(1)).close();
    }


    @Test(expected = IOException.class)
    public void deleteClinicianShouldThrowExceptionOnBadResponse() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(false);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);

        bridge.deleteClinician(new Clinician("steve", "0", "password"), "0");
        verify(mockCall).enqueue(callbackCaptor.capture());
        Callback callback = callbackCaptor.getValue();

        callback.onResponse(mockCall, mockResponse);

    }

    @Test
    public void deleteClinicianShouldCloseResponseOnSuccess() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);

        bridge.deleteClinician(new Clinician("steve", "0", "password"), "0");
        verify(mockCall).enqueue(callbackCaptor.capture());
        Callback callback = callbackCaptor.getValue();

        callback.onResponse(mockCall, mockResponse);

        verify(mockResponse, times(1)).close();
    }

    @Test
    public void getClinicianShouldReturnNullOnFailToSend() throws IOException {
        Assert.fail("not yet implemented");
    }

    @Test
    public void getClinicianShouldReturnNullOnNullResponse() {
        Assert.fail("not yet implemented");
    }

    @Test
    public void getClinicianShouldReturnClinicianOnSuccess() {
        Assert.fail("not yet implemented");
    }
}
