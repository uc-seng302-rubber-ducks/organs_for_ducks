package seng302.controller.gui.window;


import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import seng302.controller.AppController;
import seng302.controller.gui.window.UserController;
import seng302.exception.InvalidFieldsException;
import seng302.model.EmergencyContact;
import seng302.model.Memento;
import seng302.model.User;
import seng302.service.AttributeValidation;
import seng302.service.Log;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Class for updating the user
 */
public class UpdateUserController {

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
    private Label invalidDOD;

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
    private TextField phoneInput;

    @FXML
    private TextField cellInput;

    @FXML
    private TextField addressInput;

    @FXML
    private TextField regionInput;

    @FXML
    private TextField emailInput;

    @FXML
    private TextField ecNameInput;

    @FXML
    private TextField ecPhoneInput;

    @FXML
    private TextField ecCellInput;

    @FXML
    private TextField ecAddressInput;

    @FXML
    private TextField ecRegionInput;

    @FXML
    private TextField ecEmailInput;

    @FXML
    private TextField ecRelationshipInput;

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
    private DatePicker dodInput;

    @FXML
    private Button undoUpdateButton;

    @FXML
    private Button redoUpdateButton;

    private Stage stage;
    private AppController appController;
    private User currentUser;
    private User oldUser;
    private int undoMarker; //int used to hold the top of the stack before opening this window
    private boolean listen = true;


    /**
     * @param user       The current user.
     * @param controller An instance of the AppController class.
     * @param stage      The applications stage.
     */
    public void init(User user, AppController controller, Stage stage) {
        this.stage = stage;
        currentUser = user;
        System.out.println(user.toString());
        oldUser = currentUser.clone();
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
                heightInput, weightInput, phoneInput, cellInput, addressInput, regionInput, emailInput,
                ecNameInput, ecPhoneInput, ecCellInput, ecAddressInput, ecRegionInput, ecEmailInput,
                ecRelationshipInput};

        // creates a listener for each text field
        for (TextField tf : allTextFields) {
            textFieldListener(tf);
        }

        comboBoxListener(birthGenderComboBox);
        comboBoxListener(genderIdComboBox);
        comboBoxListener(bloodComboBox);
        comboBoxListener(alcoholComboBox);

        datePickerListener(dobInput);
        datePickerListener(dodInput);

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
        dp.valueProperty().addListener((observable, oldValue, newValue) -> update());
    }

    /**
     * Listens for changes on the given checkbox and calls for updates.
     *
     * @param checkBox The given CheckBox.
     */
    private void addCheckBoxListener(CheckBox checkBox) {
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> update());
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

        if (user.getPreferredFirstName() != null && !user.getPreferredFirstName()
                .equals(user.getFirstName())) {
            preferredFNameTextField.setText(user.getPreferredFirstName());
        } else {
            preferredFNameTextField.setText(user.getFirstName());
        }

        dobInput.setValue(user.getDateOfBirth());
        dodInput.setValue(user.getDateOfDeath());

        //contact
        if (user.getCurrentAddress() != null) {
            addressInput.setText(user.getCurrentAddress());
        } else {
            addressInput.setText("");
        }
        if (user.getRegion() != null) {
            regionInput.setText(user.getRegion());
        } else {
            regionInput.setText("");
        }
        if (user.getCellPhone() != null) {
            cellInput.setText(user.getCellPhone());
        } else {
            cellInput.setText("");
        }
        if (user.getHomePhone() != null) {
            phoneInput.setText(user.getHomePhone());
        } else {
            phoneInput.setText("");
        }
        if (user.getEmail() != null) {
            emailInput.setText(user.getEmail());
        } else {
            emailInput.setText("");
        }
        //ec
        if (user.getContact() != null) {
            if (user.getContact().getName() != null) {
                ecNameInput.setText(user.getContact().getName());
            } else {
                ecNameInput.setText("");
            }
            if (user.getContact().getRelationship() != null) {
                ecRelationshipInput.setText(user.getContact().getRelationship());
            } else {
                ecRelationshipInput.setText("");
            }
            if (user.getContact().getRegion() != null) {
                ecRegionInput.setText(user.getContact().getRegion());
            } else {
                ecRegionInput.setText("");
            }
            if (user.getContact().getHomePhoneNumber() != null) {
                ecPhoneInput.setText(user.getContact().getHomePhoneNumber());
            } else {
                ecPhoneInput.setText("");
            }
            if (user.getContact().getEmail() != null) {
                ecEmailInput.setText(user.getContact().getEmail());
            } else {
                ecEmailInput.setText("");
            }

            if (user.getContact().getAddress() != null) {
                ecAddressInput.setText(user.getContact().getAddress());
            } else {
                ecAddressInput.setText("");
            }
            if (user.getContact().getCellPhoneNumber() != null) {
                ecCellInput.setText(user.getContact().getCellPhoneNumber());
            } else {
                ecCellInput.setText("");
            }
        }
        //h
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

        if (user.getWeightText() != null) {
            weightInput.setText(user.getWeightText());
        } else {
            weightInput.setText(Double.toString(user.getWeight()));
        }
        if (user.getHeightText() != null) {
            heightInput.setText(user.getHeightText());
        } else {
            heightInput.setText(Double.toString(user.getHeight()));
        }
        listen = true;

        undoUpdateButton.setDisable(currentUser.getUndoStack().size() <= undoMarker);
        redoUpdateButton.setDisable(currentUser.getRedoStack().isEmpty());

    }

    /**
     *
     */
    @FXML
    public void confirmUpdate() {

        hideErrorMessages();
        errorLabel.setText("Please make sure your details are correct.");
        boolean valid = validateFields();

        //TODO change to be different

        if (valid) {
            sumAllChanged();
            AppController appController = AppController.getInstance();
            UserController userController = appController.getUserController();
            try {
                currentUser.getRedoStack().clear();
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
     * Turns all form changes into one memento on the stack
     */
    private void sumAllChanged() {
        Memento<User> sumChanges = new Memento<>();
        removeFormChanges();
        if (!currentUser.getUndoStack().isEmpty()) {
            sumChanges.setOldObject(currentUser.getUndoStack().peek().getOldObject().clone());
            currentUser.getUndoStack().pop();
            sumChanges.setNewObject(currentUser.clone());
            currentUser.getUndoStack().push(sumChanges);
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
            User user = appController.findUser(nhi);
            if (user != null && !user.getNhi()
                    .equals(nhi)) { // if a user was found, but it is not the current user
                existingNHI.setVisible(true);
                valid = false;
            }
        }

        LocalDate dob = dobInput.getValue();
        LocalDate dod = dodInput.getValue();

        String fName = fNameInput.getText();
        valid &= AttributeValidation.checkRequiredString(fName);
        if (!valid) {
            invalidFirstName.setVisible(true);
        }

        valid &= AttributeValidation.validateDateOfBirth(dob);
        if (!valid) {
            invalidDOB.setVisible(true);
        }

        if (dob != null) {
            valid &= AttributeValidation.validateDateOfDeath(dob,
                    dod); // checks if the dod is before tomorrow's date and that the dob is before the dod
            if (!valid) {
                invalidDOD.setVisible(true);
            }
        }

        double height = AttributeValidation.validateDouble(heightInput.getText());
        double weight = AttributeValidation.validateDouble(weightInput.getText());
        if (height == -1 || weight == -1) {
            errorLabel.setVisible(true);
            valid = false;
        } else {
            currentUser.setHeight(height);
            currentUser.setWeight(weight);
        }

        // validate contact info
        String email = emailInput.getText();
        valid &= AttributeValidation.validateEmail(email);
        if (!valid) {
            errorLabel.setVisible(true);
        }

        String homePhone = phoneInput.getText();
        valid &= AttributeValidation.validatePhoneNumber(homePhone);
        if (!valid) {
            errorLabel.setVisible(true);
        }

        String cellPhone = cellInput.getText();
        valid &= AttributeValidation.validateCellNumber(cellPhone);
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
        return valid;
    }

    /**
     * Validates the Emergency Contact Details section of the form.
     */
    private void validateEmergencyContactDetails() throws InvalidFieldsException {
        boolean valid;
        // validate emergency contact info
        String emergencyEmail = ecEmailInput.getText();
        valid = AttributeValidation.validateEmail(emergencyEmail);
        if (!valid) {
            errorLabel.setVisible(true);
        }

        String emergencyPhone = ecPhoneInput.getText();
        valid &= AttributeValidation.validatePhoneNumber(emergencyPhone);
        if (!valid) {
            errorLabel.setVisible(true);
        }

        String emergencyCell = ecCellInput.getText();
        valid &= AttributeValidation.validateCellNumber(emergencyCell);
        if (!valid) {
            errorLabel.setVisible(true);
        }

        String eName = ecNameInput.getText();
        valid &= AttributeValidation.checkString(eName);

        String eAddress = ecAddressInput.getText();
        valid &= AttributeValidation.checkString(eAddress);

        String eRegion = ecRegionInput.getText();
        valid &= AttributeValidation.checkString(eRegion);

        String eRelationship = ecRelationshipInput.getText();
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
        changed = updatePersonalDetails(nhiInput.getText(), fNameInput.getText(), dobInput.getValue(),
                dodInput.getValue());

        changed |= updateHealthDetails(heightInput.getText(), weightInput.getText());
        changed |= updateContactDetails(phoneInput.getText(), cellInput.getText(),
                emailInput.getText());
        changed |= updateEmergencyContact(ecNameInput.getText(), ecPhoneInput.getText(),
                ecCellInput.getText(), ecAddressInput.getText(), ecRegionInput.getText(),
                ecEmailInput.getText(), ecRelationshipInput.getText());
        if (changed) {
            appController.update(currentUser);
            setUserDetails(currentUser);
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
     * @param dod   The date of death to be checked for changes and possibly updated.
     */
    private boolean updatePersonalDetails(String nhi, String fName, LocalDate dob, LocalDate dod) {
        boolean changed = false;
        if (!currentUser.getNhi().equals(nhi)) {
            currentUser.setNhi(nhi);
            changed = true;
        }

        if (!currentUser.getFirstName().equals(fName)) {
            currentUser.setFirstName(fName);
            if (currentUser.getPreferredFirstName().equals(currentUser.getFirstName())
                    || preferredFNameTextField.getText().isEmpty()) {
                listen = false;
                preferredFNameTextField.setText(currentUser.getFirstName());
                listen = true;
            }
            changed = true;
        }

        String prefName = preferredFNameTextField.getText();
        if (!currentUser.getPreferredFirstName().equals(prefName)) {
            if (prefName.isEmpty() && !preferredFNameTextField.isFocused()) {
                currentUser.setPreferredFirstName(fName);
                changed = true;
            } else {
                currentUser.setPreferredFirstName(preferredFNameTextField.getText());
                changed = true;
            }
        }

        String mName = mNameInput.getText();
        if (!mName.isEmpty() && !mName.equals(currentUser.getMiddleName())) {
            currentUser.setMiddleName(mName);
            changed = true;
        } else if (mName.isEmpty() && (currentUser.getMiddleName() != null && !currentUser
                .getMiddleName().isEmpty())) {
            currentUser.setMiddleName(null);
            changed = true;
        }

        String lName = lNameInput.getText();
        if (!lName.isEmpty() && !lName.equals(currentUser.getLastName())) {
            currentUser.setLastName(lName);
            changed = true;
        } else if (lName.isEmpty() && (currentUser.getLastName() != null && !currentUser
                .getLastName().isEmpty())) {
            currentUser.setLastName(null);
            changed = true;
        }

        if (!currentUser.getDateOfBirth().isEqual(dob)) {
            currentUser.setDateOfBirth(dob);
            changed = true;
        }

        LocalDate deathDate = currentUser.getDateOfDeath();
        if (deathDate != null && dod != null) {
            if (!deathDate.isEqual(dod)) {
                currentUser.setDateOfDeath(dod);
                changed = true;
            }
        } else if ((deathDate == null && dod != null) || deathDate != null) {
            currentUser.setDateOfDeath(dod);
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
            changed = true;
        }

        if (weight.isEmpty() && (currentUser.getWeightText() != null && !currentUser.getWeightText()
                .isEmpty())) {
            currentUser.setHeightText(null);
            changed = true;
        } else if (!weight.isEmpty() && !weight.equals(currentUser.getWeightText())) {
            currentUser.setWeightText(weight);
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
        if (bloodType != null && !bloodType.equals(blood)) {
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
     *
     * @param homePhone The home phone number to be checked for changes and possibly updated.
     * @param cellPhone The cell phone number to be checked for changes and possibly updated.
     * @param email     The email address to be checked for changes and possibly updated.
     */
    private boolean updateContactDetails(String homePhone, String cellPhone, String email) {

        boolean changed = false;

        if (homePhone.isEmpty() && (currentUser.getHomePhone() != null && !currentUser.getHomePhone()
                .isEmpty())) {
            currentUser.setHomePhone(null);
            changed = true;
        } else if (!homePhone.isEmpty() && !homePhone.equals(currentUser.getHomePhone())) {
            currentUser.setHomePhone(homePhone);
            changed = true;
        }

        if (!cellPhone.isEmpty() && !cellPhone.equals(currentUser.getCellPhone())) {
            currentUser.setCellPhone(cellPhone);
            changed = true;
        } else if (cellPhone.isEmpty() && (currentUser.getCellPhone() != null && !currentUser
                .getCellPhone().isEmpty())) {
            currentUser.setCellPhone(null);
            changed = true;
        }

        if (!email.isEmpty() && !email.equals(currentUser.getEmail())) {
            currentUser.setEmail(email);
            changed = true;
        } else if (email.isEmpty() && (currentUser.getEmail() != null && !currentUser.getEmail()
                .isEmpty())) {
            currentUser.setEmail(null);
            changed = true;
        }

        String address = addressInput.getText();
        if (!address.isEmpty() && !address.equals(currentUser.getCurrentAddress())) {
            currentUser.setCurrentAddress(address);
            changed = true;
        } else if (address.isEmpty() && (currentUser.getCurrentAddress() != null && !currentUser
                .getCurrentAddress().isEmpty())) {
            currentUser.setCurrentAddress(null);
            changed = true;
        }

        String region = regionInput.getText();
        if (!region.isEmpty() && !region.equals(currentUser.getRegion())) {
            currentUser.setRegion(region);
            changed = true;
        } else if (region.isEmpty() && (currentUser.getRegion() != null && !currentUser.getRegion()
                .isEmpty())) {
            currentUser.setRegion(null);
            changed = true;
        }

        return changed;
    }

    /**
     * Updates all emergency contact details that have changed.
     *
     * @param eName          The emergency contact name to be checked for changes and possibly updated.
     * @param emergencyPhone The emergency contact phone number to be checked for changes and possibly updated.
     * @param emergencyCell  The emergency contact cell phone number to be checked for changes and possibly updated.
     * @param eAddress       The emergency contact address to be checked for changes and possibly updated.
     * @param eRegion        The emergency contact region to be checked for changes and possibly updated.
     * @param emergencyEmail The emergency contact email address to be checked for changes and possibly updated.
     * @param eRelationship  The relationship between the emergency contact and user to be checked for changes and possibly updated.
     */
    private boolean updateEmergencyContact(String eName, String emergencyPhone, String
            emergencyCell, String eAddress,
                                           String eRegion, String emergencyEmail, String eRelationship) {
        boolean changed = false;
        EmergencyContact contact = currentUser.getContact();

        String name = contact.getName();
        if (name != null) {
            if (!name.isEmpty() && !name.equals(eName)) {
                contact.setName(eName);
                changed = true;
            } else if (name.isEmpty() && (eName != null && !eName.isEmpty())) {
                contact.setName(eName);
                changed = true;
            }
        } else {
            if (!eName.isEmpty()) {
                contact.setName(eName);
                changed = true;
            }
        }

        String ePhone = contact.getHomePhoneNumber();
        if (ePhone != null) {
            if (!ePhone.isEmpty() && !ePhone.equals(emergencyPhone)) {
                contact.setHomePhoneNumber(emergencyPhone);
                changed = true;
            } else if (ePhone.isEmpty() && (emergencyPhone != null && !emergencyPhone.isEmpty())) {
                contact.setHomePhoneNumber(emergencyPhone);
                changed = true;
            }
        } else if (!emergencyPhone.isEmpty()) {
            contact.setHomePhoneNumber(emergencyPhone);
            changed = true;
        }

        String eCell = contact.getCellPhoneNumber();
        if (eCell != null) {
            if (!eCell.isEmpty() && !eCell.equals(emergencyCell)) {
                contact.setCellPhoneNumber(emergencyCell);
                changed = true;
            } else if (eCell.isEmpty() && (emergencyCell != null && !emergencyCell.isEmpty())) {
                contact.setCellPhoneNumber(emergencyCell);
                changed = true;
            }
        } else if (!emergencyCell.isEmpty()) {
            contact.setCellPhoneNumber(emergencyCell);
            changed = true;
        }

        String address = contact.getAddress();
        if (address != null) {
            if (!address.isEmpty() && !address.equals(eAddress)) {
                contact.setAddress(eAddress);
                changed = true;
            } else if (address.isEmpty() && (eAddress != null && !eAddress.isEmpty())) {
                contact.setAddress(eAddress);
                changed = true;
            }
        } else if (!eAddress.isEmpty()) {
            contact.setAddress(eAddress);
            changed = true;
        }

        String region = contact.getRegion();
        if (region != null) {
            if (!region.isEmpty() && !region.equals(eRegion)) {
                contact.setRegion(eRegion);
                changed = true;
            } else if (region.isEmpty() && (eRegion != null && eRegion.isEmpty())) {
                contact.setRegion(eRegion);
                changed = true;
            }
        } else if (!eRegion.isEmpty()) {
            contact.setRegion(eRegion);
            changed = true;
        }

        String eEmail = contact.getEmail();
        if (eEmail != null) {
            if (!eEmail.isEmpty() && !eEmail.equals(emergencyEmail)) {
                contact.setEmail(emergencyEmail);
                changed = true;
            } else if (eEmail.isEmpty() && (emergencyEmail != null && !emergencyEmail.isEmpty())) {
                contact.setEmail(emergencyEmail);
                changed = true;
            }
        } else if (!emergencyEmail.isEmpty()) {
            contact.setEmail(emergencyEmail);
            changed = true;
        }

        String relation = contact.getRelationship();
        if (relation != null) {
            if (!relation.isEmpty() && !relation.equals(eRelationship)) {
                contact.setRelationship(eRelationship);
                changed = true;
            } else if (relation.isEmpty() && (eRelationship != null && !eRelationship.isEmpty())) {
                contact.setRelationship(eRelationship);
                changed = true;
            }
        } else if (!eRelationship.isEmpty()) {
            contact.setRelationship(eRelationship);
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
        invalidDOD.setVisible(false);
        errorLabel.setVisible(false);
        invalidFirstName.setVisible(false);
    }
}
