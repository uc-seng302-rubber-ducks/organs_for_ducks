package odms.controller.gui.panel.logic;

import javafx.collections.ObservableList;
import odms.bridge.AppointmentsBridge;
import odms.commons.model.Appointment;
import odms.commons.model._enum.AppointmentStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;


public class ClinicianAppointmentRequestLogicController {

    private static final int ROWS_PER_PAGE = 30;
    private int startingIndex = 0;
    private ObservableList<Appointment> availableAppointments;

    public ClinicianAppointmentRequestLogicController(ObservableList<Appointment> availableAppointment) {
        this.availableAppointments = availableAppointment;

    }

    /**
     * Goes to the previous page in the available organs table.
     */
    public void goPrevPage() {
        if (startingIndex - ROWS_PER_PAGE < 0) {
            return;
        }

        startingIndex = startingIndex - ROWS_PER_PAGE;
        search(startingIndex);
    }

    /**
     * Goes to the next page in the available organs table
     */
    public void goNextPage() {
        if (availableAppointments.size() < ROWS_PER_PAGE) {
            return;
        }

        startingIndex = startingIndex + ROWS_PER_PAGE;
        search(startingIndex);
    }

    /**
     *
     */
    public void search(int startingIndex) {
        //TODO this

    }

    /**
     * Method to make a request to the server to accept the appointment for the given time.
     *
     * @param selectedAppointment appointment to update
     */
    public void acceptAppointment(Appointment selectedAppointment, String time, AppointmentsBridge appointmentsBridge) {
        String[] timeParts = time.split(":");
        selectedAppointment.setRequestedDate(LocalDateTime.of(selectedAppointment.getRequestedDate().toLocalDate(), LocalTime.of(Integer.valueOf(timeParts[0]), Integer.valueOf(timeParts[1]))));
        selectedAppointment.setAppointmentStatus(AppointmentStatus.ACCEPTED);
        appointmentsBridge.putAppointment(selectedAppointment);
    }
}
