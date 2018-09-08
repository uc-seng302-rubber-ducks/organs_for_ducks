package odms.controller.gui.popup.view;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model.datamodel.ComboBoxClinician;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.popup.logic.AppointmentPickerLogicController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
     * @param appController The applications controller.
     * @param stage         The applications stage.
     */
    public void init(User user, Stage stage, AppController appController) {
        this.logicController = new AppointmentPickerLogicController(user, stage, appController);
        appointmentBookingTypeInput.getItems().addAll(AppointmentCategory.values());
        List<ComboBoxClinician> comboBoxClinicians = new ArrayList<>();
        try {
            comboBoxClinicians = appController.getClinicianBridge().getBasicClinicians(user.getRegion());
        } catch (IOException e) {
            Log.severe("Unable to get preferred clinicians.", e);
        }

        if (comboBoxClinicians.isEmpty()){
            ComboBoxClinician defaultClinician = new ComboBoxClinician( "default", "0");
            appointmentBookingPrefClinicianInput.getItems().add(defaultClinician);
        } else {
            for (ComboBoxClinician clinician : comboBoxClinicians) {
                appointmentBookingPrefClinicianInput.getItems().add(clinician);
            }
        }
    }

    @FXML
    public void cancel() {
        logicController.cancel();
    }

    @FXML
    public void confirm() {
        String clinicianId = "";
        if (appointmentBookingPrefClinicianInput.getValue() != null) {
            clinicianId = appointmentBookingPrefClinicianInput.getValue().getId();
        }
        logicController.confirm(
                appointmentBookingDateInput.getValue(),
                appointmentBookingTypeInput.getSelectionModel().getSelectedItem(),
                clinicianId,
                appointmentBookingDescriptionInput.getText());
    }


}
