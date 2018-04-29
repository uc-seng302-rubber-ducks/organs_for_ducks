package seng302.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import seng302.Model.Clinician;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Controller for updating clinicians
 */
public class UpdateClinicianController {

    @FXML
    private TextField staffIDTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField middleNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField addressTextField;

    @FXML
    private TextField regionTextField;

    @FXML
    private Label invalidStaffIDLabel;

    @FXML
    private Label emptyPasswordLabel;

    @FXML
    private Label incorrectPasswordLabel;

    @FXML
    private Label emptyFNameLabel;

    @FXML
    private Label emptyRegionLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Button confirmButton;

    private AppController controller;
    private Stage stage;
    private Clinician currentClinician;
    private boolean newClinician;

    /**
     * Initializes the scene by setting all but the password text fields to contain the given clinicians attributes.
     * @param clinician The given clinician.
     * @param controller The application controller.
     * @param stage The application stage.
     * @param newClinician true if the current clinician is new, false if the clinician is being updated.
     * */
    public void init(Clinician clinician, AppController controller, Stage stage, boolean newClinician) {
        currentClinician = clinician;
        this.newClinician = newClinician;
        this.controller = controller;
        this.stage = stage;
        stage.setTitle("Update Clinician Profile");
        //stage.setResizable(false);

        if (!newClinician) {
            stage.setTitle("Update Clinician: " + clinician.getFirstName());
            titleLabel.setText("Update Clinician");
            confirmButton.setText("Save Changes");

            prefillFields(clinician);

            // checks if there was a change in any of the user input fields
            changesListener(staffIDTextField);
            changesListener(passwordField);
            changesListener(firstNameTextField);
            changesListener(middleNameTextField);
            changesListener(lastNameTextField);
            changesListener(addressTextField);
            changesListener(regionTextField);

            Scene scene = stage.getScene();

            final KeyCombination shortcutZ = new KeyCodeCombination(
                    KeyCode.Z, KeyCombination.SHORTCUT_DOWN);

            scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
                if (shortcutZ.match(e)) {
                    if (checkChanges()) { // checks if reverting a textfield change restores all fields to their original state
                        stage.setTitle("Update Clinician: " + currentClinician.getFirstName());
                    }
                }
            });

        } else if (newClinician) {
            stage.setTitle("Create New Clinician Profile");
            titleLabel.setText("Create Clinician");
            confirmButton.setText("Create Clinician Profile");
        }
    }

    private void prefillFields(Clinician clinician) {
        staffIDTextField.setText(clinician.getStaffId());
        passwordField.setText(clinician.getPassword());
        confirmPasswordField.setText(clinician.getPassword());

        String fName = clinician.getFirstName();
        String mName = clinician.getMiddleName();
        String lName = clinician.getLastName();
        String address = clinician.getWorkAddress();
        String region = clinician.getRegion();

        if (fName != null) {
            firstNameTextField.setText(fName);
        }

        if (mName != null) {
            middleNameTextField.setText(mName);
        }

        if (lName != null) {
            lastNameTextField.setText(lName);
        }

        if (address != null) {
            addressTextField.setText(address);
        }

        if (region != null) {
            regionTextField.setText(region);
        }
    }


    /**
     * Checks if all text fields are equal to their original pre-filled inputs.
     * The pre-filled inputs are the same as the clinicians attributes.
     * @return true if they are all equal, false if at least one is different.
     */
    private boolean checkChanges() {
        boolean noChange = true;

        if (!(currentClinician.getStaffId()).equals(staffIDTextField.getText())) {
            noChange = false;
        }

        if (!(currentClinician.getPassword()).equals(passwordField.getText())) {
            noChange = false;
        }

        if (!(currentClinician.getFirstName()).equals(firstNameTextField.getText())) {
            noChange = false;
        }

        if (currentClinician.getMiddleName() != null) {
            if (!(currentClinician.getMiddleName()).equals(middleNameTextField.getText())) {
                noChange = false;
            }
        } else if (!middleNameTextField.getText().isEmpty()) {
            noChange = false;
        }

        if (currentClinician.getLastName() != null) {
            if (!(currentClinician.getLastName()).equals(lastNameTextField.getText())) {
                noChange = false;
            }
        } else if (!lastNameTextField.getText().isEmpty()) {
            noChange = false;
        }

        if (currentClinician.getWorkAddress() != null) {
            if (!(currentClinician.getWorkAddress()).equals(addressTextField.getText())) {
                noChange = false;
            }
        } else if (!addressTextField.getText().isEmpty()) {
            noChange = false;
        }

        if (currentClinician.getRegion() != null) {
            if (!(currentClinician.getRegion()).equals(regionTextField.getText())) {
                noChange = false;
            }
        } else if (!regionTextField.getText().isEmpty()) {
            noChange = false;
        }

        return noChange;
    }

    /**
     * Changes the title bar to contain an asterisk if a change was detected.
     * @param field The current textfield/password field element.
     */
    private void changesListener(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            stage.setTitle("Update Clinician: " + currentClinician.getFirstName() + " *");
        });
    }

    /**
     * Attempts to load the clinician overview window.
     * @param clinician The current clinician.
     */
    private void loadOverview(Clinician clinician) {
        FXMLLoader clinicianLoader = new FXMLLoader(getClass().getResource("/FXML/clinicianView.fxml"));
        Parent root = null;

        try {
            root = clinicianLoader.load();
            stage.setScene(new Scene(root));
            ClinicianController clinicianController = clinicianLoader.getController();
            clinicianController.init(stage,controller,clinician);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * If changes are present, a pop up alert is displayed.
     * Closes the window without making any changes.
     * @param event an action event.
     */
    @FXML
    private void cancelUpdate(ActionEvent event) {

        if (!newClinician) {
            if (stage.getTitle().equals("Update Clinician: " + currentClinician.getFirstName() + " *")) { // has changes
                Alert alert = new Alert(Alert.AlertType.WARNING,
                        "You have unsaved changes, are you sure you want to cancel?",
                        ButtonType.YES, ButtonType.NO);

                Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
                yesButton.setId("yesButton");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.YES) {
                    stage.close();
                }
            } else { // has no changes
                stage.close();
            }

        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/loginView.fxml"));
            Parent root = null;

            try {
                root = loader.load();
                LoginController loginController = loader.getController();
                loginController.init(AppController.getInstance(), stage);
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves the clinician if all updated attributes are valid, otherwise error messages are displayed.
     * Upon a successful save, the window closes.
     * @param event an action event.
     */
    @FXML
    private void saveChanges(ActionEvent event) {
        hideErrorMessages(); // clears the error messages
        boolean valid = true;
        String staffID = staffIDTextField.getText();

        if (!staffID.isEmpty()) {
            Clinician foundClinician = controller.getClinician(staffID);

            if (!(foundClinician == null || (!newClinician && staffID.equals(currentClinician.getStaffId())))) { // no clinician exists with the updated staff ID
                invalidStaffIDLabel.setText("Staff ID already in use");
                invalidStaffIDLabel.setVisible(true);
                valid = false;
            }

        } else {
            invalidStaffIDLabel.setText("Staff ID cannot be empty");
            invalidStaffIDLabel.setVisible(true);
            valid = false;
        }

        String password = null;
        String fName = null;
        String mName = null;
        String lName = null;
        String address = null;
        String region = null;

        if ((passwordField.getText()).isEmpty()) {
            emptyPasswordLabel.setVisible(true);
            valid = false;
        } else {
            password = passwordField.getText();
            if (!(confirmPasswordField.getText()).equals(password)) {
                incorrectPasswordLabel.setVisible(true);
                valid = false;
            }
        }

        if ((firstNameTextField.getText()).isEmpty()) {
            emptyFNameLabel.setVisible(true);
        } else {
            fName = firstNameTextField.getText();
        }

        if (!(middleNameTextField.getText()).isEmpty()) {
            mName = middleNameTextField.getText();
        }

        if (!(lastNameTextField.getText()).isEmpty()) {
            lName = lastNameTextField.getText();
        }

        if (!(addressTextField.getText()).isEmpty()) {
            address = addressTextField.getText();
        }

        if ((regionTextField.getText()).isEmpty()) {
            emptyRegionLabel.setVisible(true);
            valid = false;
        } else {
            region = regionTextField.getText();
        }

        if (valid && !newClinician) { // updates an existing clinician
            // updates the attributes
            if (!staffID.equals(currentClinician.getStaffId())) {
                currentClinician.setStaffId(staffID);
            }
            if (fName != null && !fName.equals(currentClinician.getFirstName())) {
                currentClinician.setFirstName(fName);
            }
            if (mName != null && !mName.equals(currentClinician.getMiddleName())) {
                currentClinician.setMiddleName(mName);
            }
            if (lName != null && !lName.equals(currentClinician.getLastName())) {
                currentClinician.setLastName(lName);
            }
            if (address != null && !address.equals(currentClinician.getWorkAddress())) {
                currentClinician.setWorkAddress(address);
            }
            if (region != null && !region.equals(currentClinician.getRegion())) {
                currentClinician.setRegion(region);
            }
            if (!password.equals(currentClinician.getPassword())) {
                currentClinician.setPassword(password);
            }

            currentClinician.setDateLastModified(LocalDateTime.now()); // updates the modified date

            controller.updateClinicians(currentClinician); // saves the clinician
            stage.close(); // returns to the clinician overview window

        } else if (valid && newClinician) { // creates a new clinician
            Clinician clinician = new Clinician(staffID, password, fName, mName, lName, address, region);
            controller.updateClinicians(clinician);
            loadOverview(clinician);
        }
    }


    /**
     * Hides all error messages from the user.
     */
    private void hideErrorMessages() {
        invalidStaffIDLabel.setVisible(false);
        emptyPasswordLabel.setVisible(false);
        incorrectPasswordLabel.setVisible(false);
        emptyFNameLabel.setVisible(false);
        emptyRegionLabel.setVisible(false);
    }
}
