package odms.controller;

import odms.commons.model.Appointment;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.utils.Log;
import odms.controller.gui.popup.UserAppointmentAlertController;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;

public class UserAppointmentAlertControllerTest {

    private AppController controller;
    private UserAppointmentAlertController alertController;
    private Appointment testAppointment;

    @Before
    public void setUpTest() {
        controller = mock(AppController.class);
        AppController.setInstance(controller);

        alertController = new UserAppointmentAlertController();
        alertController.setAppController(controller);
        testAppointment = new Appointment("ABC1234", "0", AppointmentCategory.BLOOD_TEST, LocalDateTime.now(), "TEST", AppointmentStatus.ACCEPTED);

    }

    @Test
    public void testCreateAlert_SuccessfullyAborts_OnIncorrectStatus() {
        testAppointment.setAppointmentStatus(AppointmentStatus.PENDING);
        alertController.createAlert(testAppointment);
        List<String> logs = Log.getDebugLogs();
        System.out.println(logs);
        //Yo if I want to test something by checking that a Log has been written, how do I do that? I'm pretty I've seen it in code before but can't find it again
    }

}
