package seng302.Controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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

import java.time.LocalDate;
import java.util.List;

/**
 * Controller class for  for clinicians to
 * enter reasons for de-registering a receiver's organ.
 * @author acb116
 */
public class DeregisterOrganReasonController {

    @FXML
    private RadioButton transplantReceivedRadioButton;

    @FXML
    private RadioButton registerationErrorRadioButton;

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
    private Label noDiseaseSelectedErrorMessage;

    @FXML
    private ComboBox<Disease> diseaseNameComboBox;

    private List<Disease> diseases;

    AppController controller;
    Stage stage;
    private User currentUser;
    private DonorController donorController;
    private Organs toDeRegister;

    /**
     * Initializes the NewDiseaseController
     * @param organ name of receiver organ to be de-registered
     * @param donorController class
     * @param controller The applications controller.
     * @param stage The applications stage.
     */
    public void init(Organs organ, DonorController donorController, User user, AppController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
        currentUser = user;
        this.donorController = donorController;
        receiverName.setText(user.getFullName());
        organName.setText(organ.organName);
        this.toDeRegister = organ;
        dODDatePicker.setValue(LocalDate.now());
        dODDatePicker.setDisable(true);

        diseases = currentUser.getCurrentDiseases();
        if(diseases.size() == 0){ //if there are no current diseases, the diseaseCuredRadioButton will be disabled
            diseaseCuredRadioButton.setDisable(true);
        }
        diseaseNameComboBox.setItems(FXCollections.observableList(diseases));


        //diseaseNameComboBox.set;

//        if (user.getDeceased() == null || !user.getDeceased()){
//            receiverDiedRadioButton.setDisable(true);
//        }
        //stage.setMinWidth(620);
        //stage.setMaxWidth(620);

        //if diseaseCuredRadioButton is selected, the disease name combo box will be enabled, otherwise it sets to disabled
        diseaseCuredRadioButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
                if (isNowSelected) {
                    diseaseNameComboBox.setDisable(false);
                } else {
                    diseaseNameComboBox.setDisable(true);
                }
            }
        });

        //if receiverDiedRadioButton is selected, the DOD date picker will be enabled, otherwise it sets to disabled
        receiverDiedRadioButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean wasPreviouslySelected, Boolean isNowSelected) {
                if (isNowSelected) {
                    dODDatePicker.setDisable(false);
                } else {
                    dODDatePicker.setDisable(true);
                }
            }
        });
    }

    /**
     * Cancels the de-registration process
     * and closes this window
     * @param event passed in automatically by the gui
     */
    @FXML
    void cancelDeregistration(ActionEvent event) {
        AppController appController = AppController.getInstance();
        DonorController donorController = appController.getDonorController();
        try {
            donorController.showUser(currentUser);
        }
        catch (NullPointerException ex) {
            //TODO causes npe if donor is new in this session
            //the text fields etc. are all null
        }
        stage.close();
    }

    /**
     * accepts the de-registration reason
     * and closes this window
     * @param event passed in automatically by the gui
     */
    @FXML
    void acceptDeregistration(ActionEvent event) {
        boolean isValid = true;
        if(transplantReceivedRadioButton.isSelected()){
            donorController.setOrganDeregisterationReason(OrganDeregisterReason.TRANSPLANT_RECEIVED);

        } else if(registerationErrorRadioButton.isSelected()){
            donorController.setOrganDeregisterationReason(OrganDeregisterReason.REGISTRATION_ERROR);

        } else if (diseaseCuredRadioButton.isSelected()){
            donorController.setOrganDeregisterationReason(OrganDeregisterReason.DISEASE_CURED);
            Disease selectedDisease = diseaseNameComboBox.getSelectionModel().getSelectedItem();

            if(selectedDisease == null){ //if non of the disease is selected
                noDiseaseSelectedErrorMessage.setVisible(true);
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
                donorController.setOrganDeregisterationReason(OrganDeregisterReason.RECEIVER_DIED);
            }
        }

        if(isValid){
            donorController.deRegisterOrgan(toDeRegister);

            AppController appController = AppController.getInstance();
            DonorController donorController = appController.getDonorController();
            try {
                donorController.showUser(currentUser);
            }
            catch (NullPointerException ex) {
                //TODO causes npe if donor is new in this session
                //the text fields etc. are all null
            }
            stage.close();
        }
    }

}
