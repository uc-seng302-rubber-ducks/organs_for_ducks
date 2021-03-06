package odms.controller;

import odms.TestUtils.AppControllerMocker;
import odms.commons.model.Appointment;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.model._enum.UserType;
import odms.controller.gui.popup.UserAppointmentAlertController;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserAppointmentAlertControllerTest {

    private AppController controller;
    private UserAppointmentAlertController alertController;
    private Appointment testAppointment;

    @Before
    public void setUpTest() {
        controller = AppControllerMocker.getFullMock();
        AppController.setInstance(controller);

        alertController = spy(new UserAppointmentAlertController());
        alertController.setAppController(controller);
        testAppointment = new Appointment("ABC1234", "0", AppointmentCategory.BLOOD_TEST, LocalDateTime.now(), "TEST", AppointmentStatus.ACCEPTED);
    }

    @Test
    public void testCreateAlertWhenUnseenAcceptedAndCancelledAppointments() {
        when(controller.getAppointmentsBridge().getUnseenAppointment(anyString())).thenReturn(testAppointment);
        when(controller.getAppointmentsBridge().checkAppointmentStatusExists(anyString(), eq(UserType.USER), eq(AppointmentStatus.CANCELLED_BY_CLINICIAN))).thenReturn(true);
        doNothing().when(alertController).generateAlertWindow(anyString());

        alertController.checkForUnseenUpdates("");
        verify(alertController, times(1)).generateAlertWindow(anyString());
    }

    @Test
    public void testCreateAlertWhenUnseenAcceptedAndNoCancelledAppointments() {
        when(controller.getAppointmentsBridge().getUnseenAppointment(anyString())).thenReturn(testAppointment);
        when(controller.getAppointmentsBridge().checkAppointmentStatusExists(anyString(), eq(UserType.USER), eq(AppointmentStatus.CANCELLED_BY_CLINICIAN))).thenReturn(false);
        doNothing().when(alertController).generateAlertWindow(anyString());

        alertController.checkForUnseenUpdates("");
        verify(alertController, times(1)).generateAlertWindow(anyString());
    }

    @Test
    public void testCreateAlertWhenUnseenRejectedAndCancelledAppointments() {
        testAppointment.setAppointmentStatus(AppointmentStatus.REJECTED);
        when(controller.getAppointmentsBridge().getUnseenAppointment(anyString())).thenReturn(testAppointment);
        when(controller.getAppointmentsBridge().checkAppointmentStatusExists(anyString(), eq(UserType.USER), eq(AppointmentStatus.CANCELLED_BY_CLINICIAN))).thenReturn(true);
        doNothing().when(alertController).generateAlertWindow(anyString());

        alertController.checkForUnseenUpdates("");
        verify(alertController, times(1)).generateAlertWindow(anyString());
    }

    @Test
    public void testCreateAlertWhenUnseenRejectedAndNoCancelledAppointments() {
        testAppointment.setAppointmentStatus(AppointmentStatus.REJECTED);
        when(controller.getAppointmentsBridge().getUnseenAppointment(anyString())).thenReturn(testAppointment);
        when(controller.getAppointmentsBridge().checkAppointmentStatusExists(anyString(), eq(UserType.USER), eq(AppointmentStatus.CANCELLED_BY_CLINICIAN))).thenReturn(false);
        doNothing().when(alertController).generateAlertWindow(anyString());

        alertController.checkForUnseenUpdates("");
        verify(alertController, times(1)).generateAlertWindow(anyString());
    }

    @Test
    public void testCreateAlertWhenCancelledAppointmentsAndNoUnseen() {
        when(controller.getAppointmentsBridge().getUnseenAppointment(anyString())).thenReturn(null);
        when(controller.getAppointmentsBridge().checkAppointmentStatusExists(anyString(), eq(UserType.USER), eq(AppointmentStatus.CANCELLED_BY_CLINICIAN))).thenReturn(true);
        doNothing().when(alertController).generateAlertWindow(anyString());

        alertController.checkForUnseenUpdates("");
        verify(alertController, times(1)).generateAlertWindow(anyString());
    }

    @Test
    public void testNoAlertCreatedWhenNoCancelledAndNoUnseen() {
        when(controller.getAppointmentsBridge().getUnseenAppointment(anyString())).thenReturn(null);
        when(controller.getAppointmentsBridge().checkAppointmentStatusExists(anyString(), eq(UserType.USER), eq(AppointmentStatus.CANCELLED_BY_CLINICIAN))).thenReturn(false);
        doNothing().when(alertController).generateAlertWindow(anyString());

        alertController.checkForUnseenUpdates("");
        verify(alertController, times(0)).generateAlertWindow(anyString());
    }

    @Test
    public void testCreateAlertWhenCancelledAppointmentsAndIncorrectUnseenType() {
        testAppointment.setAppointmentStatus(AppointmentStatus.PENDING);
        when(controller.getAppointmentsBridge().getUnseenAppointment(anyString())).thenReturn(testAppointment);
        when(controller.getAppointmentsBridge().checkAppointmentStatusExists(anyString(), eq(UserType.USER), eq(AppointmentStatus.CANCELLED_BY_CLINICIAN))).thenReturn(true);
        doNothing().when(alertController).generateAlertWindow(anyString());

        alertController.checkForUnseenUpdates("");
        verify(alertController, times(1)).generateAlertWindow(anyString());
    }

    @Test
    public void testNoAlertCreatedWhenIncorrectUnseenType() {
        testAppointment.setAppointmentStatus(AppointmentStatus.PENDING);
        when(controller.getAppointmentsBridge().getUnseenAppointment(anyString())).thenReturn(testAppointment);
        when(controller.getAppointmentsBridge().checkAppointmentStatusExists(anyString(), eq(UserType.USER), eq(AppointmentStatus.CANCELLED_BY_CLINICIAN))).thenReturn(false);
        doNothing().when(alertController).generateAlertWindow(anyString());

        alertController.checkForUnseenUpdates("");
        verify(alertController, times(0)).generateAlertWindow(anyString());
    }

}
