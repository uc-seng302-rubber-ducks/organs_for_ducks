package odms.controller.gui.panel.logic;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odms.commons.model.Appointment;
import odms.commons.model.User;
import odms.commons.model._enum.UserType;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.popup.view.AppointmentPickerViewController;

import java.io.IOException;

public class UserAppointmentLogicController {

    private static final int ROWS_PER_PAGE = 30;
    private AppController appController;
    private User user;
    private ObservableList<Appointment> appointments;

    /**
     * Constructor to create a new logical instance of the controller
     *
     * @param appointments  Observable list of appointments used to populate the users appointments table
     * @param appController Main controller
     * @param user          User that the appointment panel belongs to
     */
    public UserAppointmentLogicController(ObservableList<Appointment> appointments, AppController appController, User user) {
        this.appointments = appointments;
        this.appController = appController;
        this.user = user;

        updateTable();
    }


    /**
     * Launches the pop-up to create and view requested appointments in more detail
     */
    public void launchAppointmentPicker() {
        FXMLLoader appointmentRequestLoader = new FXMLLoader(getClass().getResource("/FXML/appointmentPicker.fxml"));
        Parent root;

        try {
            root = appointmentRequestLoader.load();
            AppointmentPickerViewController appointmentPickerViewController = appointmentRequestLoader.getController();
            Stage appointmentPickerStage = new Stage();
            appointmentPickerViewController.init(user, appointmentPickerStage, appController);
            appointmentPickerStage.setScene(new Scene(root));
            appointmentPickerStage.showAndWait();
            Log.info("Successfully launched the appointment picker pop-up window for user: " + user.getNhi());
            updateTable();

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

    }

    /**
     * Calls the database to get updated appointment entries
     */
    public void updateTable() {
        appointments.clear();
        appController.getAppointmentsBridge().getAppointments(ROWS_PER_PAGE, appointments, user.getNhi(), UserType.USER);
    }

    /**
     * Goes to the previous page in the user's appointments table
     */
    public void goToPreviousPage() {

    }

    /**
     * Goes to the next page in the user's appointments table
     */
    public void goToNextPage() {

    }

}
