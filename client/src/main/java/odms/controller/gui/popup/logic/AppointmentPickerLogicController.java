package odms.controller.gui.popup.logic;

import javafx.application.Platform;
import javafx.stage.Stage;
import odms.commons.model.Appointment;
import odms.commons.model.User;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.controller.AppController;
import odms.controller.gui.popup.utils.AlertWindowFactory;

import java.time.LocalDate;

import static odms.commons.utils.AttributeValidation.validateDateOfAppointment;

public class AppointmentPickerLogicController {

    private User user;
    private Stage stage;
    private AppController appController;

    /**
     * Initializes the AppointmentPickerLogicController
     *
     * @param user          Current user
     * @param appController The applications controller.
     * @param stage         The applications stage.
     */
    public AppointmentPickerLogicController(User user, Stage stage, AppController appController) {
        this.user = user;
        this.stage = stage;
        this.appController = appController;

    }

    /**
     * Created a new appointment booking request.
     * Creates a pop-up error window if input validation fails.
     *
     * @param date               desired date of appointment
     * @param type               category/type of appointment
     * @param preferredClinician name
     * @param description        of appointment
     * @return true if  a new appointment booking is successfully
     * created, false otherwise.
     */
    public boolean confirm(LocalDate date, AppointmentCategory type, String preferredClinician, String description) {
        if (!validateDateOfAppointment(date)) {
            alertUser("Invalid Appointment Date! The earliest appointment date is tomorrow.");
            return false;
        }

        Appointment appointment = new Appointment(user.getNhi(), preferredClinician, type, date.atStartOfDay(), description, AppointmentStatus.PENDING);
        appController.getAppointmentsBridge().postAppointment(appointment);
        return false;
    }


    /**
     * closes the appointment picker view.
     */
    public void cancel() {
        stage.close();
    }

    /**
     * Alerts user with a alert window containing the given message
     *
     * @param message message to display to the user.
     */
    private void alertUser(String message) {
        Platform.runLater(() -> AlertWindowFactory.generateError(message));
    }
}
