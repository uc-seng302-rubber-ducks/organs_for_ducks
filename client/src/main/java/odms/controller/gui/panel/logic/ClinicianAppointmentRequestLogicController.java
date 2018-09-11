package odms.controller.gui.panel.logic;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odms.commons.model.Appointment;
import odms.commons.model.Clinician;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.model._enum.EventTypes;
import odms.commons.model.event.UpdateNotificationEvent;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.popup.view.RejectAppointmentReasonViewController;
import odms.socket.ServerEventNotifier;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;


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
     * Calls the server to get updated appointment entries
     * @param startIndex index to display entries from (eg. 60 will display entries 60 to 60+ROWS_PER_PAGE)
     */
    public void updateTable(int startIndex) {
        availableAppointments.clear();
        appController.getAppointmentsBridge().getClinicianAppointments(startIndex, ROWS_PER_PAGE, clinician.getStaffId(), appController.getToken(), availableAppointments);
    }

    /**
     * Goes to the previous page in the available organs table.
     */
    public void goToPreviousPage() {
        if (startingIndex - ROWS_PER_PAGE < 0) {
            return;
        }

        startingIndex = startingIndex - ROWS_PER_PAGE;
        updateTable(startingIndex);
    }

    /**
     * Goes to the next page in the available organs table
     */
    public void goToNextPage() {
        if (availableAppointments.size() < ROWS_PER_PAGE) {
            return;
        }

        startingIndex = startingIndex + ROWS_PER_PAGE;
        updateTable(startingIndex);
    }

    /**
     * @param selectedAppointment
     */
    public void rejectAppointment(Appointment selectedAppointment) {
        AppointmentStatus status = selectedAppointment.getAppointmentStatus();

        if (status == AppointmentStatus.PENDING) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/appointmentRejection.fxml"));
            Stage rejectionStage = new Stage();
            Parent root;
            try {
                root = loader.load();
                RejectAppointmentReasonViewController rejectionController = loader.getController();
                rejectionStage.setScene(new Scene(root));

                rejectionController.init(selectedAppointment, rejectionStage);
                rejectionStage.show();
            } catch (IOException e) {
                Log.severe("failed to load login window FXML", e);
            }
        } else if (status == AppointmentStatus.ACCEPTED || status == AppointmentStatus.ACCEPTED_SEEN) {
            cancelAppointment(selectedAppointment);
        }
    }

    /**
     *
     * @param appointment
     */
    private void cancelAppointment(Appointment appointment) {

    }

    /**
     *
     * @param selectedAppointment
     */
    public void acceptAppointment(Appointment selectedAppointment) {
        AppointmentStatus status = selectedAppointment.getAppointmentStatus();
        if (status == AppointmentStatus.ACCEPTED || status == AppointmentStatus.ACCEPTED_SEEN) {
            updateAppointment(selectedAppointment);
        }
    }

    /**
     *
     * @param appointment
     */
    private void updateAppointment(Appointment appointment) {

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
