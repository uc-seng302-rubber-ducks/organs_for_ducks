package seng302.Controller;

import java.time.LocalDate;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import seng302.Model.Disease;
import seng302.Model.OrganDeregisterReason;
import seng302.Model.Organs;
import seng302.Model.User;
import seng302.Service.AttributeValidation;
import seng302.Service.Log;

/**
 * Controller class for  for clinicians to
 * enter reasons for de-registering a receiver's organ.
 * @author acb116
 */
public class DeregisterOrganReasonController {

    @FXML
    private RadioButton transplantReceivedRadioButton;

    @FXML
    private RadioButton registrationErrorRadioButton;

    @FXML
    private RadioButton diseaseCuredRadioButton;

    @FXML
    private RadioButton receiverDiedRadioButton;

    @FXML
    private DatePicker dODDatePicker;

    @FXML
    private Label receiverName;

    @FXML
    private Label organName;

    @FXML
    private Label invalidDateErrorMessage;

    @FXML
    private ComboBox<Disease> diseaseNameComboBox;

    AppController controller;
    Stage stage;
    private User currentUser;
    private UserController userController;
    private Organs toDeRegister;

    /**
     * Initializes the NewDiseaseController
     * @param organ name of receiver organ to be de-registered
     * @param userController class
     * @param controller The applications controller.
     * @param stage The applications stage.
     */
    public void init(Organs organ, UserController userController, User user,
        AppController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
        currentUser = user;
        this.userController = userController;
        receiverName.setText(user.getFullName());
        organName.setText(organ.organName);
        this.toDeRegister = organ;
        dODDatePicker.setValue(LocalDate.now());
        dODDatePicker.setDisable(true);

        List<Disease> diseases = currentUser.getCurrentDiseases();
        if(diseases.size() == 0){ //if there are no current diseases, the diseaseCuredRadioButton will be disabled
            diseaseCuredRadioButton.setDisable(true);
        }
        diseaseNameComboBox.setItems(FXCollections.observableList(diseases));

        //if diseaseCuredRadioButton is selected, the disease name combo box will be enabled, otherwise it sets to disabled
        diseaseCuredRadioButton.selectedProperty().addListener(
            (obs, wasPreviouslySelected, isNowSelected) -> {
                if (isNowSelected) {
                    diseaseNameComboBox.setDisable(false);
                } else {
                    diseaseNameComboBox.setDisable(true);
                }
            });

        //if receiverDiedRadioButton is selected, the DOD date picker will be enabled, otherwise it sets to disabled
        receiverDiedRadioButton.selectedProperty().addListener(
            (obs, wasPreviouslySelected, isNowSelected) -> {
                if (isNowSelected) {
                    dODDatePicker.setDisable(false);
                } else {
                    dODDatePicker.setDisable(true);
                }
            });
    }

    /**
     * Cancels the de-registration process
     * and closes this window
     */
    @FXML
    void cancelDeregistration() {
        AppController appController = AppController.getInstance();
        UserController userController = appController.getUserController();
        try {
            userController.showUser(currentUser);
            Log.info("Receiver organ de-registration cancelled");
        }
        catch (NullPointerException ex) {
            //TODO causes npe if donor is new in this session
            //the text fields etc. are all null
            Log.severe("error cancelling Receiver organ de-registration", ex);
        }
        stage.close();
    }

    /**
     * accepts the de-registration reason
     * and closes this window
     */
    @FXML
    void acceptDeregistration() {
        boolean isValid = true;
        if(transplantReceivedRadioButton.isSelected()){
            userController.setOrganDeregisterationReason(OrganDeregisterReason.TRANSPLANT_RECEIVED);

        } else if (registrationErrorRadioButton.isSelected()) {
            userController.setOrganDeregisterationReason(OrganDeregisterReason.REGISTRATION_ERROR);

        } else if (diseaseCuredRadioButton.isSelected()){
            userController.setOrganDeregisterationReason(OrganDeregisterReason.DISEASE_CURED);
            Disease selectedDisease = diseaseNameComboBox.getSelectionModel().getSelectedItem();

            if(selectedDisease == null){ //if non of the disease is selected
                isValid = false;

            } else {
                selectedDisease.setIsCured(true);
                currentUser.getCurrentDiseases().remove(selectedDisease);
                currentUser.getPastDiseases().add(selectedDisease);
            }

        } else if(receiverDiedRadioButton.isSelected()){
            LocalDate dOD = dODDatePicker.getValue();
            if(!AttributeValidation.validateDates(currentUser.getDateOfBirth(), dOD)){
                isValid = false;
                invalidDateErrorMessage.setVisible(true);
            } else {
                currentUser.setDateOfDeath(dOD);
                //currentUser.setDeceased(true);
                userController.setOrganDeregisterationReason(OrganDeregisterReason.RECEIVER_DIED);
            }
        }

        if(isValid){
            this.userController.deRegisterOrgan(toDeRegister);

            AppController appController = AppController.getInstance();
            UserController userController = appController.getUserController();
            try {
                userController.showUser(currentUser);
                Log.info("Receiver organ de-registration success");
            }
            catch (NullPointerException ex) {
                //TODO causes npe if donor is new in this session
                //the text fields etc. are all null
                Log.severe("Receiver organ de-registration failed", ex);
            }
            stage.close();
        }
    }

}
