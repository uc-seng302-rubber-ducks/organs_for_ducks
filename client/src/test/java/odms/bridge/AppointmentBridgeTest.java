package odms.bridge;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import odms.TestUtils.CommonTestMethods;
import odms.commons.model.Appointment;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.model._enum.UserType;
import odms.commons.utils.Log;
import okhttp3.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.testfx.api.FxToolkit;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    public void getAppointmentsShouldNotPopulateListOnFailToSend() throws IOException {
        ObservableList<Appointment> testList = FXCollections.emptyObservableList();
        Call mockCall = mock(Call.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenThrow(new IOException());

        appointmentsBridge.getClinicianAppointments(0,1,"0", "asdf", testList);
        Assert.assertTrue(testList.isEmpty());
    }

    @Test
    public void getAppointmentsShouldNotPopulateOnNullResponse() throws IOException {
        ObservableList<Appointment> testList = FXCollections.emptyObservableList();
        Call mockCall = mock(Call.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(null);

        appointmentsBridge.getClinicianAppointments(0,1,"0", "asdf", testList);
        Assert.assertTrue(testList.isEmpty());
    }

    @Test
    public void getAppointmentsShouldReturnAppointmentsOnSuccess() throws Exception {
        CommonTestMethods.runMethods();
        FxToolkit.registerPrimaryStage();
        try {
            List<Appointment> someList = new ArrayList<>();
            ObservableList<Appointment> testList = FXCollections.observableList(someList);

            User testUser = new User();
            testUser.setNhi("ABC1234");
            Clinician testClinician = new Clinician();
            testClinician.setStaffId("0");
            Appointment testAppointment = new Appointment(testUser.getNhi(), testClinician.getStaffId(), AppointmentCategory.BLOOD_TEST, LocalDateTime.now(), "test", AppointmentStatus.PENDING);
            testAppointment.setAppointmentId(0);
            List<Appointment> expected = new ArrayList<>();
            expected.add(testAppointment);

            ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
            Call mockCall = mock(Call.class);
            Response mockResponse = mock(Response.class);
            ResponseBody mockResponseBody = mock(ResponseBody.class);
            when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
            when(mockCall.execute()).thenReturn(mockResponse);
            when(mockResponse.code()).thenReturn(200);
            when(mockResponse.header(eq("Content-Type"))).thenReturn("image/jpg");
            when(mockResponse.body()).thenReturn(mockResponseBody);
            when(mockResponseBody.string()).thenReturn(new Gson().toJson(expected));

            appointmentsBridge.getClinicianAppointments(0, 1, "0", "asdf", testList);
            verify(mockCall).enqueue(callbackCaptor.capture());
            Callback callback = callbackCaptor.getValue();
            callback.onResponse(mockCall, mockResponse);
            try {
                waitForRunLater();
            } catch (InterruptedException e) {
                Log.severe("The callback was interrupted", e);
            }
            Assert.assertEquals(expected.get(0), testList.get(0));
        } finally {
            FxToolkit.cleanupStages();
        }
    }

    @Test
    public void testGetUnseenAppointmentReturnsAppointmentOnSuccess() throws IOException {
        Appointment expected = new Appointment();
        expected.setAppointmentId(0);

        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        ResponseBody mockResponseBody = mock(ResponseBody.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.code()).thenReturn(200);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn(new Gson().toJson(expected));

        Appointment actual = (appointmentsBridge.getUnseenAppointment("default"));
        Assert.assertEquals(expected, actual);
    }


    @Test
    public void testGetUnseenAppointmentReturnsNullOnNullBody() throws IOException {
        Call mockCall = mock(Call.class);
        Response mockResponse = mock(Response.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.code()).thenReturn(200);
        when(mockResponse.body()).thenReturn(null);

        Appointment actual = (appointmentsBridge.getUnseenAppointment("default"));
        Assert.assertNull(actual);
    }

    @Test
    public void testGetUnseenAppointmentReturnsNullOnNullPointerException() {
        when(mockClient.newCall(any(Request.class))).thenReturn(null);

        Appointment actual = (appointmentsBridge.getUnseenAppointment("default"));
        Assert.assertNull(actual);
    }

    @Test
    public void testGetUnseenAppointmentReturnsNullOnIOException() throws IOException {
        Call mockCall = mock(Call.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenThrow(new IOException());

        Appointment actual = (appointmentsBridge.getUnseenAppointment("default"));
        Assert.assertNull(actual);
    }
}
