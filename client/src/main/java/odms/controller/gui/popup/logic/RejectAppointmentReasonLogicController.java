package odms.controller.gui.popup.logic;

import odms.bridge.AppointmentsBridge;
import odms.commons.model.Appointment;
import odms.commons.model._enum.AppointmentStatus;

public class RejectAppointmentReasonLogicController {
    /**
     * Method to reject the appointment with an rejection reason, and send a server request to update it
     *
     * @param appointment        appointment to reject
     * @param rejectionReason    the reason for rejection
     * @param appointmentsBridge A non null bridge for making a request to the server
     */
    public void rejectAppointment(Appointment appointment, String rejectionReason, AppointmentsBridge appointmentsBridge) {
        appointment.setAppointmentStatus(AppointmentStatus.REJECTED);
        appointment.setRejectionReason(rejectionReason);
        appointmentsBridge.putAppointment(appointment);
    }
}
