package odms.controller.gui.popup;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model._enum.Regions;
import odms.commons.utils.Log;
import odms.controller.AppController;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
    private ChoiceBox<String> updateDeathDetailsCountryChoiceBox;


    private AppController controller;
    private Stage stage;
    private User currentUser;

    public void init(AppController controller, Stage stage, User currentUser) {
        this.controller = controller;
        this.stage = stage;
        this.currentUser = currentUser;

        prefillFields();
    }

    private void prefillFields() {

        for (Regions regions : Regions.values()) {
            updateDeathDetailsRegionChoiceBox.getItems().add(regions.toString());
        }
        for (String country : controller.getAllCountries()) {
            updateDeathDetailsCountryChoiceBox.getItems().add(country);
        }

        updateDeathDetailsDatePicker.setValue(currentUser.getDateOfDeath());
        String timeOfDeath;
        if (currentUser.getTimeOfDeath() != null) {
            timeOfDeath = currentUser.getTimeOfDeath().toString();
        } else {
            timeOfDeath = "";
        }
        updateDeathDetailsTimeTextField.setText(timeOfDeath);

        updateDeathDetailsCityTextField.setText(currentUser.getCity());
        handleRegionPickerPrefill();
        updateDeathDetailsCountryChoiceBox.setValue(currentUser.getCountry());
    }

    private void handleRegionPickerPrefill() {
        boolean isNewZealand = false;
        String currentTextRegion = "";
        String currentChoiceRegion = "";
        if (currentUser.getCountry().equals("New Zealand")) {
            isNewZealand = true;
            currentChoiceRegion = currentUser.getRegion();
        } else {
            currentTextRegion = currentUser.getRegion();
        }

        updateDeathDetailsRegionTextField.setText(currentTextRegion);
        updateDeathDetailsRegionTextField.setDisable(isNewZealand);
        updateDeathDetailsRegionTextField.setVisible(!isNewZealand);

        updateDeathDetailsRegionChoiceBox.setValue(currentChoiceRegion);
        updateDeathDetailsRegionChoiceBox.setDisable(!isNewZealand);
        updateDeathDetailsRegionChoiceBox.setVisible(isNewZealand);

    }

    @FXML
    private void cancelUpdateDeathDetails() {
        closeUpdateDeathDetails();
    }

    /**
     * Closes the update death details popup. Called by the Cancel button
     * (Empty for now)
     */
    private void closeUpdateDeathDetails(){
        Log.severe("Not implemented yet", new NotImplementedException());
    }

    @FXML
    private void confirmUpdateDeathDetails() {
        saveUpdateDeathDetails();
        closeUpdateDeathDetails();
    }

    /**
     * Saves the update death details changes to the user. Called by the Confirm button
     * (Empty for now)
     */
    private void saveUpdateDeathDetails() {
        Log.severe("Not implemented yet", new NotImplementedException());
    }

}
