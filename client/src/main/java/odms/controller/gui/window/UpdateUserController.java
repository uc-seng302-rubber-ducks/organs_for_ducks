package odms.controller.gui.window;


import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import odms.commons.exception.InvalidFieldsException;
import odms.commons.model.EmergencyContact;
import odms.commons.model.User;
import odms.commons.model._enum.Regions;
import odms.commons.utils.AttributeValidation;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.FileSelectorController;
import org.apache.commons.lang.ObjectUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.Buffer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static odms.commons.utils.PhotoHelper.displayImage;
import static odms.commons.utils.PhotoHelper.setUpImageFile;

/**
 * Class for updating the user
 */
public class UpdateUserController {

    private final int MAX_FILE_SIZE = 2097152;
    //<editor-fold desc="fxml stuff">
    @FXML
    private Label errorLabel;
    @FXML
    private Label existingNHI;
    @FXML
    private Label invalidNHI;
    @FXML
    private Label invalidFirstName;
    @FXML
    private Label invalidDOB;
    @FXML
    private TextField nhiInput;
    @FXML
    private TextField fNameInput;
    @FXML
    private TextField preferredFNameTextField;
    @FXML
    private TextField mNameInput;
    @FXML
    private TextField lNameInput;
    @FXML
    private TextField heightInput;
    @FXML
    private TextField weightInput;
    @FXML
    private TextField phone;
    @FXML
    private TextField cell;
    @FXML
    private TextField streetNumber;
    @FXML
    private TextField street;
    @FXML
    private TextField neighborhood;
    @FXML
    private TextField city;
    @FXML
    private TextField regionInput;
    @FXML
    private ComboBox<String> regionSelector;
    @FXML
    private TextField zipCode;
    @FXML
    private ComboBox<String> countrySelector;
    @FXML
    private TextField email;
    @FXML
    private TextField ecName;
    @FXML
    private TextField ecPhone;
    @FXML
    private TextField ecCell;
    @FXML
    private TextField ecStreetNumber;
    @FXML
    private TextField ecStreet;
    @FXML
    private TextField ecNeighborhood;
    @FXML
    private TextField ecCity;
    @FXML
    private TextField ecRegionInput;
    @FXML
    private ComboBox<String> ecRegionSelector;
    @FXML
    private TextField ecZipCode;
    @FXML
    private ComboBox<String> ecCountrySelector;
    @FXML
    private TextField ecEmail;
    @FXML
    private TextField ecRelationship;
    @FXML
    private ComboBox<String> birthGenderComboBox;
    @FXML
    private ComboBox<String> genderIdComboBox;
    @FXML
    private ComboBox<String> bloodComboBox;
    @FXML
    private CheckBox smokerCheckBox;
    @FXML
    private ComboBox<String> alcoholComboBox;
    @FXML
    private DatePicker dobInput;
    @FXML
    private Button undoUpdateButton;
    @FXML
    private Button redoUpdateButton;
    @FXML
    private ImageView profileImage;
    //</editor-fold>
    @FXML
    private Button resetProfileImageUser;
    private Stage stage;
    private AppController appController;
    private User currentUser;
    private User oldUser;
    private int undoMarker; //int used to hold the top of the stack before opening this window
    private boolean listen = true;
    private File inFile;
    private String defaultCountry = "New Zealand";
    private UserController userController;

    /**
     * @param user       The current user.
     * @param controller An instance of the AppController class.
     * @param stage      The applications stage.
     */
    public void init(User user, AppController controller, Stage stage, UserController userController) {
        countrySelector.setItems(FXCollections.observableList(controller.getAllowedCountries()));
        ecCountrySelector.setItems(FXCollections.observableList(controller.getAllowedCountries()));
        for (Regions regions : Regions.values()) {
            regionSelector.getItems().add(regions.toString());
            ecRegionSelector.getItems().add(regions.toString());
        }


        this.userController = userController;

        this.stage = stage;
        oldUser = user;
        currentUser = User.clone(oldUser);
        this.appController = controller;
        setUserDetails(currentUser);
        undoUpdateButton.setDisable(true);
        redoUpdateButton.setDisable(true);
        errorLabel.setText("");
        undoMarker = currentUser.getUndoStack().size();
        if (user.getLastName() != null) {
            stage.setTitle("Update User: " + user.getFirstName() + " " + user.getLastName());
        } else {
            stage.setTitle("Update User: " + user.getFirstName());
        }

        Scene scene = stage.getScene();

        final TextField[] allTextFields = {nhiInput, fNameInput, preferredFNameTextField, mNameInput,
                lNameInput,
                heightInput, weightInput, phone, cell, street, streetNumber, city, neighborhood, zipCode, email,
                ecName, ecPhone, ecCell, ecEmail, ecStreet, ecStreetNumber, ecCity, ecNeighborhood, ecZipCode,
                ecRelationship, regionInput, ecRegionInput};

        // creates a listener for each text field
        for (TextField tf : allTextFields) {
            textFieldListener(tf);
        }

        comboBoxListener(birthGenderComboBox);
        comboBoxListener(genderIdComboBox);
        comboBoxListener(bloodComboBox);
        comboBoxListener(alcoholComboBox);
        comboBoxListener(regionSelector);
        comboBoxListener(ecRegionSelector);
        comboBoxListener(countrySelector);
        comboBoxListener(ecCountrySelector);

        datePickerListener(dobInput);

        addCheckBoxListener(smokerCheckBox);

        final KeyCombination shortcutZ = new KeyCodeCombination(
                KeyCode.Z, KeyCombination.SHORTCUT_DOWN);

        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if (shortcutZ.match(e)) {
                undo();
            }
        });

        stage.setOnCloseRequest(event -> {
            event.consume();
            goBack();
        });
    }

    /**
     * If New Zealand is selected at the country combo box, the region combo box will appear.
     * If country other than New Zealand is selected at the country combo box, the region combo box will
     * be replaced with a text field.
     * region text field is cleared by default when it appears.
     * region combo box selects the first item by default when it appears.
     *
     * @param event from GUI
     */
    @FXML
    private void countrySelectorListener(ActionEvent event) {
//        if (listen) {
        appController.countrySelectorEventHandler(countrySelector, regionSelector, regionInput, currentUser, null);
//        }
    }

    /**
     * If New Zealand is selected at the country combo box, the region combo box will appear.
     * If country other than New Zealand is selected at the country combo box, the region combo box will
     * be replaced with a text field.
     * region text field is cleared by default when it appears.
     * region combo box selects the first item by default when it appears.
     *
     * @param event from GUI
     */
    @FXML
    private void ecCountrySelectorListener(ActionEvent event) {
//        if (listen) {
        appController.countrySelectorEventHandler(ecCountrySelector, ecRegionSelector, ecRegionInput, currentUser, null);
//        }
    }

    /**
     * Calls a method to update the undo stack and changes the stage title appropriately.
     * Adds an asterisk to the stage title when the undo button is clickable.
     * Removes an asterisk from the stage title when the undo button is disabled.
     */
    private void update() {
        updateUndos();
        if (!undoUpdateButton.isDisabled() && !stage.getTitle().endsWith("*")) {
            stage.setTitle(stage.getTitle() + "*");
        }

    }

    /**
     * Changes the title bar to add/remove an asterisk when a change was detected on the date picker.
     *
     * @param dp The current date picker.
     */
    private void datePickerListener(DatePicker dp) {
        dp.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (listen) {
                update();
            }
        });
    }

    /**
     * Listens for changes on the given checkbox and calls for updates.
     *
     * @param checkBox The given CheckBox.
     */
    private void addCheckBoxListener(CheckBox checkBox) {
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (listen) {
                update();
            }
        });
    }

    /**
     * Changes the title bar to add/remove an asterisk when a change is detected on the ComboBox.
     *
     * @param cb The current ComboBox.
     */
    private void comboBoxListener(ComboBox cb) {
        cb.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (listen) {
                update();
            }
        });
    }

    /**
     * Changes the title bar to contain an asterisk if a change was detected on the textfields.
     *
     * @param field The current textfield.
     */
    private void textFieldListener(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (listen) {
                update();
            }
        });

    }

    /**
     * Sets the details for the current user
     *
     * @param user The current user.
     */
    @FXML
    private void setUserDetails(User user) {
        //personal
        String region = user.getRegion() == null ? "" : user.getRegion();
        String country = user.getCountry();

        listen = false;
        fNameInput.setText(user.getFirstName());

        nhiInput.setText(user.getNhi());
        if (user.getLastName() != null) {
            lNameInput.setText(user.getLastName());
        } else {
            lNameInput.setText("");
        }

        if (user.getMiddleName() != null) {
            mNameInput.setText(user.getMiddleName());
        } else {
            mNameInput.setText("");
        }

        if (user.getPreferredFirstName() != null) {
            preferredFNameTextField.setText(user.getPreferredFirstName());
        } else {
            preferredFNameTextField.setText("");
        }

        dobInput.setValue(user.getDateOfBirth());


        if (user.getStreetNumber() != null) {
            streetNumber.setText(user.getStreetNumber());
        } else {
            streetNumber.setText("");
        }

        if (user.getStreetName() != null) {
            street.setText(user.getStreetName());
        } else {
            street.setText("");
        }
        if (user.getCity() != null) {
            city.setText(user.getCity());
        } else {
            city.setText("");
        }

        //Set the correct default country
        if (user.getCountry().equals("") || user.getCountry() == null) {
            if (appController.getAllowedCountries().contains(defaultCountry) || appController.getAllowedCountries().isEmpty()) {
                countrySelector.setValue(defaultCountry);
                user.setCountryNoUndo(defaultCountry);
            } else {
                countrySelector.setValue(appController.getAllowedCountries().get(0));
                user.setCountryNoUndo(appController.getAllowedCountries().get(0));

            }
        } else {
            countrySelector.setValue(user.getCountry());
        }
        if (user.getContact().getAddress().getCountry().equals("") || user.getContact().getAddress().getCountry() == null) {
            if (appController.getAllowedCountries().contains(defaultCountry) || appController.getAllowedCountries().isEmpty()) {
                ecCountrySelector.setValue(defaultCountry);
                user.getContact().getAddress().setCountry(defaultCountry);
            } else {
                ecCountrySelector.setValue(appController.getAllowedCountries().get(0));
                user.getContact().getAddress().setCountry(appController.getAllowedCountries().get(0));
            }
        } else {
            ecCountrySelector.setValue(user.getContact().getAddress().getCountry());
        }

        if (user.getNeighborhood() != null) {
            neighborhood.setText(user.getNeighborhood());
        } else {
            neighborhood.setText("");
        }

        if (countrySelector.getSelectionModel().getSelectedItem() != null && !countrySelector.getSelectionModel().getSelectedItem().equals(defaultCountry)) {
            regionInput.setVisible(true);
            regionInput.setText(region);
            regionSelector.setVisible(false);

        } else {
            regionSelector.setValue(region); //region selector is visible by default if clinician's country is NZ.
        }

        if (user.getZipCode() != null) {
            zipCode.setText(user.getZipCode());
        } else {
            zipCode.setText("");
        }

        if (user.getCellPhone() != null) {
            cell.setText(user.getCellPhone());
        } else {
            cell.setText("");
        }
        if (user.getHomePhone() != null) {
            phone.setText(user.getHomePhone());
        } else {
            phone.setText("");
        }
        if (user.getEmail() != null) {
            email.setText(user.getEmail());
        } else {
            email.setText("");
        }
        if (user.getProfilePhotoFilePath() != null) {
            displayImage(profileImage, user.getProfilePhotoFilePath());
        }
        String ecRegion = user.getRegion() == null ? "" : user.getContact().getRegion();
        String ecCountry = user.getContact().getCountry();

        if (user.getContact() != null) {
            if (user.getContact().getName() != null) {
                ecName.setText(user.getContact().getName());
            } else {
                ecName.setText("");
            }
            if (user.getContact().getRelationship() != null) {
                ecRelationship.setText(user.getContact().getRelationship());
            } else {
                ecRelationship.setText("");
            }

            if (ecCountry.isEmpty()) {
                ecRegionSelector.setValue(ecRegion);
            }

            if (ecCountrySelector.getSelectionModel().getSelectedItem() != null && !ecCountrySelector.getSelectionModel().getSelectedItem().equals(defaultCountry)) {
                ecRegionInput.setVisible(true);
                ecRegionInput.setText(ecRegion);
                ecRegionSelector.setVisible(false);

            } else { //if ecCountry == NZ
                ecRegionSelector.getSelectionModel().select(ecRegion); //region selector is visible by default if clinician's country is NZ.
            }
            if (user.getContact().getHomePhoneNumber() != null) {
                ecPhone.setText(user.getContact().getHomePhoneNumber());
            } else {
                ecPhone.setText("");
            }
            if (user.getContact().getEmail() != null) {
                ecEmail.setText(user.getContact().getEmail());
            } else {
                ecEmail.setText("");
            }

            if (user.getContact().getStreetNumber() != null) {
                ecStreetNumber.setText(user.getContact().getStreetNumber());
            } else {
                ecStreetNumber.setText("");
            }

            if (user.getContact().getStreetName() != null) {
                ecStreet.setText(user.getContact().getStreetName());
            } else {
                ecStreet.setText("");
            }
            if (user.getContact().getCity() != null) {
                ecCity.setText(user.getContact().getCity());
            } else {
                ecCity.setText("");
            }

            if (user.getContact().getNeighborhood() != null) {
                ecNeighborhood.setText(user.getContact().getNeighborhood());
            } else {
                ecNeighborhood.setText("");
            }

            if (user.getContact().getZipCode() != null) {
                ecZipCode.setText(user.getContact().getZipCode());
            } else {
                ecZipCode.setText("");
            }

            if (user.getContact().getCellPhoneNumber() != null) {
                ecCell.setText(user.getContact().getCellPhoneNumber());
            } else {
                ecCell.setText("");
            }
        }
        // health details
        alcoholComboBox
                .setValue(user.getAlcoholConsumption() == null ? "None" : user.getAlcoholConsumption());
        if (user.isSmoker()) {
            smokerCheckBox.setSelected(true);
        } else {
            smokerCheckBox.setSelected(false);
        }

        bloodComboBox.setValue(user.getBloodType() == null ? "" : user.getBloodType());

        birthGenderComboBox.setValue(user.getBirthGender() == null ? "" : user.getBirthGender());

        genderIdComboBox.setValue(user.getGenderIdentity() == null ? "" : user.getGenderIdentity());


        user.setWeightText(Double.toString(user.getWeight()));
        if (user.getWeightText() != null && !user.getWeightText().equals("")) {
            weightInput.setText(user.getWeightText());
        } else if (user.getWeight() > 0) {
            weightInput.setText(Double.toString(user.getWeight()));
        } else {
            weightInput.setText("");
        }

        user.setHeightText(Double.toString(user.getHeight()));
        if (user.getHeightText() != null && !user.getHeightText().equals("")) {
            heightInput.setText(user.getHeightText());
        } else if (user.getHeight() > 0) {
            heightInput.setText(Double.toString(user.getHeight()));
        } else {
            heightInput.setText("0.0");
        }

        listen = true;

        undoUpdateButton.setDisable(currentUser.getUndoStack().size() <= undoMarker);
        redoUpdateButton.setDisable(currentUser.getRedoStack().isEmpty());

    }

    /**
     * sets the profile photo back to the default image
     */
    @FXML
    private void resetProfileImageOld() {
        ClassLoader classLoader = getClass().getClassLoader();
        inFile = new File(classLoader.getResource("default-profile-picture.jpg").getFile());

        currentUser.setProfilePhotoFilePath(inFile.getPath());
        displayImage(profileImage, inFile.getPath());
    }

    @FXML
    private void resetProfileImage() {
        ClassLoader classLoader = getClass().getClassLoader();
        inFile = new File(classLoader.getResource("default-profile-picture.jpg").getFile());
        currentUser.setProfilePhotoFilePath(inFile.getPath());

        URL url = getClass().getResource("/default-profile-picture.jpg");
        displayImage(profileImage, url);
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
        FileSelectorController fileSelectorController = new FileSelectorController();
        filename = fileSelectorController.getFileSelector(stage, extensions);
        if (filename != null) {
            inFile = new File(filename);

            if (inFile.length() > MAX_FILE_SIZE) { //if more than 2MB
                Alert imageTooLargeAlert = new Alert(Alert.AlertType.WARNING, "Could not upload the image as the image size exceeded 2MB");
                imageTooLargeAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                imageTooLargeAlert.showAndWait();
                isValid = false;
            }
            if (isValid) {
                update();
                displayImage(profileImage, inFile.getPath());
                currentUser.setProfilePhotoFilePath(inFile.getPath());
            }
        }
    }

    /**
     *
     */
    @FXML
    public void confirmUpdate() throws IOException {

        hideErrorMessages();
        errorLabel.setText("Please make sure your details are correct.");
        boolean valid = validateFields();


        if (valid) {
            removeFormChanges();
            if(inFile != null){
                String filePath = setUpImageFile(inFile, currentUser.getNhi());
                currentUser.setProfilePhotoFilePath(filePath);
            }
            try {
                currentUser.getRedoStack().clear();
                oldUser.setDeleted(true);
                userController.showUser(currentUser);
                Log.info("Update User Successful for User NHI: " + currentUser.getNhi());
            } catch (NullPointerException ex) {
                //TODO causes npe if donor is new in this session
                //the text fields etc. are all null
                Log.severe("Update user failed for User NHI: " + currentUser.getNhi(), ex);
            }
            stage.close();
        }
    }

    /**
     * Pops all but the specified number of changes off the stack.
     */
    private void removeFormChanges() {
        while (currentUser.getUndoStack().size() > undoMarker + 1) {
            currentUser.getUndoStack().pop();
        }
    }

    /**
     * Checks if all fields that require validation are valid.
     * Sets error messages visible if fields are invalid.
     * Calls methods to update the changes if all fields are valid.
     */
    private boolean validateFields() {
        boolean valid;
        String nhi = nhiInput.getText();
        valid = AttributeValidation.validateNHI(nhi);
        if (!valid) {
            invalidNHI.setVisible(true);
        } else {
            if (appController.getUserBridge().getExists(nhi) && !currentUser.getNhi().equals(nhi)) { // if a user was found, but it is not the current user
                existingNHI.setVisible(true);
                valid = false;
            }
        }

        LocalDate dob = dobInput.getValue();

        String fName = fNameInput.getText();
        valid &= AttributeValidation.checkRequiredString(fName);
        if (!valid) {
            invalidFirstName.setVisible(true);
        }

        valid &= AttributeValidation.validateDateOfBirth(dob);
        if (!valid) {
            invalidDOB.setVisible(true);
        }

        double height = AttributeValidation.validateDouble(heightInput.getText());
        double weight = AttributeValidation.validateDouble(weightInput.getText());
        if (height == -1 || weight == -1) {
            errorLabel.setVisible(true);
            valid = false;
        }

        // validate contact info
        String email = this.email.getText();
        valid &= AttributeValidation.validateEmail(email);
        if (!valid) {
            errorLabel.setVisible(true);
        }

        String homePhone = phone.getText();
        valid &= AttributeValidation.validatePhoneNumber(homePhone.replaceAll(" ", ""));
        if (!valid) {
            errorLabel.setVisible(true);
        }

        String cellPhone = cell.getText();
        valid &= AttributeValidation.validateCellNumber(cellPhone.replaceAll(" ", ""));
        if (!valid) {
            errorLabel.setVisible(true);
        }

        try {
            validateEmergencyContactDetails();
        } catch (InvalidFieldsException e) {
            valid = false;
        }

        if (valid) { // only updates if everything is valid
            appController.update(currentUser);
        }
        if(!currentUser.getNhi().equals(nhi) && AppController.getInstance().getUserBridge().getExists(nhi)){
            valid = false;
        }
        return valid;
    }

    private boolean checkChangedProperty(String newString, String oldString) {
        oldString = oldString == null ? "" : oldString;
        newString = newString == null ? "" : newString;
        return ((newString.isEmpty() && !oldString.isEmpty()) ||
                (!newString.isEmpty() && !newString.equals(oldString)));
    }

    /**
     * Validates the Emergency Contact Details section of the form.
     */
    private void validateEmergencyContactDetails() throws InvalidFieldsException {
        boolean valid;
        // validate emergency contact info
        String emergencyEmail = ecEmail.getText();
        valid = AttributeValidation.validateEmail(emergencyEmail);
        if (!valid) {
            errorLabel.setVisible(true);
        }

        String emergencyPhone = ecPhone.getText();
        valid &= AttributeValidation.validatePhoneNumber(emergencyPhone.replaceAll(" ", ""));
        if (!valid) {
            errorLabel.setVisible(true);
        }

        String emergencyCell = ecCell.getText();
        valid &= AttributeValidation.validateCellNumber(emergencyCell.replaceAll(" ", ""));
        if (!valid) {
            errorLabel.setVisible(true);
        }

        String eName = ecName.getText();
        valid &= AttributeValidation.checkString(eName);

        String eStreetNumber = ecStreetNumber.getText();
        valid &= AttributeValidation.checkString(eStreetNumber);

        String eRegion;
        if (ecRegionInput.isVisible()) {
            eRegion = ecRegionInput.getText();

        } else {
            eRegion = ecRegionSelector.getSelectionModel().getSelectedItem();
        }
        valid &= AttributeValidation.checkString(eRegion);

        String eRelationship = ecRelationship.getText();
        valid &= AttributeValidation.checkString(eRelationship);

        // the name and cell number are required if any other attributes are filled out
        if ((eName.isEmpty() != emergencyCell.isEmpty()) && !valid) {
            errorLabel.setText("Name and cell phone number are required for an emergency contact.");
            errorLabel.setVisible(true);
            throw new InvalidFieldsException();
        }
    }

    /**
     * Updates the undo stacks of the form.
     */
    private void updateUndos() {
        boolean changed;
        String photoPath;
        if (inFile != null) {
            photoPath = inFile.getPath();
        } else {
            photoPath = currentUser.getProfilePhotoFilePath();
        }
        changed = updatePersonalDetails(nhiInput.getText(), fNameInput.getText(), dobInput.getValue(), photoPath);
        changed |= updateHealthDetails(heightInput.getText(), weightInput.getText());
        changed |= updateContactDetails();
        changed |= updateEmergencyContact();
        if (changed) {
            currentUser.getRedoStack().clear();
        }
        undoUpdateButton.setDisable(currentUser.getUndoStack().size() <= undoMarker);
        redoUpdateButton.setDisable(currentUser.getRedoStack().isEmpty());
    }


    /**
     * Updates all personal details that have changed.
     *
     * @param nhi   The national health index to be checked for changes and possibly updated.
     * @param fName The first name to be checked for changes and possibly updated.
     * @param dob   The date of birth to be checked for changes and possibly updated.
     */
    private boolean updatePersonalDetails(String nhi, String fName, LocalDate dob, String photoPath) {
        boolean changed = false;

        if (!currentUser.getNhi().equals(nhi)) {
            currentUser.setNhi(nhi);
            changed = true;
        }


        if (checkChangedProperty(fNameInput.getText(), currentUser.getFirstName())) {
            currentUser.setFirstName(fNameInput.getText());
            changed = true;
        }

        String prefName = preferredFNameTextField.getText();
        if (!currentUser.getPreferredFirstName().equals(prefName)) {
            currentUser.setPreferredFirstName(preferredFNameTextField.getText());
            changed = true;

        }

        if (checkChangedProperty(mNameInput.getText(), currentUser.getMiddleName())) {
            currentUser.setMiddleName(mNameInput.getText());
            changed = true;
        }

        if (checkChangedProperty(lNameInput.getText(), currentUser.getLastName())) {
            currentUser.setLastName(lNameInput.getText());
            changed = true;
        }

        if (!currentUser.getDateOfBirth().isEqual(dob)) {
            currentUser.setDateOfBirth(dob);
            changed = true;
        }


        if (checkChangedProperty(currentUser.getProfilePhotoFilePath(), photoPath)) {
            currentUser.setProfilePhotoFilePath(photoPath);
            changed = true;
        }

        return changed;
    }

    /**
     * Updates all health details that have changed.
     *
     * @param height The height to be checked for changes and possibly updated.
     * @param weight The weight to be checked for changes and possibly updated.
     */
    private boolean updateHealthDetails(String height, String weight) {
        boolean changed = false;

        if (height.isEmpty() && (currentUser.getHeightText() != null && !currentUser.getHeightText()
                .isEmpty())) {
            currentUser.setHeightText(null);
            changed = true;
        } else if (!height.isEmpty() && !height.equals(currentUser.getHeightText())) {
            currentUser.setHeightText(height);
            currentUser.setHeight(Double.parseDouble(height));
            changed = true;
        }


        if (weight.isEmpty() && (currentUser.getWeightText() != null && !currentUser.getWeightText()
                .isEmpty())) {
            currentUser.setWeightText(null);
            changed = true;
        } else if (!weight.isEmpty() && !weight.equals(currentUser.getWeightText())) {
            currentUser.setWeightText(weight);
            currentUser.setWeight(Double.parseDouble(weight));
            changed = true;
        }

        String birthGender = currentUser.getBirthGender();
        String bGender =
                AttributeValidation.validateGender(birthGenderComboBox.getValue()) ? birthGenderComboBox.getValue()
                        : "";

        if (birthGender != null && !birthGender.equals(bGender)) {
            currentUser.setBirthGender(bGender);
            changed = true;
        } else if (birthGender == null && bGender != null) {
            currentUser.setBirthGender(bGender);
            changed = true;
        }

        String genderIdentity = currentUser.getGenderIdentity();
        String genderID =
                AttributeValidation.validateGender(genderIdComboBox.getValue()) ? genderIdComboBox.getValue() : "";
        if (genderIdentity != null && !genderIdentity.equals(genderID)) {
            if (genderID == null) {
                currentUser.setGenderIdentity(birthGender);
                changed = true;
            } else {
                currentUser.setGenderIdentity(genderID);
                changed = true;
            }
        } else if (genderIdentity == null && genderID != null) {
            currentUser.setGenderIdentity(genderID);
            changed = true;
        }

        String bloodType = currentUser.getBloodType();
        String blood =
                AttributeValidation.validateBlood(bloodComboBox.getValue()) ? bloodComboBox.getValue()
                        : "";
        if (bloodType != null && !bloodType.equals("U") && !bloodType.equals(blood)) {
            currentUser.setBloodType(blood);
            changed = true;
        } else if (bloodType == null && blood != null) {
            currentUser.setBloodType(blood);
            changed = true;
        }

        String alcohol = alcoholComboBox.getValue();
        if (!currentUser.getAlcoholConsumption().equals(alcohol)) {
            currentUser.setAlcoholConsumption(alcohol);
            changed = true;
        }

        boolean smoker = smokerCheckBox.isSelected();
        if (currentUser.isSmoker() != smoker) {
            currentUser.setSmoker(smoker);
            changed = true;
        }

        return changed;
    }


    /**
     * Updates all contact details that have changed.
     */
    private Boolean updateContactDetails() {
        Boolean changed = false;
        if (checkChangedProperty(phone.getText(), currentUser.getHomePhone())) {
            currentUser.setHomePhone(phone.getText());
            changed = true;
        }

        if (checkChangedProperty(cell.getText(), currentUser.getCellPhone())) {
            currentUser.setCellPhone(cell.getText());
            changed = true;

        }

        if (checkChangedProperty(email.getText(), currentUser.getEmail())) {
            currentUser.setEmail(email.getText());
            changed = true;

        }

        if (checkChangedProperty(streetNumber.getText(), currentUser.getStreetNumber())) {
            currentUser.setStreetNumber(streetNumber.getText());
            changed = true;

        }

        if (checkChangedProperty(street.getText(), currentUser.getStreetName())) {
            currentUser.setStreetName(street.getText());
            changed = true;

        }
        if (checkChangedProperty(neighborhood.getText(), currentUser.getNeighborhood())) {
            currentUser.setNeighborhood(neighborhood.getText());
            changed = true;

        }

        if (checkChangedProperty(city.getText(), currentUser.getCity())) {
            currentUser.setCity(city.getText());
            changed = true;

        }

        if (checkChangedProperty(countrySelector.getSelectionModel().getSelectedItem(), currentUser.getCountry())) {
            currentUser.setCountry(countrySelector.getSelectionModel().getSelectedItem());
            changed = true;

        }

        if (checkChangedProperty(zipCode.getText(), currentUser.getZipCode())) {
            currentUser.setZipCode(zipCode.getText());
            changed = true;

        }

        String region;
        if (regionInput.isVisible()) {
            region = regionInput.getText();
        } else {
            region = regionSelector.getSelectionModel().getSelectedItem();
        }

        if (checkChangedProperty(region, currentUser.getRegion())) {
            currentUser.setRegion(region);
            changed = true;

        }
        return changed;
    }


    /**
     * Updates all emergency contact details that have changed.
     */
    private boolean updateEmergencyContact() {
        boolean changed = false;
        EmergencyContact contact = currentUser.getContact();

        if (checkChangedProperty(ecName.getText(), contact.getName())) {
            changed = true;
            currentUser.setECName(ecName.getText());
        }

        if (checkChangedProperty(ecPhone.getText(), contact.getHomePhoneNumber())) {
            currentUser.setECHomePhone(ecPhone.getText());
            changed = true;
        }

        if (checkChangedProperty(ecCell.getText(), contact.getCellPhoneNumber())) {
            currentUser.setECCellPhone(ecCell.getText());
        }

        if (checkChangedProperty(ecStreetNumber.getText(), contact.getStreetNumber())) {
            currentUser.setECStreetNumber(ecStreetNumber.getText());
            changed = true;
        }

        if (checkChangedProperty(ecStreet.getText(), contact.getStreetName())) {
            currentUser.setECStreeName(ecStreet.getText());
            changed = true;

        }
        if (checkChangedProperty(ecNeighborhood.getText(), contact.getNeighborhood())) {
            currentUser.setECNeighborhood(ecNeighborhood.getText());
            changed = true;
        }

        if (checkChangedProperty(ecCity.getText(), contact.getCity())) {
            currentUser.setECCity(ecCity.getText());
            changed = true;
        }

        if (checkChangedProperty(ecCountrySelector.getSelectionModel().getSelectedItem(), contact.getCountry())) {
            currentUser.setECCountry(ecCountrySelector.getSelectionModel().getSelectedItem());
            changed = true;
        }

        if (checkChangedProperty(ecZipCode.getText(), contact.getZipCode())) {
            currentUser.setECZipCode(ecZipCode.getText());
            changed = true;
        }

        String ecRegion;
        if (ecRegionInput.isVisible()) {
            ecRegion = ecRegionInput.getText();
        } else {
            ecRegion = ecRegionSelector.getSelectionModel().getSelectedItem();
        }

        if (checkChangedProperty(ecRegion, contact.getRegion())) {
            currentUser.setECRegion(ecRegion);
            changed = true;
        }

        if (checkChangedProperty(ecEmail.getText(), currentUser.getContact().getEmail())) {
            currentUser.setECEmail(ecEmail.getText());
            changed = true;
        }

        if (checkChangedProperty(ecRelationship.getText(), contact.getRelationship())) {
            currentUser.setECRelationship(ecRelationship.getText());
            changed = true;
        }

        return changed;
    }

    /**
     * Undoes a form change
     */
    @FXML
    void undo() {
        currentUser.undo();
        undoUpdateButton.setDisable(currentUser.getUndoStack().size() <= undoMarker);
        setUserDetails(currentUser);

        if (undoUpdateButton.isDisabled()) {
            stage.setTitle(stage.getTitle().substring(0, stage.getTitle().length() - 1));
        }
        Log.info("Undo executed for User NHI: " + currentUser.getNhi());
    }

    /**
     * Redoes a form change
     */
    @FXML
    void redo() {
        currentUser.redo();
        redoUpdateButton.setDisable(currentUser.getRedoStack().isEmpty());
        setUserDetails(currentUser);
        Log.info("Redo executed for User NHI: " + currentUser.getNhi());
    }

    /**
     * Prompts the user with a warning alert if there are unsaved changes, otherwise cancels
     * immediately.
     */
    @FXML
    void goBack() {
        if (!undoUpdateButton.isDisabled()) { // has changes
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "You have unsaved changes, are you sure you want to cancel?",
                    ButtonType.YES, ButtonType.NO);

            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
            yesButton.setId("yesButton");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.YES) {
                AppController appController = AppController.getInstance();
                UserController userController = appController.getUserController();
                try {
                    currentUser.getRedoStack().clear();
                    userController.showUser(oldUser);
                    Log.info("User update Cancelled for User NHI: " + currentUser.getNhi());
                } catch (NullPointerException ex) {
                    //TODO causes npe if donor is new in this session
                    //the text fields etc. are all null
                    Log.severe("Error cancelling user update for User NHI: " + currentUser.getNhi(), ex);
                }
                stage.close();
            }
        } else { // has no changes
            AppController appController = AppController.getInstance();
            UserController userController = appController.getUserController();
            try {
                currentUser.getRedoStack().clear();
                userController.showUser(oldUser);
                Log.info("User update Cancelled for User NHI: " + currentUser.getNhi());
            } catch (NullPointerException ex) {
                //TODO causes npe if donor is new in this session
                //the text fields etc. are all null
                Log.severe("Error cancelling user update for User NHI: " + currentUser.getNhi(), ex);
            }
            stage.close();
        }
    }

    /**
     * Makes all the error messages no longer visible.
     */
    private void hideErrorMessages() {
        existingNHI.setVisible(false);
        invalidNHI.setVisible(false);
        invalidDOB.setVisible(false);
        errorLabel.setVisible(false);
        invalidFirstName.setVisible(false);
    }
}
