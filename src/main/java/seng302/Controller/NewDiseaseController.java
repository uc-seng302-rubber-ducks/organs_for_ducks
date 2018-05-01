package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seng302.App;
import seng302.Model.Disease;
import seng302.Model.User;
import seng302.Service.AttributeValidation;

import java.time.LocalDate;

/**
 * Controller class for creating new disease.
 * @author acb116
 */
public class NewDiseaseController {
    @FXML
    public TextField diseaseNameInput;

    @FXML
    public DatePicker  diagnosisDateInput;

    @FXML
    public Label diseaseNameInputErrorMessage;

    @FXML
    public Label diagnosisDateInputErrorMessage;

    @FXML
    public RadioButton chronicRadioButton;

    @FXML
    public RadioButton curedRadioButton;

    AppController controller;
    Stage stage;
    DonorController donorController;
    private User currentUser;
    private Disease editableDisease;

    /**
     * Initializes the NewDiseaseController
     * @param controller The applications controller.
     * @param stage The applications stage.
     */
    public void init(User user, AppController controller, Stage stage, Disease disease, DonorController donorController) {
        this.controller = controller;
        this.stage = stage;
        this.donorController = donorController;
        currentUser = user;
        editableDisease = disease;

        String diseaseName = disease.getName();
        LocalDate date = disease.getDiagnosisDate();
        boolean isCured = disease.getIsCured();
        boolean isChronic = disease.getIsChronic();

        diseaseNameInput.setText(diseaseName);
        diagnosisDateInput.setValue(date);
        curedRadioButton.setSelected(isCured);
        chronicRadioButton.setSelected(isChronic);
        
        //showCurrentDate();
        //stage.setMinWidth(620); //*Commented out by Aaron*
        //stage.setMaxWidth(620);
    }

    /**
     * Cancels the creation of
     * new disease.
     * @param event passed in automatically by the gui
     */
    @FXML
    void cancelCreation(ActionEvent event) {
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
     * closes New Disease Window
     * and show door's updated info.
     */
    private void closeNewDiseaseWindow() {
        AppController appController = AppController.getInstance();
        DonorController donorController = appController.getDonorController();
        try {
            donorController.showUser(currentUser);
            donorController.showDonorDiseases(currentUser, false);
        }
        catch (NullPointerException ex) {
            //TODO causes npe if donor is new in this session
            //the text fields etc. are all null
        }
        this.controller.update(currentUser);
        stage.close();
    }

    /**
     * set date picker to display current date
     */
    private void showCurrentDate() {
        diagnosisDateInput.setValue(LocalDate.now());
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
     * creates new disease and adds to donor
     * profile. shows error messages if input
     * is invalid.
     */
    @FXML
    private void CreateDisease() {
        boolean isValid = true;

        String diseaseName = AttributeValidation.checkString(diseaseNameInput.getText());
        LocalDate diagnosisDate = diagnosisDateInput.getValue();
        boolean isCured = curedRadioButton.isSelected();
        boolean isChronic = chronicRadioButton.isSelected();

        if (diseaseName == null) {
            diseaseNameInputErrorMessage.setVisible(true);
            isValid = false;

        } else {
            diseaseNameInputErrorMessage.setVisible(false);
        }

        if (diagnosisDate == null) { //TODO: date validation, eg: diagnosis date cannot be before DOB
            diagnosisDateInputErrorMessage.setVisible(true);
            isValid = false;
        } else if (diagnosisDate.isAfter(LocalDate.now()) || diagnosisDate.isBefore(currentUser.getDateOfBirth())) {
            diagnosisDateInputErrorMessage.setVisible(true);
            isValid = false;
        } else {
            diagnosisDateInputErrorMessage.setVisible(false);
        }

        if (isValid) {

            //this if/if else ensures that cured diseases can only be in pastDiseases[] and chronic diseases can only be in currentDiseases[]
            if (isCured ==  true && editableDisease.getIsCured() == false ) { //if it WASNT cured, but now IS, move to past
                currentUser.getCurrentDiseases().remove(editableDisease);
                currentUser.getPastDiseases().add(editableDisease);

                editableDisease.setName(diseaseName);
                editableDisease.setDiagnosisDate(diagnosisDate);
                editableDisease.setIsCured(isCured);
                editableDisease.setIsChronic(isChronic);

            } else if (isCured == false && editableDisease.getIsCured() == true) { //if it WAS cured, but now isn't, move to current
                currentUser.getPastDiseases().remove(editableDisease);
                currentUser.getCurrentDiseases().add(editableDisease);

                editableDisease.setName(diseaseName);
                editableDisease.setDiagnosisDate(diagnosisDate);
                editableDisease.setIsCured(isCured);
                editableDisease.setIsChronic(isChronic);
            } else {

                editableDisease.setName(diseaseName);
                editableDisease.setDiagnosisDate(diagnosisDate);
                editableDisease.setIsCured(isCured);
                editableDisease.setIsChronic(isChronic);

            }

            //Refresh the view
            donorController.diseaseRefresh(donorController.getIsSortedByName(), donorController.getIsRevereSorted());
            closeNewDiseaseWindow();

        }
    }
}
