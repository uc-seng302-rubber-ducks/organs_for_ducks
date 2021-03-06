package odms.controller.gui.panel.logic;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import odms.commons.model.Appointment;
import odms.commons.model.User;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.model._enum.EventTypes;
import odms.commons.model._enum.UserType;
import odms.commons.model.event.UpdateNotificationEvent;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.popup.utils.AlertWindowFactory;
import odms.controller.gui.popup.view.AppointmentPickerViewController;
import odms.socket.ServerEventNotifier;
import utils.StageIconLoader;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserAppointmentLogicController implements PropertyChangeListener {

    private static final int ROWS_PER_PAGE = 30;
    private User user;
    private ObservableList<Appointment> appointments;
    private int startingIndex = 0;

    /**
     * Constructor to create a new logical instance of the controller
     *
     * @param appointments  Observable list of appointments used to populate the users appointments table
     * @param user          User that the appointment panel belongs to
     */
    public UserAppointmentLogicController(ObservableList<Appointment> appointments, User user) {
        this.appointments = appointments;
        this.user = user;
        ServerEventNotifier.getInstance().addPropertyChangeListener(this);
    }


    /**
     * Launches the pop-up to create and view requested appointments in more detail
     */
    public void launchAppointmentPicker() {
        if (AppController.getInstance().getAppointmentsBridge().checkAppointmentStatusExists(user.getNhi(), UserType.USER, AppointmentStatus.PENDING)) {
            alertUser("You cannot request a new appointment as you already have one pending approval.");
            return;
        }

        FXMLLoader appointmentRequestLoader = new FXMLLoader(getClass().getResource("/FXML/appointmentPicker.fxml"));
        Parent root;

        try {
            root = appointmentRequestLoader.load();
            AppointmentPickerViewController appointmentPickerViewController = appointmentRequestLoader.getController();
            Stage appointmentPickerStage = new Stage();
            appointmentPickerViewController.init(user, appointmentPickerStage);
            appointmentPickerStage.setScene(new Scene(root));
            StageIconLoader stageIconLoader = new StageIconLoader();
            appointmentPickerStage = stageIconLoader.addStageIcon(appointmentPickerStage);
            appointmentPickerStage.showAndWait();
            Log.info("Successfully launched the appointment picker pop-up window for user: " + user.getNhi());

        } catch (IOException e) {
            Log.severe("Failed to load appointmentPicker pop-up window for user: " + user.getNhi(), e);
        }
    }

    /**
     * Prompts the user with a confirmation pop-up to cancel the given appointment
     *
     * @param appointment The appointment to be cancelled
     */
    public void cancelAppointment(Appointment appointment) {
        AppointmentStatus status = appointment.getAppointmentStatus();

        if (!(status == AppointmentStatus.ACCEPTED || status == AppointmentStatus.ACCEPTED_SEEN || status == AppointmentStatus.PENDING)) {
            alertUser("This appointment is no longer available");
            return;
        }

        if (appointment.getRequestedDate().minusDays(1).isBefore(LocalDateTime.now())) {
            alertUser("You cannot cancel this appointment as it is within 24 hours of the scheduled time");
            return;
        }

        Optional<ButtonType> result = confirmOption("Are you sure you want to delete this appointment?");

        if (!result.isPresent()) {
            return;
        }

        if (result.get() == ButtonType.OK) {
            appointment.setAppointmentStatus(AppointmentStatus.CANCELLED_BY_USER);
            AppController.getInstance().getAppointmentsBridge().patchAppointmentStatus(appointment.getAppointmentId(),
                    AppointmentStatus.CANCELLED_BY_USER.getDbValue());
        }
    }

    /**
     * Creates a confirmation alert pop-up with the given message
     * Extracted for easier testability
     * @param message message to display in the alert window
     * @return the confirmation alert window result
     */
    public Optional<ButtonType> confirmOption(String message) {
        return AlertWindowFactory.generateConfirmation(message);
    }

    /**
     * Calls the database to get updated appointment entries
     * @param startIndex how many entries to skip before returning
     */
    public void updateTable(int startIndex) {
        appointments.clear();
        AppController.getInstance().getAppointmentsBridge().getAppointments(ROWS_PER_PAGE, startIndex, appointments, user.getNhi(), UserType.USER);
    }

    /**
     * Goes to the previous page in the user's appointments table
     */
    public void goToPreviousPage() {
        if (startingIndex - ROWS_PER_PAGE < 0) {
            return;
        }

        startingIndex = startingIndex - ROWS_PER_PAGE;
        updateTable(startingIndex);
    }

    /**
     * Goes to the next page in the user's appointments table
     */
    public void goToNextPage() {
        if (appointments.size() < ROWS_PER_PAGE) {
            return;
        }

        startingIndex = startingIndex + ROWS_PER_PAGE;
        updateTable(startingIndex);
    }


    /**
     * Alerts user with a alert window containing the given message
     *
     * @param message message to display to the user.
     */
    public void alertUser(String message) {
        AlertWindowFactory.generateError(message);
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

        if (event.getType().equals(EventTypes.USER_UPDATE) && event.getOldIdentifier().equalsIgnoreCase(user.getNhi()) || event.getNewIdentifier().equalsIgnoreCase(user.getNhi())) {

            try {
                User newUser = AppController.getInstance().getUserBridge().getUser(event.getNewIdentifier());
                if (newUser != null) {
                    user = newUser;                }
            } catch (IOException ex) {
                Log.warning("failed to get updated user", ex);
            }

        }
    }
}
