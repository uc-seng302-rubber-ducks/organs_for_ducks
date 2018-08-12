package odms.controller.gui.popup;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model._enum.Regions;
import odms.commons.utils.AttributeValidation;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.window.UserController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Controller class for editing death details of a user
 */
public class UpdateDeathDetailsController {

    @FXML
    private DatePicker updateDeathDetailsDatePicker;

    @FXML
    private TextField updateDeathDetailsTimeTextField;

    @FXML
    private TextField updateDeathDetailsCityTextField;

    @FXML
    private TextField updateDeathDetailsRegionTextField;

    @FXML
    private ChoiceBox<String> updateDeathDetailsRegionChoiceBox;

    @FXML
    private ComboBox<String> updateDeathDetailsCountryComboBox;

    @FXML
    private Label updateDeathDetailsErrorLabel;


    private AppController controller;
    private Stage stage;
    private User currentUser;
    private boolean isNewZealand;

    /**
     * Initialises the update death details popup when it is opened by hiding the error label and filling in the
     * location fields
     * @param controller the application controller
     * @param stage the application stage
     * @param currentUser user to be edited
     */
    public void init(AppController controller, Stage stage, User currentUser) {
        this.controller = controller;
        this.stage = stage;
        this.currentUser = currentUser;

        stage.setTitle(currentUser.getNhi());
        updateDeathDetailsErrorLabel.setVisible(false);
        prefillFields();
    }

    Stage getStage() {
        return stage;
    }


    /**
     * Fills in the location text fields with the users current contact location. Can still be edited if that is not
     * where they died.
     */
    private void prefillFields() {

        for (Regions regions : Regions.values()) {
            updateDeathDetailsRegionChoiceBox.getItems().add(regions.toString());
        }
        for (String country : controller.getAllCountries()) {
            updateDeathDetailsCountryComboBox.getItems().add(country);
        }

        if (currentUser.getDateOfDeath() != null) {
            updateDeathDetailsDatePicker.setValue(currentUser.getDateOfDeath());
        } else {
            updateDeathDetailsDatePicker.setValue(LocalDate.now());
        }


        String timeOfDeath;
        if (currentUser.getTimeOfDeath() != null) {
            timeOfDeath = currentUser.getTimeOfDeath().toString();
        } else {
            String minute = String.format("%02d", LocalDateTime.now().getMinute());
            String hour = String.format("%02d", LocalDateTime.now().getHour());
            timeOfDeath = hour + ":" + minute;
        }
        updateDeathDetailsTimeTextField.setText(timeOfDeath);
        handleRegionPicker();


        if (!currentUser.getDeathCity().isEmpty()) {
            updateDeathDetailsCityTextField.setText(currentUser.getDeathCity());
        } else {
            updateDeathDetailsCityTextField.setText(currentUser.getCity());
        }
        if (!currentUser.getDeathRegion().isEmpty()) {
            updateDeathDetailsRegionChoiceBox.setValue(currentUser.getDeathRegion());
            updateDeathDetailsRegionTextField.setText(currentUser.getDeathRegion());
        } else {
            updateDeathDetailsRegionChoiceBox.setValue(currentUser.getRegion());
            updateDeathDetailsRegionTextField.setText(currentUser.getRegion());
        }
        if (!currentUser.getDeathCountry().isEmpty()) {
            updateDeathDetailsCountryComboBox.setValue(currentUser.getDeathCountry());
        } else {
            updateDeathDetailsCountryComboBox.setValue(currentUser.getCountry());
        }

    }

    /**
     * Changes the region selector based on whether New Zealand is selected or not
     */
    private void handleRegionPicker() {
        String currentTextRegion = "";
        String currentChoiceRegion = "";
        if (updateDeathDetailsCountryComboBox.getValue() != null && updateDeathDetailsCountryComboBox.getValue().equals("New Zealand")) {
            isNewZealand = true;
            currentChoiceRegion = currentUser.getRegion();
        } else {
            isNewZealand = false;
            currentTextRegion = currentUser.getRegion();
        }

        updateDeathDetailsRegionTextField.setText(currentTextRegion);
        updateDeathDetailsRegionTextField.setDisable(isNewZealand);
        updateDeathDetailsRegionTextField.setVisible(!isNewZealand);

        updateDeathDetailsRegionChoiceBox.setValue(currentChoiceRegion);
        updateDeathDetailsRegionChoiceBox.setDisable(!isNewZealand);
        updateDeathDetailsRegionChoiceBox.setVisible(isNewZealand);

    }

    /**
     * Updates the region selector type when the country selector is interacted with
     */
    @FXML
    private void deathCountrySelectorListener() {
        handleRegionPicker();
    }

    /**
     * Closes the update death details popup. Called by the Cancel button
     */
    @FXML
    private void cancelUpdateDeathDetails() {
        Log.info("Death details update cancelled without change for User NHI: " + currentUser.getNhi());
        stage.close();
    }

    /**
     * Saves the update death details changes to the user. Called by the confirm button
     */
    @FXML
    private void confirmUpdateDeathDetails() {
        updateDeathDetailsErrorLabel.setVisible(false);
        UserController userController = controller.getUserController();

        if (!validateFields()) {
            updateDeathDetailsErrorLabel.setVisible(true);
            return;
        }

        LocalDate dateOfDeath = updateDeathDetailsDatePicker.getValue();
        LocalTime timeOfDeath = LocalTime.parse(updateDeathDetailsTimeTextField.getText());
        currentUser.setMomentOfDeath(currentUser.getDeathDetails().createMomentOfDeath(dateOfDeath, timeOfDeath));

        currentUser.setDeathCity(updateDeathDetailsCityTextField.getText());
        if (isNewZealand) {
            currentUser.setDeathRegion(updateDeathDetailsRegionChoiceBox.getValue());
        } else {
            currentUser.setDeathRegion(updateDeathDetailsRegionTextField.getText());
        }
        currentUser.setDeathCountry(updateDeathDetailsCountryComboBox.getValue());

        currentUser.getRedoStack().clear();
        userController.showUser(currentUser);
        Log.info("Update User Death Details Successful for User NHI: " + currentUser.getNhi());

        stage.close();
    }

    /**
     * Opens the alert window asking if the user really wants to remove the death details
     */
    @FXML
    private void removeUpdateDeathDetails() {
        FXMLLoader removeDeathDetailsLoader = new FXMLLoader(getClass().getResource("/FXML/removeDeathDetailsAlert.fxml"));
        Parent root;
        try {
            root = removeDeathDetailsLoader.load();
            RemoveDeathDetailsAlertController removeDeathDetailsController = removeDeathDetailsLoader.getController();
            Stage updateStage = new Stage();
            updateStage.initModality(Modality.APPLICATION_MODAL);
            updateStage.setScene(new Scene(root));
            removeDeathDetailsController.init(AppController.getInstance(), updateStage, currentUser, this);
            updateStage.show();
            Log.info("Successfully remove death details window for User NHI: " + currentUser.getNhi());

        } catch (IOException e) {
            Log.severe("Failed to load remove death details window for User NHI: " + currentUser.getNhi(), e);
        }
    }

    /**
     * Checks if the entry fields are of a valid format and sensible time (death date after birth date)
     * Combobox entries automatically validates country and region if from New Zealand
     * @return True if fields are valid.
     */
    private boolean validateFields() {
        boolean isValid = true;

        LocalDate dateOfDeath = updateDeathDetailsDatePicker.getValue();
        String timeOfDeath = updateDeathDetailsTimeTextField.getText();

        if (!(AttributeValidation.validateDateOfDeath(currentUser.getDateOfBirth(), dateOfDeath))) {
            updateDeathDetailsErrorLabel.setText("There is an error with your Date of Death");
            isValid = false;
        }

        if (!(AttributeValidation.validateTimeString(timeOfDeath))) {
            updateDeathDetailsErrorLabel.setText("The format of the Time of Death is incorrect");
            isValid = false;
        }

        return isValid;
    }

}
