package odms.controller.gui.popup.view;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model._enum.AppointmentCategory;
import odms.controller.AppController;
import odms.controller.gui.popup.logic.AppointmentPickerLogicController;

public class AppointmentPickerViewController {
    @FXML
    private DatePicker appointmentBookingDateInput;

    @FXML
    private ComboBox<AppointmentCategory> appointmentBookingTypeInput;

    @FXML
    private ComboBox<String> appointmentBookingPrefClinicianInput;

    @FXML
    private TextArea appointmentBookingDescriptionInput;

    private User user;
    private Stage stage;
    private AppController appController;
    private AppointmentPickerLogicController logicController;


    /**
     * Initializes the AppointmentPickerViewController
     *
     * @param user          Current user
     * @param appController The applications controller.
     * @param stage         The applications stage.
     */
    public void init(User user, Stage stage, AppController appController) {
        this.stage = stage;
        this.user = user;
        this.appController = appController;
        this.logicController = new AppointmentPickerLogicController(user, stage, appController);

    }

    @FXML
    public void cancel() {
        logicController.cancel();
    }

    @FXML
    public void confirm() {
        logicController.confirm(
                appointmentBookingDateInput.getValue(),
                appointmentBookingTypeInput.getSelectionModel().getSelectedItem(),
                appointmentBookingPrefClinicianInput.getValue(),
                appointmentBookingDescriptionInput.getText());
    }


}
