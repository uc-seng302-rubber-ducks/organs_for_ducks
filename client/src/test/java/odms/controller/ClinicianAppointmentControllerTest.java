package odms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import odms.TestUtils.AppControllerMocker;
import odms.bridge.AppointmentsBridge;
import odms.commons.config.ConfigPropertiesSession;
import odms.commons.model.Appointment;
import odms.commons.model.Clinician;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.controller.gui.panel.logic.ClinicianAppointmentRequestLogicController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class ClinicianAppointmentControllerTest {

    private ObservableList<Appointment> appointments;
    private ObservableList<LocalTime> availableTimes;
    private AppController controller = AppControllerMocker.getFullMock();
    private OkHttpClient client = mock(OkHttpClient.class);
    private Call call = mock(Call.class);
    private ClinicianAppointmentRequestLogicController clinicianAppointmentRequestLogicController;
    private AppointmentsBridge appointmentsBridge;


    @Before
    public void setUp() {
        ConfigPropertiesSession mockSession = mock(ConfigPropertiesSession.class);
        when(mockSession.getProperty(eq("server.url"))).thenReturn("http://test.url");
        when(mockSession.getProperty(eq("server.url"), anyString())).thenReturn("http://test.url");
        when(mockSession.getProperty(eq("server.token.header"), anyString())).thenReturn("x-auth-token");
        when(mockSession.getProperty(eq("server.token.header"))).thenReturn("x-auth-token");

        appointments = FXCollections.observableList(new ArrayList<>());
        availableTimes = FXCollections.observableList(new ArrayList<>());
        Clinician testClinician = new Clinician("default", "0", "password");
        clinicianAppointmentRequestLogicController = spy(new ClinicianAppointmentRequestLogicController(appointments, controller, testClinician, availableTimes));


        appointmentsBridge = spy(new AppointmentsBridge(client));

        when(controller.getToken()).thenReturn("token");
        doNothing().when(appointmentsBridge).putAppointment(any(Appointment.class), anyString());
        when(controller.getAppointmentsBridge()).thenReturn(appointmentsBridge);
        when(client.newCall(any(Request.class))).thenReturn(call);
        doNothing().when(call).enqueue(any(Callback.class));

        ConfigPropertiesSession.setInstance(mockSession);
        AppController.setInstance(controller);
    }

    @After
    public void tearDown() {
        AppController.setInstance(null);
        ConfigPropertiesSession.setInstance(null);
    }

    @Test
    public void testGoNextPageNoPages() {
        appointments.add(new Appointment());
        clinicianAppointmentRequestLogicController.goToNextPage();

        assertTrue(appointments.size() == 1);
        verify(controller, times(0)).getAppointmentsBridge();
    }

    @Test
    public void testGoPrevPageNoPages() {
        appointments.add(new Appointment());
        clinicianAppointmentRequestLogicController.goToPreviousPage();

        assertTrue(appointments.size() == 1);
        verify(controller, times(0)).getAppointmentsBridge();
    }

    @Test
    public void testAcceptAppointment() {
        LocalDate testDate = LocalDate.now().plusDays(5);
        String testTime = "14:00";
        AppointmentCategory testCategory = AppointmentCategory.HEALTH_ADVICE;
        Appointment testAppointment = new Appointment();

        clinicianAppointmentRequestLogicController.updateAppointment(testAppointment, testCategory, testDate, testTime, "description", true);

        assertEquals(testCategory, testAppointment.getAppointmentCategory());
        assertEquals("description", testAppointment.getRequestDescription());
        verify(controller, times(1)).getAppointmentsBridge();
    }

    @Test
    public void testUpdateAppointment() {
        LocalDate testDate = LocalDate.now().plusDays(5);
        String testTime = "14:00";
        AppointmentCategory testCategory = AppointmentCategory.HEALTH_ADVICE;
        Appointment testAppointment = new Appointment();

        clinicianAppointmentRequestLogicController.updateAppointment(testAppointment, testCategory, testDate, testTime, "description", false);

        assertEquals(testCategory, testAppointment.getAppointmentCategory());
        assertEquals("description", testAppointment.getRequestDescription());
        verify(controller, times(1)).getAppointmentsBridge();
    }

    @Ignore // todo fix this - something is probably not mocked correclty, the ip is null
    @Test
    public void testCancelAppointmentSuccessfully() {
        LocalDateTime testDate = LocalDateTime.now().plusDays(5);
        Appointment testAppointment = new Appointment("ABC1234", "0",
                AppointmentCategory.GENERAL_CHECK_UP, testDate, "", AppointmentStatus.ACCEPTED);

        testAppointment.setAppointmentId(1);
        appointments.add(testAppointment);
        doNothing().when(clinicianAppointmentRequestLogicController).alertClinician(anyString());
        doReturn(Optional.of(ButtonType.OK)).when(clinicianAppointmentRequestLogicController).confirmOption(anyString());
        doNothing().when(appointmentsBridge).patchAppointmentStatus(1, AppointmentStatus.ACCEPTED.getDbValue());
        clinicianAppointmentRequestLogicController.cancelAppointment(testAppointment);

        int testStatus = AppointmentStatus.CANCELLED_BY_CLINICIAN.getDbValue();
        assertEquals(testStatus, testAppointment.getAppointmentStatus().getDbValue());
        //verify(appointmentsBridge, times(1)).patchAppointmentStatus(1, testStatus);
    }

    @Test
    public void testClinicianAppointmentCancelWithWrongStatus() {
        Appointment testAppointment = new Appointment();
        appointments.add(testAppointment);
        doNothing().when(clinicianAppointmentRequestLogicController).alertClinician(anyString());

        clinicianAppointmentRequestLogicController.cancelAppointment(testAppointment);
        int testStatus = AppointmentStatus.CANCELLED_BY_CLINICIAN.getDbValue();

        assertTrue(appointments.size() == 1);
        verify(appointmentsBridge, times(0)).patchAppointmentStatus(1, testStatus);
    }

    @Test
    public void testClinicianAppointmentCancelWithInvalidDate() {
        LocalDateTime testDate = LocalDateTime.now();
        Appointment testAppointment = new Appointment("ABC1234", "0",
                AppointmentCategory.GENERAL_CHECK_UP, testDate, "", AppointmentStatus.ACCEPTED);

        appointments.add(testAppointment);
        doNothing().when(clinicianAppointmentRequestLogicController).alertClinician(anyString());
        clinicianAppointmentRequestLogicController.cancelAppointment(testAppointment);
        int testStatus = AppointmentStatus.CANCELLED_BY_CLINICIAN.getDbValue();

        assertTrue(appointments.size() == 1);
        verify(appointmentsBridge, times(0)).patchAppointmentStatus(1, testStatus);
    }

    @Test
    public void testClinicianAppointmentCancelWithNoAlertResult() {
        LocalDateTime testDate = LocalDateTime.now().plusDays(5);
        Appointment testAppointment = new Appointment("ABC1234", "0",
                AppointmentCategory.GENERAL_CHECK_UP, testDate, "", AppointmentStatus.ACCEPTED);

        appointments.add(testAppointment);
        doReturn(Optional.empty()).when(clinicianAppointmentRequestLogicController).confirmOption(anyString());
        clinicianAppointmentRequestLogicController.cancelAppointment(testAppointment);
        int testStatus = AppointmentStatus.CANCELLED_BY_CLINICIAN.getDbValue();

        assertTrue(appointments.size() == 1);
        verify(appointmentsBridge, times(0)).patchAppointmentStatus(1, testStatus);
    }

    @Test
    public void testClinicianAppointmentCancelWithNoConfirmation() {
        LocalDateTime testDate = LocalDateTime.now().plusDays(5);
        Appointment testAppointment = new Appointment("ABC1234", "0",
                AppointmentCategory.GENERAL_CHECK_UP, testDate, "", AppointmentStatus.ACCEPTED);

        appointments.add(testAppointment);
        doReturn(Optional.of(ButtonType.CANCEL)).when(clinicianAppointmentRequestLogicController).confirmOption(anyString());
        clinicianAppointmentRequestLogicController.cancelAppointment(testAppointment);
        int testStatus = AppointmentStatus.CANCELLED_BY_CLINICIAN.getDbValue();

        assertTrue(appointments.size() == 1);
        verify(appointmentsBridge, times(0)).patchAppointmentStatus(1, testStatus);
    }


}
