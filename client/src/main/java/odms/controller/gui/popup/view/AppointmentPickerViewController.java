package odms.controller.gui.popup.view;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model.datamodel.ComboBoxClinician;
import odms.controller.gui.popup.logic.AppointmentPickerLogicController;

public class AppointmentPickerViewController {
    @FXML
    private DatePicker appointmentBookingDateInput;

    @FXML
    private ComboBox<AppointmentCategory> appointmentBookingTypeInput;

    @FXML
    private ComboBox<ComboBoxClinician> appointmentBookingPrefClinicianInput;

    @FXML
    private TextArea appointmentBookingDescriptionInput;

    private AppointmentPickerLogicController logicController;


    /**
     * Initializes the AppointmentPickerViewController
     *
     * @param user          Current user
     * @param stage         The applications stage.
     */
    public void init(User user, Stage stage) {
        this.logicController = new AppointmentPickerLogicController(user, stage);
        appointmentBookingTypeInput.getItems().addAll(AppointmentCategory.values());

        //TODO: populate the preferred clinicians combobox with new GET clinicians api that doesn't require authentication. -27/8
//        appController.getClinicianBridge().getClinicians(0, Integer.MAX_VALUE, "", user.getRegion(), appController.getToken());
//        for (Clinician clinician : appController.getClinicians()) {
//            appointmentBookingPrefClinicianInput.getItems().add(clinician.getFullName());
//        }


    }

    @FXML
    public void cancel() {
        logicController.cancel();
    }

    @FXML
    public void confirm() {
        // todo: change back to using the clinician combobox when it is populated properly
        logicController.confirm(
                appointmentBookingDateInput.getValue(),
                appointmentBookingTypeInput.getValue(), "0",
                //appointmentBookingPrefClinicianInput.getValue().getId(),
                appointmentBookingDescriptionInput.getText());
    }


}
