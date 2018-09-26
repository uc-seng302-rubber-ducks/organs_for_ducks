package odms.controller.gui.popup.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
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

    @FXML
    private CheckBox defaultPreferredClinicianCheckBox;

    private AppointmentPickerLogicController logicController;


    /**
     * Initializes the AppointmentPickerViewController
     *
     * @param user  Current user
     * @param stage The applications stage.
     */
    public void init(User user, Stage stage) {
        this.logicController = new AppointmentPickerLogicController(user, stage);
        appointmentBookingTypeInput.getItems().addAll(AppointmentCategory.values());
        appointmentBookingTypeInput.getItems().remove(AppointmentCategory.PERSONAL); //Only clinicians can use this category

        appointmentBookingDescriptionInput.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 255 ? change : null)); // limits user input to 255 characters
        loadPreferredClinicians(user);
    }

    /**
     * Calls the server to get the users preferred clinicians and a list of clinicians available for the user to select
     * from when making an appointment (available clinicians are ones in the same region as the user)
     *
     * @param user User to get available and preferred clinician for
     */
    private void loadPreferredClinicians(User user) {
        List<ComboBoxClinician> comboBoxClinicians = new ArrayList<>();
        ComboBoxClinician defaultPreferred = null;
        try {
            comboBoxClinicians = AppController.getInstance().getClinicianBridge().getBasicClinicians(user.getRegion());
            defaultPreferred = AppController.getInstance().getUserBridge().getPreferredClinician(user.getNhi());
        } catch (IOException e) {
            Log.severe("Unable to get preferred clinicians.", e);
        }

        if (comboBoxClinicians.isEmpty()) {
            ComboBoxClinician defaultClinician = new ComboBoxClinician("default", "0");
            appointmentBookingPrefClinicianInput.getItems().add(defaultClinician);
        } else {
            for (ComboBoxClinician clinician : comboBoxClinicians) {
                appointmentBookingPrefClinicianInput.getItems().add(clinician);
            }

            if (defaultPreferred != null && comboBoxClinicians.contains(defaultPreferred)) {
                appointmentBookingPrefClinicianInput.getSelectionModel().select(defaultPreferred);
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
        if (defaultPreferredClinicianCheckBox.isSelected()) {
            logicController.setDefaultPreferredClinician(clinicianId);
        }
        logicController.confirm(
                appointmentBookingDateInput.getValue(),
                appointmentBookingTypeInput.getSelectionModel().getSelectedItem(),
                clinicianId,
                appointmentBookingDescriptionInput.getText());
    }


}
