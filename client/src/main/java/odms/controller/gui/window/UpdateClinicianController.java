package odms.controller.gui.window;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import odms.commons.model.Clinician;
import odms.commons.model._enum.Regions;
import odms.commons.model.datamodel.Address;
import odms.commons.model.datamodel.ContactDetails;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.FileSelectorController;
import odms.controller.gui.popup.utils.AlertWindowFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static odms.commons.utils.PhotoHelper.displayImage;
import static odms.commons.utils.PhotoHelper.setUpImageFile;
import static odms.commons.utils.UndoHelpers.removeFormChanges;

/**
 * controller for updating clinicians
 */
public class UpdateClinicianController {

    //<editor-fold desc="fxml stuff">
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
    private TextField streetNoTextField;

    @FXML
    private TextField streetNameTextField;

    @FXML
    private TextField neighbourhoodTextField;

    @FXML
    private TextField cityTextField;

    @FXML
    private TextField regionTextField;
    @FXML
    private ComboBox<String> regionSelector;
    @FXML
    private TextField zipCodeTextField;

    @FXML
    private ComboBox<String> countrySelector;
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

    @FXML
    private ImageView profileImage;

    @FXML
    private Button resetProfileImageClinician;
    //</editor-fold>

    private AppController controller;
    private Stage stage;
    private Clinician currentClinician;
    private Clinician oldClinician;
    private boolean newClinician;
    private int undoMarker;
    private Stage ownStage;
    private File inFile;
    private String defaultCountry = "New Zealand";

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
        countrySelector.setItems(FXCollections.observableList(controller.getAllowedCountries()));
        for (Regions regions : Regions.values()) {
            regionSelector.getItems().add(regions.toString());
        }

        if (!newClinician) {
            oldClinician = Clinician.clone(clinician);
            undoMarker = currentClinician.getUndoStack().size();
            ownStage.setTitle("Update Clinician: " + clinician.getFirstName());
            titleLabel.setText("Update Clinician");
            confirmButton.setText("Save Changes");

            prefillFields(clinician);

            // checks if there was a change in any of the clinician input fields
            changesListener(staffIDTextField);
            changesListener(passwordField);
            changesListener(confirmPasswordField);
            changesListener(firstNameTextField);
            changesListener(middleNameTextField);
            changesListener(lastNameTextField);
            changesListener(streetNoTextField);
            changesListener(streetNameTextField);
            changesListener(neighbourhoodTextField);
            changesListener(cityTextField);
            changesListener(regionTextField);
            changesListener(zipCodeTextField);

            comboBoxListener(regionSelector);
            comboBoxListener(countrySelector);

            Scene scene = ownStage.getScene();

            if (currentClinician.getStaffId().equals("0")) {
                staffIDTextField.setDisable(true); // default clinician cannot change their staff ID
            }

            final KeyCombination shortcutZ = new KeyCodeCombination(
                    KeyCode.Z, KeyCombination.CONTROL_DOWN);

            scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
                if (shortcutZ.match(e)) {
                    undo();
                }
            });

            if(! clinician.getCountry().equals(defaultCountry)){
                regionSelector.setVisible(false);
                regionTextField.setVisible(true);
            }


        } else {
            ownStage.setTitle("Create New Clinician Profile");
            titleLabel.setText("Create Clinician");
            confirmButton.setText("Create Clinician Profile");
            countrySelector.setValue("");
            regionSelector.setValue("");
        }

        ownStage.setOnCloseRequest(event -> cancelUpdate());
    }

    /**
     * If New Zealand is selected at the country combo box, the region combo box will appear.
     * If country other than New Zealand is selected at the country combo box, the region combo box will
     * be replaced with a text field.
     * region text field is cleared by default when it appears.
     * region combo box selects the first item by default when it appears.
     * @param event from GUI
     */
    @FXML
    private void countrySelectorListener(ActionEvent event) {
        controller.countrySelectorEventHandler(countrySelector, regionSelector, regionTextField);
    }

    /**
     * sets the profile photo back to the default image
     */
    @FXML
    private void resetProfileImage() {
        ClassLoader classLoader = getClass().getClassLoader();
        inFile = new File(classLoader.getResource("default-profile-picture.jpg").getFile());
        currentClinician.setProfilePhotoFilePath(inFile.toString());
        displayImage(profileImage, inFile.getPath());
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
        String streetNo = clinician.getStreetNumber();
        String streetName = clinician.getStreetName();
        String neighbourhood = clinician.getNeighborhood();
        String city = clinician.getCity();
        String region = clinician.getRegion() == null ? "": clinician.getRegion();
        String zipCode = clinician.getZipCode();
        String country = clinician.getCountry();

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

        streetNoTextField.setText(streetNo);
        streetNameTextField.setText(streetName);
        neighbourhoodTextField.setText(neighbourhood);
        cityTextField.setText(city);
        zipCodeTextField.setText(zipCode);
        countrySelector.getSelectionModel().select(country);

        if(!country.equals(defaultCountry)) {
            regionTextField.setText(region);

        } else {
            regionSelector.getSelectionModel().select(region); //region selector is visible by default if clinician's country is NZ.
        }
        displayImage(profileImage, currentClinician.getProfilePhotoFilePath());
    }

    /**
     * Changes the title bar to add/remove an asterisk when a change is detected on the ComboBox.
     *
     * @param cb The current ComboBox.
     */
    private void comboBoxListener(ComboBox cb) {
        cb.valueProperty().addListener((observable, oldValue, newValue) -> update());
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
     * uploads an image using file picker. includes validation.
     */
    @FXML
    private void uploadImage() {
        boolean isValid = true;
        String filename;
        List<String> extensions = new ArrayList<>();
        extensions.add("*.png");
        extensions.add("*.jpg");
        extensions.add("*.gif");
        FileSelectorController fileSelectorController =  new FileSelectorController();
        filename = fileSelectorController.getFileSelector(stage, extensions);

        if (filename != null) {
            inFile = new File(filename);

            if (inFile.length() > 2000000) { //if more than 2MB
                Alert imageTooLargeAlert = new Alert(Alert.AlertType.WARNING, "Could not upload the image as the image size exceeded 2MB");
                imageTooLargeAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                imageTooLargeAlert.showAndWait();
                isValid = false;
            }
            if (isValid) {
                update();
                displayImage(profileImage, inFile.getPath());
                currentClinician.setProfilePhotoFilePath(filename);
            }
        }
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
     * gets the region from text input field or
     * selected by users from combo box
     * @return region from combo box or text input
     */
    private String getRegionInput(){
        if(regionTextField.isVisible()){
            return regionTextField.getText();
        }
        return regionSelector.getSelectionModel().getSelectedItem();
    }

    /**
     * updates the undo stack
     */
    private void updateUndos() {
        boolean changed;
        String photoPath;
        if (inFile != null) {
            photoPath = inFile.getPath();
        } else {
            photoPath = currentClinician.getProfilePhotoFilePath();
        }

        changed = updateDetails(staffIDTextField.getText(), firstNameTextField.getText(),
                lastNameTextField.getText(), middleNameTextField.getText(), photoPath);
        boolean addressChanged;
        addressChanged = updateAddress(streetNoTextField.getText(), streetNameTextField.getText(), neighbourhoodTextField.getText(),
                cityTextField.getText(), getRegionInput(), zipCodeTextField.getText(), countrySelector.getSelectionModel().getSelectedItem());

        if (changed || addressChanged) {
            prefillFields(currentClinician);
            currentClinician.getRedoStack().clear();
        }
        undoClinicianFormButton.setDisable(currentClinician.getUndoStack().size() <= undoMarker);
        redoClinicianFormButton.setDisable(currentClinician.getRedoStack().isEmpty());
    }



    /**
     * Updates personal details of the current clinician to match the given values
     *
     * @param staffId StaffID that may have been changed
     * @param fName First name that may have been changed
     * @param lName Last name that may have been changed
     * @param mName Middle name that may have been changed
     * @return true if any of the clinicians personal details has changed, false otherwise
     */
    private boolean updateDetails(String staffId, String fName, String lName, String mName, String photoPath) {
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

        if (!currentClinician.getProfilePhotoFilePath().equals(photoPath)) {
            currentClinician.setProfilePhotoFilePath(photoPath);
            changed = true;
        }

        return changed;
    }

    /**
     * Updates the work address of the current clinician to match the given values
     *
     * @param streetNumber Street number that may have changed
     * @param streetName Street name that may have been changed
     * @param neighbourhood Neighbourhood that may have changed
     * @param city City that may have changed
     * @param region Region that may have been changed
     * @param zipcode Zip code that may have changed
     * @param country Country that may have changed
     * @return true if there has been a change and any of the address values, false otherwise
     */
    private boolean updateAddress(String streetNumber, String streetName, String neighbourhood, String city, String region, String zipcode, String country) {
        boolean changed = false;

        if (!currentClinician.getStreetNumber().equals(streetNumber)) {
            currentClinician.setStreetNumber(streetNumber);
            changed = true;
        }

        if (currentClinician.getStreetName() != null) {
            if (!currentClinician.getStreetName().equals(streetName)) {
                currentClinician.setStreetName(streetName);
                changed = true;
            }
        } else {
            if (!streetName.isEmpty()) {
                currentClinician.setStreetName(streetName);
                changed = true;
            }
        }

        if (!currentClinician.getNeighborhood().equals(neighbourhood)) {
            currentClinician.setNeighborhood(neighbourhood);
            changed = true;
        }

        if (!currentClinician.getCity().equals(city)) {
            currentClinician.setCity(city);
            changed = true;
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

        if (currentClinician.getZipCode() == null || !currentClinician.getZipCode().equals(zipcode)) {
            currentClinician.setZipCode(zipcode);
            changed = true;
        }

        if (!currentClinician.getCountry().equals(country)) {
            currentClinician.setCountry(country);
            changed = true;
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
                    clinicianController.init(clinicianStage, AppController.getInstance(), clinician, true, null);
                    clinicianStage.setScene(new Scene(root));
                    clinicianStage.show();
                    ownStage.close();
                    Log.info("successfully launched clinician overview window for Clinician Staff Id: " + clinician.getStaffId());
                } catch (IOException e) {
                    Log.severe("failed to load clinician overview window for Clinician Staff Id: " + clinician.getStaffId(), e);
                }

            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/clinicianView.fxml"));
                Parent root;

                try {
                    root = loader.load();
                    ClinicianController clinicianController = loader.getController();
                    clinicianController.init(stage, AppController.getInstance(), clinician, false, null);
                    stage.setScene(new Scene(root));
                    stage.show();
                    ownStage.close();
                    Log.info("successfully launched clinician overview window for Clinician Staff Id: " + clinician.getStaffId());
                } catch (IOException e) {
                    Log.severe("failed to load clinician overview window for Clinician Staff Id: " + clinician.getStaffId(), e);
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
            if (!undoClinicianFormButton.isDisabled() || !passwordField.getText().isEmpty() || !confirmPasswordField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                        "You have unsaved changes, are you sure you want to cancel?",
                        ButtonType.YES, ButtonType.NO);

                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
                yesButton.setId("yesButton");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.YES) {
                    Log.info("Clinician update cancelled for Clinician Staff Id: " + currentClinician.getStaffId());
                    removeFormChanges(0, currentClinician, undoMarker);
                    currentClinician.getRedoStack().clear();
                    controller.updateClinicians(oldClinician);
                    loadOverview(oldClinician);
                    ownStage.close();
                }
            } else { // has no changes
                currentClinician.getRedoStack().clear();
                ownStage.close();
                Log.info("no changes made to Clinician Staff Id: " + currentClinician.getStaffId());
            }

        } else {
            ownStage.close();
        }
    }

    /**
     * NO LONGER NEEDED I THINK
     * Turns all form changes into one memento on the stack
     */
    private void sumAllChanged() {
        removeFormChanges(1, currentClinician, undoMarker);
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

        String password = "";
        String fName = "";
        String mName = "";
        String lName = "";
        String streetNumber = streetNoTextField.getText();
        String streetName = "";
        String neighbourhood = neighbourhoodTextField.getText();
        String city = cityTextField.getText();
        String region = "";
        String zipCode = zipCodeTextField.getText();
        String country = countrySelector.getSelectionModel().getSelectedItem();
        boolean updatePassword = false;

        if (newClinician) {
            if (passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
                valid = false;
                emptyPasswordLabel.setVisible(true);
            } else if (passwordField.getText().equals(confirmPasswordField.getText())) {
                password = passwordField.getText();
            } else {
                valid = false;
                incorrectPasswordLabel.setVisible(true);
            }
        } else {
            if ((passwordField.getText().isEmpty() || (confirmPasswordField.getText().isEmpty()))) {
                //this stops the rest of the if statement executing if the passwords are blank avoiding NPE
            } else if (!(confirmPasswordField.getText()).equals(passwordField.getText()) || currentClinician.isPasswordCorrect(passwordField.getText())) {
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

        if (!(streetNameTextField.getText()).isEmpty()) {
            streetName = streetNameTextField.getText();
        }

        region = getRegionInput();
        if (region.isEmpty()) {
            emptyRegionLabel.setVisible(true);
            valid = false;
        }

        if (valid && !newClinician) { // updates an existing clinician
            // updates the attributes that have changed
            updateChanges(staffID, fName, mName, lName, password, updatePassword);
            updateWorkChanges(streetNumber, streetName, neighbourhood, city, region, zipCode, country);

            currentClinician.setDateLastModified(LocalDateTime.now()); // updates the modified date
            sumAllChanged();
            if(inFile != null) {
                try {
                    String filePath = setUpImageFile(inFile, currentClinician.getStaffId());
                    currentClinician.setProfilePhotoFilePath(filePath);

                    //currentClinician.getUndoStack().pop();

                } catch (IOException e) {
                    System.err.println(e);
                }
            }
            currentClinician.getRedoStack().clear();
            controller.updateClinicians(currentClinician); // saves the clinician
            ownStage.close(); // returns to the clinician overview window
            Log.info("Clinician updated for Clinician Staff Id: " + staffID);

        } else if (valid && newClinician) { // creates a new clinician
            Clinician clinician = new Clinician(staffID, password, fName, mName, lName);
            currentClinician = clinician;
            Address workAddress = new Address(streetNumber, streetName, neighbourhood, city, region, zipCode, country);
            clinician.setWorkContactDetails(new ContactDetails("", "", workAddress, ""));
            resetProfileImage();
            controller.updateClinicians(clinician);
            try {
                controller.saveClinician(clinician);
            } catch (IOException e) {
                AlertWindowFactory.generateError(e);
            }
            loadOverview(clinician);

        } else {
            Log.warning("Clinician not updated for Clinician Staff Id: " + staffID);
        }
    }

    /**
     * Only updates the personal detail values that have been changed.
     *
     * @param staffID The clinicians unique staff identifier
     * @param fName The clinicians first name
     * @param mName The clinicians middle name
     * @param lName The clinicians last name
     * @param password The clinicians account password
     * @param updatePassword A boolean flag indicating if the password has changed or not
     */
    private void updateChanges(String staffID, String fName, String mName, String lName, String password, boolean updatePassword) {
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
    }

    /**
     * Only updates the work address values that have changed
     *
     * @param streetNumber Street number of the work address
     * @param streetName Street name that of the work address
     * @param neighbourhood Neighbourhood that of the work address
     * @param city City the clinicians work place is located in
     * @param region Region the clinicians work place is located in
     * @param zipCode Zip code of the area the work place is located in
     * @param country Country that the clinicians work place is located in
     */
    private void updateWorkChanges(String streetNumber, String streetName, String neighbourhood, String city, String region, String zipCode, String country) {
        if (!currentClinician.getStreetNumber().equals(streetNumber)) {
            currentClinician.setStreetNumber(streetNumber);
        }

        if (!currentClinician.getStreetName().equals(streetName)) {
            currentClinician.setStreetName(streetName);
        }

        if (!currentClinician.getNeighborhood().equals(neighbourhood)) {
            currentClinician.setNeighborhood(neighbourhood);
        }

        if (!currentClinician.getCity().equals(city)) {
            currentClinician.setCity(city);
        }

        if (!currentClinician.getRegion().equals(region)) {
            currentClinician.setRegion(region);
        }

        if (currentClinician.getZipCode() == null || !currentClinician.getZipCode().equals(zipCode)) {
            currentClinician.setZipCode(zipCode);
        }

        if (!currentClinician.getCountry().equals(country)) {
            currentClinician.setCountry(country);
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
        Log.info("Redo executed for Clinician Staff Id: " + currentClinician.getStaffId());
    }


    /**
     * Undoes the previous action
     */
    @FXML
    public void undo() {
        currentClinician.undo();
        undoClinicianFormButton.setDisable(currentClinician.getUndoStack().size() <= undoMarker);
        prefillFields(currentClinician);
        Log.info("Undo executed for Clinician Staff Id: " + currentClinician.getStaffId());
    }
}
