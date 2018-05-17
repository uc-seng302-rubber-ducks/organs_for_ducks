package seng302.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
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
            changesListener(passwordField);
            changesListener(confirmPasswordField);
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
                  undo();
                }
            });

        } else {
            ownStage.setTitle("Create New Clinician Profile");
            titleLabel.setText("Create Clinician");
            confirmButton.setText("Create Clinician Profile");
        }

      ownStage.setOnCloseRequest(event -> cancelUpdate());
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
     * Changes the title bar to contain an asterisk if a change was detected.
     *
     * @param field The current textfield/password field element.
     */
    private void changesListener(TextField field) {
      field.textProperty().addListener((observable, oldValue, newValue) -> update());
    }

    /**
     * Updates the title bar depending on changes made to the user fields.
     */
    private void update() {
        updateUndos();
        if (undoClinicianFormButton.isDisabled() && passwordField.getText().isEmpty() && confirmPasswordField.getText().isEmpty()) {
            ownStage.setTitle("Update Clinician: " + currentClinician.getFirstName());
        } else if (!ownStage.getTitle().endsWith("*")) {
            ownStage.setTitle(ownStage.getTitle() + " *");
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
            if (stage.getTitle().matches("Administrator*")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/clinicianView.fxml"));
                Parent root;

                try {
                    root = loader.load();
                    ClinicianController clinicianController = loader.getController();
                    Stage clinicianStage = new Stage();
                    clinicianController.init(clinicianStage, AppController.getInstance(), clinician, false);
                    clinicianController.disableLogout();
                    clinicianStage.setScene(new Scene(root));
                    clinicianStage.show();
                    ownStage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/clinicianView.fxml"));
              Parent root;

                try {
                    root = loader.load();
                    ClinicianController clinicianController = loader.getController();
                    clinicianController.init(stage, AppController.getInstance(), clinician, false);
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
     */
    @FXML
    private void cancelUpdate() {

        if (!newClinician) {
            if (!undoClinicianFormButton.isDisabled()) {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                        "You have unsaved changes, are you sure you want to cancel?",
                        ButtonType.YES, ButtonType.NO);

                Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
                yesButton.setId("yesButton");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.YES) {
                    removeFormChanges(0);
                    currentClinician.getRedoStack().clear();
                    controller.updateClinicians(oldClinician);
                    loadOverview(oldClinician);
                    ownStage.close();
                }
            } else { // has no changes
                currentClinician.getRedoStack().clear();
                ownStage.close();
            }

        } else {
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
            currentClinician.getRedoStack().clear();
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

  /**
   * Redoes the previous undone action
   */
  @FXML
  public void redo() {
    currentClinician.redo();
    redoClinicianFormButton.setDisable(currentClinician.getRedoStack().isEmpty());
    prefillFields(currentClinician);
  }


  /**
   * Undoes the previous action
   */
  @FXML
  public void undo() {
    currentClinician.undo();
    undoClinicianFormButton.setDisable(currentClinician.getUndoStack().size() <= undoMarker);
    prefillFields(currentClinician);
  }
}
