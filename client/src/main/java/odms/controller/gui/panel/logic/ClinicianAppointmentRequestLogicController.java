package odms.controller.gui.panel.logic;

import javafx.collections.ObservableList;
import odms.commons.model.Appointment;
import odms.commons.model.Clinician;
import odms.commons.model._enum.EventTypes;
import odms.commons.model.event.UpdateNotificationEvent;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.socket.ServerEventNotifier;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class ClinicianAppointmentRequestLogicController implements PropertyChangeListener {

    private static final int ROWS_PER_PAGE = 30;
    private int startingIndex = 0;
    private ObservableList<Appointment> availableAppointments;
    private AppController appController;
    private Clinician clinician;

    public ClinicianAppointmentRequestLogicController(ObservableList<Appointment> availableAppointment, AppController controller, Clinician clinician) {
        this.availableAppointments = availableAppointment;
        this.appController = controller;
        this.clinician = clinician;
        ServerEventNotifier.getInstance().addPropertyChangeListener(this);
    }

    /**
     * Launches the pop-up to view a requested appointment and choose a time for it before accepting
     */
    public void launchAcceptedPopup() {
        Log.warning("Not implemented yet");
    }

    /**
     * Launches the pop-up to view a requested appointment and enter a rejection reason before rejecting it.
     */
    public void launchRejectedPopup() {
        Log.warning("Not implemented yet");
    }

    public void updateTable(int startIndex) {
        availableAppointments.clear();
        appController.getAppointmentsBridge().getClinicianAppointments(startIndex, ROWS_PER_PAGE, clinician.getStaffId(), appController.getToken(), availableAppointments);
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
     * Handles events fired by appointments that are being listened to
     * The user's appointments table will be updated when the given event is appropriate
     *
     * @param evt PropertyChangeEvent to be handled
     * @see UpdateNotificationEvent
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        UpdateNotificationEvent event;
        try {
            event = (UpdateNotificationEvent) evt;
        } catch (ClassCastException ex) {
            return;
        }
        if (event == null) {
            return;
        }
        if (event.getType().equals(EventTypes.APPOINTMENT_UPDATE)) {
            updateTable(startingIndex);
        }
    }

}
