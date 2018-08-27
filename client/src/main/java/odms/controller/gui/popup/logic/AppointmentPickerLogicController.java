package odms.controller.gui.popup.logic;

import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model._enum.AppointmentCategory;
import odms.controller.AppController;

import java.time.LocalDate;

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
     * Created a new appointment booking
     *
     * @param date               desired date of appointment
     * @param type               category/type of appointment
     * @param preferredClinician name
     * @param description        of appointment
     * @return true if  a new appointment booking is successfully
     * created, false otherwise.
     */
    public boolean confirm(LocalDate date, AppointmentCategory type, String preferredClinician, String description) {
        return false; //TODO: implement functionality -27/8
    }


    /**
     * closes the appointment picker view.
     */
    public void cancel() {
        stage.close();
    }
}
