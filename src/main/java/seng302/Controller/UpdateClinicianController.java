package seng302.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import seng302.Model.Clinician;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Controller for updating clinicians
 * @author jha236
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
            stage.setTitle("Update Clinician Profile");
            titleLabel.setText("Update Clinician");
            confirmButton.setText("Save Changes");
            staffIDTextField.setText(clinician.getStaffId());
            passwordField.setText(clinician.getPassword());
            confirmPasswordField.setText(clinician.getPassword());
            firstNameTextField.setText(clinician.getFirstName());
            regionTextField.setText(clinician.getRegion());

            String mName = clinician.getMiddleName();
            String lName = clinician.getLastName();
            String address = clinician.getWorkAddress();

            if (mName == null) {
                middleNameTextField.setText("");
            } else {
                middleNameTextField.setText(mName);
            }

            if (lName == null) {
                lastNameTextField.setText("");
            } else {
                lastNameTextField.setText(lName);
            }

            if (address == null) {
                addressTextField.setText("");
            } else {
                addressTextField.setText(address);
            }

        } else if (newClinician) {
            stage.setTitle("Create New Clinician Profile");
            titleLabel.setText("Create Clinician");
            confirmButton.setText("Create Clinician Profile");
        }
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
     * Closes the window without making any changes.
     * @param event an action event.
     */
    @FXML
    private void cancelUpdate(ActionEvent event) {

        if (!newClinician) {
            stage.close();
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
        System.out.println("here");

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
            System.out.println("valid");
            // updates the attributes
            currentClinician.setStaffId(staffID);
            currentClinician.setFirstName(fName);
            currentClinician.setMiddleName(mName);
            currentClinician.setLastName(lName);
            currentClinician.setWorkAddress(address);
            currentClinician.setRegion(region);
            currentClinician.setPassword(password);

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
