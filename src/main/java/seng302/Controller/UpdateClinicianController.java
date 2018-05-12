package seng302.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import seng302.Model.Clinician;
import seng302.Model.Memento;
import seng302.Service.PasswordManager;

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

    @FXML
    private Button undoClinicianFormButton;

    @FXML
    private Button redoClinicianFormButton;

    private AppController controller;
    private Stage stage;
    private Clinician currentClinician;
    private Clinician oldClinician;
    private boolean newClinician;
    private int undoMarker;
    private Stage ownStage;

    /**
     * Initializes the scene by setting all but the password text fields to contain the given clinicians attributes.
     *
     * @param clinician    The given clinician.
     * @param controller   The application controller.
     * @param stage        The application stage.
     * @param newClinician true if the current clinician is new, false if the clinician is being updated.
     */
    public void init(Clinician clinician, AppController controller, Stage stage, boolean newClinician, Stage ownStage) {
        currentClinician = clinician;
        this.newClinician = newClinician;
        this.controller = controller;
        this.stage = stage;
        this.ownStage = ownStage;
        ownStage.setTitle("Update Clinician Profile");
        //stage.setResizable(false);
        undoClinicianFormButton.setDisable(true);
        redoClinicianFormButton.setDisable(true);

        if (!newClinician) {
            oldClinician = currentClinician.clone();
            undoMarker = currentClinician.getUndoStack().size();
            ownStage.setTitle("Update Clinician: " + clinician.getFirstName());
            titleLabel.setText("Update Clinician");
            confirmButton.setText("Save Changes");

            prefillFields(clinician);

            // checks if there was a change in any of the user input fields
            changesListener(staffIDTextField);
            changesListener(firstNameTextField);
            changesListener(middleNameTextField);
            changesListener(lastNameTextField);
            changesListener(addressTextField);
            changesListener(regionTextField);

            Scene scene = ownStage.getScene();

            final KeyCombination shortcutZ = new KeyCodeCombination(
                    KeyCode.Z, KeyCombination.CONTROL_DOWN);

            scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
                if (shortcutZ.match(e)) {
                    undo(new ActionEvent());
                    if (checkChanges()) { // checks if reverting a textfield change restores all fields to their original state
                        ownStage.setTitle("Update Clinician: " + currentClinician.getFirstName());
                    }
                }
            });

        } else {
            ownStage.setTitle("Create New Clinician Profile");
            titleLabel.setText("Create Clinician");
            confirmButton.setText("Create Clinician Profile");
        }

        ownStage.setOnCloseRequest(event -> {
            cancelUpdate(new ActionEvent());
        });
    }

    /**
     * Prefills all the text fields as the attribute values.
     * If the attributes are null, then the fields are set as empty strings.
     *
     * @param clinician The current clinician.
     */
    private void prefillFields(Clinician clinician) {
        staffIDTextField.setText(clinician.getStaffId());

        String fName = clinician.getFirstName();
        String mName = clinician.getMiddleName();
        String lName = clinician.getLastName();
        String address = clinician.getWorkAddress();
        String region = clinician.getRegion();

        if (fName != null) {
            firstNameTextField.setText(fName);
        } else {
            firstNameTextField.setText("");
        }

        if (mName != null) {
            middleNameTextField.setText(mName);
        } else {
            middleNameTextField.setText("");
        }

        if (lName != null) {
            lastNameTextField.setText(lName);
        } else {
            lastNameTextField.setText("");
        }

        if (address != null) {
            addressTextField.setText(address);
        } else {
            addressTextField.setText("");
        }

        if (region != null) {
            regionTextField.setText(region);
        } else {
            regionTextField.setText("");
        }
    }


    /**
     * Checks if all text fields are equal to their original pre-filled inputs.
     * The pre-filled inputs are the same as the clinicians attributes.
     *
     * @return true if they are all equal, false if at least one is different.
     */
    private boolean checkChanges() {
        boolean noChange = true;

        if (!(currentClinician.getStaffId()).equals(oldClinician.getStaffId())) {
            noChange = false;
        }

/*        if (!(currentClinician.getPassword()).equals(oldClinician.getPassword())) {
            noChange = false;
        }*/ //We shouldn't need this now for secure passwords

        if (!(currentClinician.getFirstName()).equals(oldClinician.getFirstName())) {
            noChange = false;
        }

        if (currentClinician.getMiddleName() != null) {
            if (!(currentClinician.getMiddleName()).equals(oldClinician.getMiddleName())) {
                noChange = false;
            }
        } else if (!middleNameTextField.getText().isEmpty()) {
            noChange = false;
        }

        if (currentClinician.getLastName() != null) {
            if (!(currentClinician.getLastName()).equals(oldClinician.getLastName())) {
                noChange = false;
            }
        } else if (!lastNameTextField.getText().isEmpty()) {
            noChange = false;
        }

        if (currentClinician.getWorkAddress() != null) {
            if (!(currentClinician.getWorkAddress()).equals(oldClinician.getWorkAddress())) {
                noChange = false;
            }
        } else if (!addressTextField.getText().isEmpty()) {
            noChange = false;
        }

        if (currentClinician.getRegion() != null) {
            if (!(currentClinician.getRegion()).equals(oldClinician.getRegion())) {
                noChange = false;
            }
        } else if (!regionTextField.getText().isEmpty()) {
            noChange = false;
        }

        if (passwordField.getText().isEmpty()) {
            noChange = false;
        }

        return noChange;
    }

    /**
     * Changes the title bar to contain an asterisk if a change was detected.
     *
     * @param field The current textfield/password field element.
     */
    private void changesListener(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            update();
        });
    }

    private void update() {
        updateUndos();
        if (checkChanges()) { // checks if reverting a textfield change restores all fields to their original state
            stage.setTitle("Update Clinician: " + currentClinician.getFirstName());
        } else {
            stage.setTitle("Update Clinician: " + currentClinician.getFirstName() + " *");
        }
    }

    /**
     * updates the undo stack
     */
    private void updateUndos() {
        boolean changed;
        changed = updateDetails(staffIDTextField.getText(), firstNameTextField.getText(),
                lastNameTextField.getText(),
                regionTextField.getText(), addressTextField.getText(), middleNameTextField.getText());

        if (changed) {
            prefillFields(currentClinician);
            currentClinician.getRedoStack().clear();
        }
        undoClinicianFormButton.setDisable(currentClinician.getUndoStack().size() <= undoMarker);
        redoClinicianFormButton.setDisable(currentClinician.getRedoStack().isEmpty());
    }

    private boolean updateDetails(String staffId, String fName, String lName, String region,
                                  String address,
                                  String mName) {
        boolean changed = false;
        if (!currentClinician.getStaffId().equals(staffId)) {
            currentClinician.setStaffId(staffId);
            changed = true;
        }

        if (!currentClinician.getFirstName().equals(fName)) {
            currentClinician.setFirstName(fName);
            changed = true;
        }

        if (currentClinician.getLastName() != null) {
            if (!currentClinician.getLastName().equals(lName)) {
                currentClinician.setLastName(lName);
                changed = true;
            }
        } else {
            if (!lName.isEmpty()) {
                currentClinician.setLastName(lName);
                changed = true;
            }
        }

        String middle = currentClinician.getMiddleName();
        if (middle != null) {
            if (!middle.equals(mName)) {
                currentClinician.setMiddleName(mName);
                changed = true;
            }
        } else {
            if (!mName.isEmpty()) {
                currentClinician.setMiddleName(mName);
                changed = true;
            }
        }

        if (currentClinician.getRegion() != null) {
            if (!currentClinician.getRegion().equals(region)) {
                currentClinician.setRegion(region);
                changed = true;
            }
        } else {
            if (!region.isEmpty()) {
                currentClinician.setRegion(region);
                changed = true;
            }
        }

        if (currentClinician.getWorkAddress() != null) {
            if (!currentClinician.getWorkAddress().equals(address)) {
                currentClinician.setWorkAddress(address);
                changed = true;
            }
        } else {
            if (!address.isEmpty()) {
                currentClinician.setWorkAddress(address);
                changed = true;
            }
        }

        return changed;
    }

    /**
     * Attempts to load the clinician overview window.
     *
     * @param clinician The current clinician.
     */
    private void loadOverview(Clinician clinician) {
        if (!newClinician) {
            ClinicianController clinicianController = AppController.getInstance()
                    .getClinicianController();
            clinicianController.showClinician(oldClinician);
            ownStage.close();

        } else {
            System.out.println(stage.getTitle());
            if (stage.getTitle().matches("Administrator*")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/clinicianView.fxml"));
                Parent root;

                try {
                    root = loader.load();
                    ClinicianController clinicianController = loader.getController();
                    Stage clinicianStage = new Stage();
                    clinicianController.init(clinicianStage, AppController.getInstance(), clinician);
                    clinicianController.disableLogout();
                    clinicianStage.setScene(new Scene(root));
                    clinicianStage.show();
                    ownStage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/clinicianView.fxml"));
                Parent root = null;

                try {
                    root = loader.load();
                    ClinicianController clinicianController = loader.getController();
                    clinicianController.init(stage, AppController.getInstance(), clinician);
                    stage.setScene(new Scene(root));
                    stage.show();
                    ownStage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * If changes are present, a pop up alert is displayed.
     * Closes the window without making any changes.
     *
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
                    removeFormChanges(0);
                    controller.updateClinicians(oldClinician);
                    loadOverview(oldClinician);
                    ownStage.close();
                }
            } else { // has no changes
                ownStage.close();
            }

        } else {
            /*FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/loginView.fxml"));
            Parent root = null;

            try {
                root = loader.load();
                LoginController loginController = loader.getController();
                loginController.init(AppController.getInstance(), stage);
                stage.setScene(new Scene(root));
                stage.show();
                stage.hide();
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            ownStage.close();
        }
    }

    /**
     * Turns all form changes into one memento on the stack
     */
    private void sumAllChanged() {
        Memento<Clinician> sumChanges = new Memento<>();
        removeFormChanges(1);
        if (!currentClinician.getUndoStack().isEmpty()) {
            sumChanges.setOldObject(currentClinician.getUndoStack().peek().getOldObject().clone());
            currentClinician.getUndoStack().pop();
            sumChanges.setNewObject(currentClinician.clone());
            currentClinician.getUndoStack().push(sumChanges);
        }
    }

    /**
     * Pops all but the specified number of changes off the stack.
     *
     * @param offset an denotes how many changes to leave in the stack.
     */
    private void removeFormChanges(int offset) {
        while (currentClinician.getUndoStack().size() > undoMarker + offset) {
            currentClinician.getUndoStack().pop();
        }
    }

    /**
     * Saves the clinician if all updated attributes are valid, otherwise error messages are displayed.
     * Upon a successful save, the window closes.
     */
    @FXML
    private void saveChanges() {
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
        boolean updatePassword = false;

        if (newClinician) {
            if (passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
                valid = false;
                emptyPasswordLabel.setVisible(true);
            }else if(passwordField.getText().equals(confirmPasswordField.getText())) {
                password = passwordField.getText();
            } else {
                valid = false;
                incorrectPasswordLabel.setVisible(true);
            }
        } else {
            if ((passwordField.getText().isEmpty() || (confirmPasswordField.getText().isEmpty()))) {
                //this stops the rest of the if statement executing if the passwords are blank avoiding NPE
            } else
                if (!(confirmPasswordField.getText()).equals(passwordField.getText()) || currentClinician.isPasswordCorrect(passwordField.getText())){
                incorrectPasswordLabel.setVisible(true);
                valid = false;
            } else {
                updatePassword = true;
                password = passwordField.getText();
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
            // updates the attributes that have changed
            updateChanges(staffID, fName, mName, lName, address, region, password, updatePassword);


            currentClinician.setDateLastModified(LocalDateTime.now()); // updates the modified date
            sumAllChanged();
            controller.updateClinicians(currentClinician); // saves the clinician
            ownStage.close(); // returns to the clinician overview window

        } else if (valid && newClinician) { // creates a new clinician
            Clinician clinician = new Clinician(staffID, password, fName, mName, lName, address, region);
            controller.updateClinicians(clinician);
            loadOverview(clinician);
        }
    }

    /**
     * Only updates the values that have been changed.
     */

    private void updateChanges(String staffID, String fName, String mName, String lName, String address, String region, String password, boolean updatePassword) {
        if (!currentClinician.getStaffId().equals(staffID)) {
            currentClinician.setStaffId(staffID);
        }

        if (updatePassword) {
            currentClinician.setPassword(password);
        }

        if (!currentClinician.getFirstName().equals(fName)) {
            currentClinician.setFirstName(fName);
        }

        String middle = currentClinician.getMiddleName();
        if (middle != null && !middle.equals(mName)) {
            currentClinician.setMiddleName(mName);
        } else if (middle == null && mName != null) {
            currentClinician.setMiddleName(mName);
        }

        String last = currentClinician.getLastName();
        if (last != null && !last.equals(lName)) {
            currentClinician.setLastName(mName);
        } else if (last == null && lName != null) {
            currentClinician.setLastName(lName);
        }

        String add = currentClinician.getWorkAddress();
        if (add != null && !add.equals(address)) {
            currentClinician.setWorkAddress(address);
        } else if (add == null && address != null) {
            currentClinician.setWorkAddress(address);
        }

        String reg = currentClinician.getRegion();
        if (reg != null && !reg.equals(region)) {
            currentClinician.setRegion(region);
        } else if (reg == null && region != null) {
            currentClinician.setRegion(region);
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

    @FXML
    public void redo(ActionEvent actionEvent) {
        currentClinician.redo();
        redoClinicianFormButton.setDisable(currentClinician.getRedoStack().isEmpty());
        prefillFields(currentClinician);
    }


    @FXML
    public void undo(ActionEvent actionEvent) {
        currentClinician.undo();
        undoClinicianFormButton.setDisable(currentClinician.getUndoStack().size() <= undoMarker);
        prefillFields(currentClinician);
    }
}
