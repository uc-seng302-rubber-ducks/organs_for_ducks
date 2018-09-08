package odms.controller.gui.popup;

import odms.commons.model.Appointment;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.popup.utils.AlertWindowFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Handles the alert creation for when there is an appointment status update the user should know about.
 * Makes appropriate changes to the appointment once the user hsa seen the alert.
 */
public class UserAppointmentAlertController {

    private AppController controller;

    public void setAppController(AppController controller) {
        this.controller = controller;
    }

    /**
     * Asks the server if there is an appointment with an unseen update for the specified user
     * @param userId Id of the user to check for unseen updates for
     */
    public void checkForUnseenUpdates(String userId) {
        List<Appointment> appointments = new ArrayList<>(controller.getAppointmentsBridge().getUnseenAppointment(userId));
        if (!appointments.isEmpty()) {
            createAlert(appointments.get(0));
        }
    }

    /**
     * Creates an alert window based on the data in the appointment object. It then updates the appointment's seen status
     * If a message "ABORT" is returned, then an incorrect appointment status was retrieved from the database.
     * @param appointment Appointment to create an alert about
     */
    public void createAlert(Appointment appointment) {

        String message = createMessage(appointment);
        if (!message.equals("ABORT")) {
            AlertWindowFactory.generateAlertWindow(message);
            updateAppointmentSeenStatus(appointment);
        } else {
            Log.info("The alert creation was successfully aborted");
        }
    }

    /**
     * Creates a message to go in the alert window. The contents change based on the category, time, and status of the appointment
     * @param appointment Appointment to create the message about
     * @return A string describing the update to the requested appointment
     */
    private String createMessage(Appointment appointment) {
        String messageEnd;
        if (appointment.getAppointmentStatus() == AppointmentStatus.ACCEPTED) {
            messageEnd = "accepted. The time of your appointment is " + appointment.getRequestedDate().toLocalTime() + ".";


        } else if (appointment.getAppointmentStatus() == AppointmentStatus.REJECTED) {
            messageEnd = "rejected. The clinician gave this reason for rejection: \"" + appointment.getRequestDescription() + "\".";
            //Uses the description of the appointment here because haven't actually got a way to store a clinician's reason for rejection yet.

        } else {
            Log.warning("A notification attempt about an appointment with an incorrect status was made");
            return "ABORT";
        }


        return "Your appointment request for a \"" + appointment.getAppointmentCategory().toString() +
                "\" on the " + appointment.getRequestedDate().toLocalDate() +
                " was " + messageEnd;
    }

    /**
     * Updates the status of an appointment to _SEEN based on whether is was accepted or rejected.
     * @param appointment Appointment to update the status of
     */
    private void updateAppointmentSeenStatus(Appointment appointment) {
        if (appointment.getAppointmentStatus() == AppointmentStatus.ACCEPTED) {
            appointment.setAppointmentStatus(AppointmentStatus.ACCEPTED_SEEN);
            controller.getAppointmentsBridge().patchAppointmentStatus(appointment.getAppointmentId(), AppointmentStatus.ACCEPTED_SEEN.getDbValue());
        } else if (appointment.getAppointmentStatus() == AppointmentStatus.REJECTED) {
            appointment.setAppointmentStatus(AppointmentStatus.REJECTED_SEEN);
            controller.getAppointmentsBridge().patchAppointmentStatus(appointment.getAppointmentId(), AppointmentStatus.REJECTED_SEEN.getDbValue());
        }
    }

}
