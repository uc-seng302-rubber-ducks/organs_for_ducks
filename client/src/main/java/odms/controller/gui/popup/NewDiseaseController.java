package odms.controller.gui.popup;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.commons.model.Disease;
import odms.commons.model.User;
import odms.commons.utils.AttributeValidation;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.window.UserController;

import java.time.LocalDate;

/**
 * controller class for creating new disease.
 */
public class NewDiseaseController {
    @FXML
    public TextField diseaseNameInput;

    @FXML
    public DatePicker diagnosisDateInput;

    @FXML
    public Label diseaseNameInputErrorMessage;

    @FXML
    public Label diagnosisDateInputErrorMessage;

    @FXML
    public Label headerLabel;

    @FXML
    public RadioButton chronicRadioButton;

    @FXML
    public RadioButton curedRadioButton;

    AppController controller;
    Stage stage;
    UserController userController;
    private User currentUser;
    private Disease editableDisease;

    /**
     * Initializes the NewDiseaseController
     *
     * @param user       the current user.
     * @param controller The applications controller.
     * @param stage      The applications stage.
     * @param disease    disease to show
     * @param userController parent of this window
     */
    public void init(User user, AppController controller, Stage stage, Disease disease,
                     UserController userController) {
        this.controller = controller;
        this.stage = stage;
        this.userController = userController;
        stage.setResizable(false);
        stage.setTitle("Create New Disease");
        currentUser = user;
        editableDisease = disease;
        if(disease != null) {
            stage.setTitle("Update Disease");
            String diseaseName = disease.getName();
            LocalDate date = disease.getDiagnosisDate();
            boolean isCured = disease.getIsCured();
            boolean isChronic = disease.getIsChronic();

            diseaseNameInput.setText(diseaseName);
            diagnosisDateInput.setValue(date);
            curedRadioButton.setSelected(isCured);
            chronicRadioButton.setSelected(isChronic);
            headerLabel.setText("Update Disease");
        }
    }

    /**
     * Cancels the creation of
     * new disease.
     */
    @FXML
    void cancelCreation() {
        try {
            userController.showUser(currentUser);
            Log.info("successfully cancelled creation of new disease for User NHI: " + currentUser.getNhi());
        } catch (NullPointerException ex) {
            Log.severe("Failed to cancel creation of new disease for User NHI: " + currentUser.getNhi(), ex);
            //TODO causes npe if donor is new in this session
            //the text fields etc. are all null
        }
        stage.close();
    }


    /**
     * closes New Disease Window
     * and show door's updated info.
     */
    private void closeNewDiseaseWindow() {
        try {
            userController.showUser(currentUser);
            //userController.showDonorDiseases(currentUser, false); //Pointless
            Log.info("successfully closed New Disease Window for User NHI: " + currentUser.getNhi());
        } catch (NullPointerException ex) {
            Log.severe("Failed to close New Disease Window for User NHI: " + currentUser.getNhi(), ex);

            //the text fields etc. are all null
        }
        this.controller.update(currentUser);
        stage.close();
    }

    /**
     * clears all selection from
     * radio button that are in diseaseStatus toggle group
     */
    @FXML
    private void clearSelection() {
        chronicRadioButton.setSelected(false);
        curedRadioButton.setSelected(false);
    }

    /**
     * creates new disease and adds to user
     * profile. shows error messages if input
     * is invalid.
     */
    @FXML
    private void createDisease() {
        boolean isValid;

        String diseaseName = diseaseNameInput.getText();
        isValid = AttributeValidation.checkString(diseaseName);
        LocalDate diagnosisDate = diagnosisDateInput.getValue();
        boolean isCured = curedRadioButton.isSelected();
        boolean isChronic = chronicRadioButton.isSelected();

        if (isChronic && isCured) {
            isValid = false;
        }

        if (diseaseName.isEmpty()) {
            diseaseNameInputErrorMessage.setVisible(true);
            isValid = false;
        } else {
            diseaseNameInputErrorMessage.setVisible(false);
        }

        if (diagnosisDate == null || diagnosisDate.isAfter(LocalDate.now()) || diagnosisDate.isBefore(currentUser.getDateOfBirth())) {
            diagnosisDateInputErrorMessage.setVisible(true);
            isValid = false;
        } else {
            diagnosisDateInputErrorMessage.setVisible(false);
        }

        if (isValid) {
            if(editableDisease == null){
                editableDisease = new Disease();
            }
            //this if/if else ensures that cured diseases can only be in pastDiseases[] and chronic diseases can only be in currentDiseases[]
            if (isCured && !editableDisease.getIsCured()) { //if it WASNT cured, but now IS, move to past
                currentUser.getCurrentDiseases().remove(editableDisease);
                currentUser.getPastDiseases().add(editableDisease);

                editableDisease.setName(diseaseName);
                editableDisease.setDiagnosisDate(diagnosisDate);
                editableDisease.setIsCured(
                        isCured); // noted as always true, but we feel this is clearer for others
                editableDisease.setIsChronic(isChronic);

            } else if (!isCured && editableDisease.getIsCured()) { //if it WAS cured, but now isn't, move to current
                currentUser.getPastDiseases().remove(editableDisease);
                currentUser.getCurrentDiseases().add(editableDisease);

                editableDisease.setName(diseaseName);
                editableDisease.setDiagnosisDate(diagnosisDate);
                editableDisease.setIsCured(isCured);
                editableDisease.setIsChronic(isChronic);
            } else {
                if(!currentUser.getCurrentDiseases().contains(editableDisease) && !currentUser.getPastDiseases().contains(editableDisease)) {
                    if(isCured){
                        currentUser.addPastDisease(editableDisease);
                    } else {
                        currentUser.addCurrentDisease(editableDisease);
                    }
                }
                editableDisease.setName(diseaseName);
                editableDisease.setDiagnosisDate(diagnosisDate);
                editableDisease.setIsCured(isCured);
                editableDisease.setIsChronic(isChronic);

            }


            userController.refreshDiseases();
            closeNewDiseaseWindow();
            Log.info("Successfully added new disease: " + diseaseName + " for User NHI: " + currentUser.getNhi());
        } else {
            Log.warning("Unable to add new disease: " + diseaseName + " for User NHI: " + currentUser.getNhi() + " as there are invalid user input");
        }
    }
}
