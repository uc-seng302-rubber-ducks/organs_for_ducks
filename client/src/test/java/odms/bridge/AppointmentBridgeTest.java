package odms.bridge;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import odms.commons.model.Appointment;
import odms.commons.model._enum.UserType;
import okhttp3.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AppointmentBridgeTest extends BridgeTestBase {

    private AppointmentsBridge appointmentsBridge;

    @Before
    public void setUp() {
        appointmentsBridge = new AppointmentsBridge(mockClient, true);
    }

    @Test
    public void testGoodRequestGoodResponse() throws IOException {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();

        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(true);
        ResponseBody mockResponseBody = mock(ResponseBody.class);
        when(mockResponseBody.string()).thenReturn(new Gson().toJson(new Appointment[]{
                new Appointment()
        }));

        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);

        appointmentsBridge.getAppointments(30, 0, appointments, "0", UserType.CLINICIAN);
        verify(mockCall).enqueue(callbackCaptor.capture());
        Callback callback = callbackCaptor.getValue();

        callback.onResponse(mockCall, mockResponse);
        assertTrue(appointments.size() == 1);
    }

    @Test
    public void testBadRequest() throws IOException {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();

        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockResponse.code()).thenReturn(400);
        ResponseBody mockResponseBody = mock(ResponseBody.class);
        when(mockResponseBody.string()).thenReturn("{}");

        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);

        appointmentsBridge.getAppointments(30, 0, appointments, "0", UserType.CLINICIAN);
        verify(mockCall).enqueue(callbackCaptor.capture());
        Callback callback = callbackCaptor.getValue();

        callback.onResponse(mockCall, mockResponse);
        assertTrue(appointments.isEmpty());
    }

    @Test
    public void testGetUnseenAppointment_ReturnsAppointmentOnSuccess() throws IOException {
//        Appointment appointment;
//
//        Call mockCall = mock(Call.class);
//        Response mockResponse = mock(Response.class);
//        ResponseBody mockResponseBody = mock(ResponseBody.class);
//        when(mockResponse.body()).thenReturn(mockResponseBody);
//
//        when(mockResponseBody.string()).thenReturn(new Gson().toJson(new Appointment[]{
//                new Appointment()
//        }));
//
//        when(mockResponse.body()).thenReturn(mockResponseBody);
//        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
//
//        appointment = appointmentsBridge.getUnseenAppointment("ABC1234");
//
//
//        Assert.assertNotNull(appointment);
    }

    @Test
    public void testGetUnseenAppointment_ReturnsNullOnEmptyList() {

    }

    @Test
    public void testGetUnseenAppointment_ReturnsNullOnNullBody() {

    }

    @Test
    public void testGetUnseenAppointment_ReturnsNullOnNullPointerException() throws IOException {
        Appointment appointment;

        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockResponse.code()).thenReturn(400);
        ResponseBody mockResponseBody = mock(ResponseBody.class);
        when(mockResponseBody.string()).thenReturn("{}");

        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);

        appointment = appointmentsBridge.getUnseenAppointment("ABC1234");


        Assert.assertNull(appointment);
    }

    @Test
    public void testGetUnseenAppointment_ReturnsNullOnIOException() {

    }
}
