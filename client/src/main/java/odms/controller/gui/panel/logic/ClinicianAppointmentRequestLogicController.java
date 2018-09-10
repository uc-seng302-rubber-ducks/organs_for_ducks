package odms.controller.gui.panel.logic;

import javafx.collections.ObservableList;
import odms.commons.model.Appointment;


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
}
