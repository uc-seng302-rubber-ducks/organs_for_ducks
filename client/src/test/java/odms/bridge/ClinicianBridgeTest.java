package odms.bridge;

import com.google.gson.Gson;
import odms.commons.model.Clinician;
import odms.commons.model.datamodel.ComboBoxClinician;
import odms.controller.AppController;
import okhttp3.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ClinicianBridgeTest extends BridgeTestBase {

    private ClinicianBridge bridge;

    @Before
    public void setUp() {
        bridge = new ClinicianBridge(mockClient);
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

    @Ignore //TODO come back to this after actual implementation is done. Need to check manually if working because don't know if mocking is working 28/08
    @Test
    public void getBasicCliniciansShouldReturnListOfBasicCliniciansOnSuccess() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn(new Gson().toJson(new ComboBoxClinician[]{
                new ComboBoxClinician("geoff", "0")
        }));

        List<ComboBoxClinician> result = bridge.getBasicClinicians("");

        Assert.assertEquals(1, result.size());

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
        Call mockCall = mock(Call.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenThrow(new IOException());

        Clinician result = bridge.getClinician("0", "asdf");
        Assert.assertNull(result);
    }

    @Test
    public void getClinicianShouldReturnNullOnNullResponse() throws IOException {
        Call mockCall = mock(Call.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(null);

        Clinician result = bridge.getClinician("0", "asdf");
        Assert.assertNull(result);
    }

    @Test
    @Ignore // also needs to make a request to get profile picture.
    public void getClinicianShouldReturnClinicianOnSuccess() throws IOException {
        Clinician expected = new Clinician("geoff", "0", "password");
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.code()).thenReturn(200);
        when(mockResponse.header(eq("Content-Type"))).thenReturn("image/jpg");
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn(new Gson().toJson(expected));

        Clinician actual = bridge.getClinician("0", "asdf");

        Assert.assertEquals(expected, actual);

    }
}
