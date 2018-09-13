package odms.controller.gui.panel.logic;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import odms.bridge.AppointmentsBridge;
import odms.commons.model.Appointment;
import odms.commons.model.Clinician;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.model._enum.EventTypes;
import odms.commons.model.event.UpdateNotificationEvent;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.popup.utils.AlertWindowFactory;
import odms.controller.gui.popup.view.RejectAppointmentReasonViewController;
import odms.socket.ServerEventNotifier;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;


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
     * Displays the pop-up for rejecting a pending appointment.
     *
     * @param selectedAppointment The appointment to be rejected
     */
    public void rejectAppointment(Appointment selectedAppointment) {
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
    }

    /**
     * Cancels the given appointment if it has already been accepted.
     *
     * @param appointment Appointment to be cancelled
     */
    public void cancelAppointment(Appointment appointment) {
        if (appointment.getAppointmentStatus() != AppointmentStatus.ACCEPTED && appointment.getAppointmentStatus() != AppointmentStatus.ACCEPTED_SEEN) {
            alertClinician("You cannot cancel this type of appointment");
            return;
        }

        if (appointment.getRequestedDate().minusDays(1).isBefore(LocalDateTime.now())) {
            alertClinician("You cannot cancel this appointment as it is within 24 hours of the scheduled time");
            return;
        }

        Optional<ButtonType> result = confirmOption("Are you sure you want to delete this appointment?");

        if (!result.isPresent()) {
            return;
        }

        if (result.get() == ButtonType.OK) {
            appointment.setAppointmentStatus(AppointmentStatus.CANCELLED_BY_CLINICIAN);
            AppController.getInstance().getAppointmentsBridge().patchAppointmentStatus(appointment.getAppointmentId(),
                    AppointmentStatus.CANCELLED_BY_CLINICIAN.getDbValue());
        }
    }

    /**
     * Alerts the clinician with an alert window containing the given message
     *
     * @param message message to display to the user.
     */
    public void alertClinician(String message) {
        AlertWindowFactory.generateError(message);
    }

    /**
     * Creates a confirmation alert pop-up with the given message
     * Extracted for easier testability
     *
     * @return the confirmation alert window result
     */
    public Optional<ButtonType> confirmOption(String message) {
        return AlertWindowFactory.generateConfirmation(message);
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
        appointmentsBridge.putAppointment(selectedAppointment, AppController.getInstance().getToken());
    }

    /**
     * Updates the given appointment to the new values given
     *
     * @param appointment Appointment to be updated
     */
    public void updateAppointment(Appointment appointment, AppointmentCategory category, LocalDate date, String time, String description) {
        String[] timeParts = time.split(":");
        LocalTime localTime = LocalTime.of(Integer.valueOf(timeParts[0]), Integer.valueOf(timeParts[1]));

        appointment.setRequestedDate(LocalDateTime.of(date, localTime));
        appointment.setAppointmentCategory(category);
        appointment.setRequestDescription(description);
        AppController.getInstance().getAppointmentsBridge().putAppointment(appointment, AppController.getInstance().getToken());
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
