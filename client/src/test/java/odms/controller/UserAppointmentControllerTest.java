package odms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import odms.TestUtils.AppControllerMocker;
import odms.bridge.AppointmentsBridge;
import odms.commons.config.ConfigPropertiesSession;
import odms.commons.model.Appointment;
import odms.commons.model.User;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.controller.gui.panel.logic.UserAppointmentLogicController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class UserAppointmentControllerTest {

    private ObservableList<Appointment> appointments;
    private AppController controller = AppControllerMocker.getFullMock();
    private OkHttpClient client = mock(OkHttpClient.class);
    private Call call = mock(Call.class);
    private UserAppointmentLogicController userAppointmentLogicController;


    @Before
    public void setUp() {
        ConfigPropertiesSession mockSession = mock(ConfigPropertiesSession.class);
        when(mockSession.getProperty(eq("server.url"))).thenReturn("http://test.url");
        when(mockSession.getProperty(eq("server.url"), anyString())).thenReturn("http://test.url");
        when(mockSession.getProperty(eq("server.token.header"), anyString())).thenReturn("x-auth-token");
        when(mockSession.getProperty(eq("server.token.header"))).thenReturn("x-auth-token");

        ConfigPropertiesSession.setInstance(mockSession);
        AppController.setInstance(controller);

        appointments = FXCollections.observableList(new ArrayList<>());
        User testUser = new User("Jeff", LocalDate.parse("9/11/1997", DateTimeFormatter.ofPattern("d/M/yyyy")), "ABC1234");
        userAppointmentLogicController = spy(new UserAppointmentLogicController(appointments, testUser));
        AppointmentsBridge appointmentsBridge = new AppointmentsBridge(client);

        when(controller.getAppointmentsBridge()).thenReturn(appointmentsBridge);
        when(client.newCall(any(Request.class))).thenReturn(call);
    }

    @After
    public void tearDown() {
        AppController.setInstance(null);
        ConfigPropertiesSession.setInstance(null);

    }

    @Test
    public void testGoNextPageNoPages() {
        doNothing().when(call).enqueue(any(Callback.class));
        appointments.add(new Appointment());
        userAppointmentLogicController.goToNextPage();

        assertTrue(appointments.size() == 1);
        verify(controller, times(0)).getAppointmentsBridge();
    }

    @Test
    public void testGoPrevPageNoPages() {
        doNothing().when(call).enqueue(any(Callback.class));
        appointments.add(new Appointment());
        userAppointmentLogicController.goToPreviousPage();

        assertTrue(appointments.size() == 1);
        verify(controller, times(0)).getAppointmentsBridge();
    }

    @Test
    public void testCancelAppointment() {
        LocalDateTime testDate = LocalDateTime.now().plusDays(5);
        Appointment testAppointment = new Appointment("ABC1234", "0",
                AppointmentCategory.GENERAL_CHECK_UP, testDate, "", AppointmentStatus.PENDING);

        appointments.add(testAppointment);
        doReturn(Optional.of(ButtonType.OK)).when(userAppointmentLogicController).confirmOption(anyString());
        userAppointmentLogicController.cancelAppointment(testAppointment);

        verify(controller, times(1)).getAppointmentsBridge();
    }

    @Test
    public void testCancelNoResult() {
        LocalDateTime testDate = LocalDateTime.now().plusDays(5);
        Appointment testAppointment = new Appointment("ABC1234", "0",
                AppointmentCategory.GENERAL_CHECK_UP, testDate, "", AppointmentStatus.PENDING);

        appointments.add(testAppointment);
        doReturn(Optional.empty()).when(userAppointmentLogicController).confirmOption(anyString());
        userAppointmentLogicController.cancelAppointment(testAppointment);

        assertTrue(appointments.size() == 1);
        verify(controller, times(0)).getAppointmentsBridge();
    }

    @Test
    public void testCancelNoConfirmation() {
        LocalDateTime testDate = LocalDateTime.now().plusDays(5);
        Appointment testAppointment = new Appointment("ABC1234", "0",
                AppointmentCategory.GENERAL_CHECK_UP, testDate, "", AppointmentStatus.PENDING);

        appointments.add(testAppointment);

        doReturn(Optional.of(ButtonType.CANCEL)).when(userAppointmentLogicController).confirmOption(anyString());
        userAppointmentLogicController.cancelAppointment(testAppointment);

        assertTrue(appointments.size() == 1);
        verify(controller, times(0)).getAppointmentsBridge();
    }

    @Test
    public void testCancelFailsDateCheck() {
        LocalDateTime testDate = LocalDateTime.now();
        Appointment testAppointment = new Appointment("ABC1234", "0",
                AppointmentCategory.GENERAL_CHECK_UP, testDate, "", AppointmentStatus.PENDING);

        appointments.add(testAppointment);
        doNothing().when(userAppointmentLogicController).alertUser(anyString());

        userAppointmentLogicController.cancelAppointment(testAppointment);

        assertTrue(appointments.size() == 1);
        verify(controller, times(0)).getAppointmentsBridge();
    }
}
