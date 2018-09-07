package odms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import odms.TestUtils.AppControllerMocker;
import odms.bridge.AppointmentsBridge;
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
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
        appointments = FXCollections.observableList(new ArrayList<>());
        User testUser = new User("Jeff", LocalDate.parse("9/11/1997", DateTimeFormatter.ofPattern("d/M/yyyy")), "ABC1234");
        userAppointmentLogicController = new UserAppointmentLogicController(appointments, controller, testUser);
        AppointmentsBridge appointmentsBridge = new AppointmentsBridge(client);

        when(controller.getAppointmentsBridge()).thenReturn(appointmentsBridge);
        when(client.newCall(any(Request.class))).thenReturn(call);

        AppController.setInstance(controller);
    }

    @After
    public void tearDown() {
        AppController.setInstance(null);
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

    @Ignore // todo: make test when cancel functionality is implemented
    @Test
    public void testCancelAppointment() {

    }

    @Ignore // cannot figure out how to mock the alert window
    @Test
    public void testCannotCancelAppointment() {
        LocalDateTime testDate = LocalDateTime.now();
        Appointment testAppointment = new Appointment("ABC1234", "0",
                AppointmentCategory.GENERAL_CHECK_UP, testDate, "", AppointmentStatus.PENDING);

        appointments.add(testAppointment);
        userAppointmentLogicController.cancelAppointment(testAppointment);

        assertTrue(appointments.size() == 1);
        verify(controller, times(0)).getAppointmentsBridge();
    }

}
