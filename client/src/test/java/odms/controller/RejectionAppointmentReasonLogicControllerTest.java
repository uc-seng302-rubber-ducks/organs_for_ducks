package odms.controller;

import odms.bridge.AppointmentsBridge;
import odms.commons.model.Appointment;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.controller.gui.popup.logic.RejectAppointmentReasonLogicController;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RejectionAppointmentReasonLogicControllerTest {
    private AppointmentsBridge appointmentsBridge = mock(AppointmentsBridge.class);
    private RejectAppointmentReasonLogicController controller = new RejectAppointmentReasonLogicController();
    private Appointment appointment;

    @Before
    public void setUp() {
        appointment = new Appointment("ABC1234", "0", AppointmentCategory.GENERAL_CHECK_UP, LocalDateTime.of(2018, 5, 20, 15, 30), "Halp", AppointmentStatus.PENDING);
        doNothing().when(appointmentsBridge).putAppointment(any(Appointment.class), anyString());
    }

    @Test
    public void rejectAppointmentTest() {
        controller.rejectAppointment(appointment, "he ded", appointmentsBridge);
        verify(appointmentsBridge, atLeastOnce()).putAppointment(any(Appointment.class), anyString());
    }
}
