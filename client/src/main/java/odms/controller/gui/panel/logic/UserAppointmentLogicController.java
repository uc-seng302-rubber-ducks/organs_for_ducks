package odms.controller.gui.panel.logic;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import odms.commons.model.Appointment;
import odms.commons.model.User;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.popup.view.AppointmentPickerViewController;

import java.io.IOException;

public class UserAppointmentLogicController {

    private AppController appController;
    private User user;

    /**
     * Constructor to create a new logical instance of the controller
     *
     * @param appController Main controller
     * @param user          User that the appointment panel belongs to
     */
    public UserAppointmentLogicController(AppController appController, User user) {
        this.appController = appController;
        this.user = user;
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
            appointmentPickerStage.show();
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

    }

}
