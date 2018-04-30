package seng302.Controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import seng302.Model.EmergencyContact;
import seng302.Model.User;
import seng302.Service.AttributeValidation;


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
  private ComboBox birthGenderComboBox;

  @FXML
  private ComboBox genderIdComboBox;

  @FXML
  private ComboBox bloodComboBox;

  @FXML
  private CheckBox smokerCheckBox;

  @FXML
  private ComboBox alcoholComboBox;

  @FXML
  private DatePicker dobInput;

  @FXML
  private DatePicker dodInput;

  @FXML
  private Button cancelButton;

  @FXML
  private Button confirmButton;

    private Stage stage;
    private AppController appController;
    private User currentUser;
    private User oldUser;


    /**
     *
     * @param user The current user.
     * @param controller An instance of the AppController class.
     * @param stage The applications stage.
     */
    public void init(User user, AppController controller, Stage stage){
        this.stage = stage;
        currentUser = user;
        this.appController = controller;
        //UndoRedoStacks.storeUndoCopy(currentUser);
        currentUser = user;
        oldUser = new User();
        setUserDetails(currentUser);
        if (user.getLastName() != null) {
          stage.setTitle("Update User: " + user.getFirstName() +" " + user.getLastName());
        } else {
          stage.setTitle("Update User: " + user.getFirstName());
        }
        //UndoRedoStacks.cloneUser(currentUser,oldUser);

        Scene scene = stage.getScene();

        final TextField[] allTextFields = {nhiInput, fNameInput, preferredFNameTextField, mNameInput, lNameInput,
                heightInput, weightInput, phoneInput, cellInput, addressInput, regionInput, emailInput,
        ecNameInput, ecPhoneInput, ecCellInput, ecAddressInput, ecRegionInput, ecEmailInput, ecRelationshipInput};

        // creates a listener for each text field
        for (TextField tf: allTextFields) {
            textFieldListener(tf);
        }

        comboBoxListener(birthGenderComboBox);
        comboBoxListener(genderIdComboBox);
        comboBoxListener(bloodComboBox);
        comboBoxListener(alcoholComboBox);

        datePickerListener(dobInput);
        datePickerListener(dodInput);

        final KeyCombination shortcutZ = new KeyCodeCombination(
                KeyCode.Z, KeyCombination.SHORTCUT_DOWN);

        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if (shortcutZ.match(e)) {
                if (checkChanges()) { // checks if reverting a textfield change restores all fields to their original state
                    stage.setTitle("Update User: " + user.getFirstName());
                }
            }
        });

    }

    /**
     * Changes the title bar to add/remove an asterisk when a change was detected on the date picker.
     * @param dp The current date picker.
     */
    private void datePickerListener(DatePicker dp) {
        dp.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (checkChanges()) {
                stage.setTitle("Update User: " + currentUser.getFirstName());
            } else {
                stage.setTitle("Update User: " + currentUser.getFirstName() + " *");
            }
        });
    }

    /**
     * Changes the title bar to add/remove an asterisk when a change is detected on the ComboBox.
     * @param cb The current ComboBox.
     */
    private void comboBoxListener(ComboBox cb) {
        cb.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (checkChanges()) {
                stage.setTitle("Update User: " + currentUser.getFirstName());
            } else {
                stage.setTitle("Update User: " + currentUser.getFirstName() + " *");
            }
        });

    }

    /**
     * Changes the title bar to add/remove an asterisk when the smoker checkbox is selected/unselected.
     * @param event The user selecting/deselecting the check box.
     */
    @FXML
    private void smokerClicked(ActionEvent event) {
        if (currentUser.isSmoker() == smokerCheckBox.isSelected()) {
            if (checkChanges()) {
                stage.setTitle("Update User: " + currentUser.getFirstName());
            }
        } else {
            stage.setTitle("Update User: " + currentUser.getFirstName() + " *");
        }
    }

    /**
     * Changes the title bar to contain an asterisk if a change was detected on the textfields.
     * @param field The current textfield.
     */
    private void textFieldListener(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            stage.setTitle("Update User: " + currentUser.getFirstName() + " *");
        });
    }

    /**
     * Checks if all fields are in their original state (i.e. no change has been made / all changes have been undone).
     * @return true if all fields are in their original state, false if at least one field is different.
     */
    private boolean checkChanges() {
        boolean noChange = true;

        // user details
        if (!(currentUser.getNhi()).equals(nhiInput.getText())) {
            noChange = false;
        }

        if (!(currentUser.getFirstName()).equals(fNameInput.getText())) {
            noChange = false;
        }

        if (!currentUser.getPrefFirstName().equals(preferredFNameTextField.getText())) {
            noChange = false;
        }

        if (currentUser.getMiddleName() != null) {
            if (!(currentUser.getMiddleName()).equals(mNameInput.getText())) {
                noChange = false;
            }
        } else if (!mNameInput.getText().isEmpty()) {
            noChange = false;
        }

        if (currentUser.getLastName() != null) {
            if (!(currentUser.getLastName()).equals(lNameInput.getText())) {
                noChange = false;
            }
        } else if (!lNameInput.getText().isEmpty()) {
            noChange = false;
        }

        if (dobInput.getValue() != null) {
            if (!currentUser.getDateOfBirth().isEqual(dobInput.getValue())) {
                noChange = false;
            }
        } else {
            noChange = false;
        }

        LocalDate deathDate = currentUser.getDateOfDeath();
        LocalDate dod = dodInput.getValue();
        if (deathDate != null && dod != null) {
            if (!deathDate.isEqual(dod)) {
                noChange = false;
            }
        } else if ((deathDate == null && dod != null) || deathDate != null) {
            noChange = false;
        }

        // health details
        if (currentUser.getBirthGender() != null && birthGenderComboBox.getValue() != null) {
            if (!(currentUser.getBirthGender()).equals(birthGenderComboBox.getValue().toString())) {
                noChange = false;
            }
        } else if (birthGenderComboBox.getValue() != null && !birthGenderComboBox.getValue().toString().equals("")) {
            noChange = false;
        }

        if (currentUser.getGenderIdentity() != null && genderIdComboBox.getValue() != null) {
            if (!(currentUser.getGenderIdentity()).equals(genderIdComboBox.getValue().toString())) {
                noChange = false;
            }
        } else if (genderIdComboBox.getValue() != null && !genderIdComboBox.getValue().toString().equals("")) {
            noChange = false;
        }
        if (currentUser.getBloodType() != null && bloodComboBox.getValue() != null) {
            if (!(currentUser.getBloodType()).equals(bloodComboBox.getValue().toString())) {
                noChange = false;
            }
        } else if (bloodComboBox.getValue() != null) {
            noChange = false;
        }

        if (currentUser.getAlcoholConsumption() != null && alcoholComboBox.getValue() != null) {
            if (!(currentUser.getAlcoholConsumption()).equals(alcoholComboBox.getValue().toString())) {
                noChange = false;
            }
        } else if (alcoholComboBox.getValue() != null) {
            noChange = false;
        }
        if (currentUser.getWeight() > 0) {
            try {
                double weight = Double.parseDouble(weightInput.getText());
                if (currentUser.getWeight() != weight) {
                    noChange = false;
                }
            } catch (NumberFormatException e) {
                noChange = false;
            }
        } else if (!weightInput.getText().isEmpty()) {
            noChange = false;
        }

        if (currentUser.getHeight() > 0) {
            try {
                double height = Double.parseDouble(heightInput.getText());
                if (currentUser.getHeight() != height) {
                    noChange = false;
                }
            } catch (NumberFormatException e) {
                noChange = false;
            }
        } else if (!heightInput.getText().isEmpty()) {
            noChange = false;
        }
        if (!(currentUser.isSmoker() == smokerCheckBox.isSelected())) noChange = false;

        // contact details
        if (currentUser.getHomePhone() != null) {
            if (!(currentUser.getHomePhone()).equals(phoneInput.getText())) {
                noChange = false;
            }
        } else if (!phoneInput.getText().isEmpty()) {
            noChange = false;
        }

        if (currentUser.getCellPhone() != null) {
            if (!(currentUser.getCellPhone()).equals(cellInput.getText())) {
                noChange = false;
            }
        } else if (!cellInput.getText().isEmpty()) {
            noChange = false;
        }

        if (currentUser.getCurrentAddress() != null) {
            if (!(currentUser.getCurrentAddress()).equals(addressInput.getText())) {
                noChange = false;
            }
        } else if (!addressInput.getText().isEmpty()) {
            noChange = false;
        }

        if (currentUser.getRegion() != null) {
            if (!(currentUser.getRegion()).equals(regionInput.getText())) {
                noChange = false;
            }
        } else if (!regionInput.getText().isEmpty()) {
            noChange = false;
        }

        if (currentUser.getEmail() != null) {
            if (!(currentUser.getEmail()).equals(emailInput.getText())) {
                noChange = false;
            }
        } else if (!emailInput.getText().isEmpty()) {
            noChange = false;
        }

        // emergency contact details
        EmergencyContact contact = currentUser.getContact();

        if (contact.getName() != null) {
            if (!(contact.getName()).equals(ecNameInput.getText())) {
                noChange = false;
            }
        } else if (!ecNameInput.getText().isEmpty()) {
            noChange = false;
        }

        if (contact.getHomePhoneNumber() != null) {
            if (!(contact.getHomePhoneNumber()).equals(ecPhoneInput.getText())) {
                noChange = false;
            }
        } else if (!ecPhoneInput.getText().isEmpty()) {
            noChange = false;
        }

        if (contact.getCellPhoneNumber() != null) {
            if (!(contact.getCellPhoneNumber()).equals(ecCellInput.getText())) {
                noChange = false;
            }
        } else if (!ecCellInput.getText().isEmpty()) {
            noChange = false;
        }

        if (contact.getAddress() != null) {
            if (!(contact.getAddress()).equals(ecAddressInput.getText())) {
                noChange = false;
            }
        } else if (!ecAddressInput.getText().isEmpty()) {
            noChange = false;
        }

        if (contact.getRegion() != null) {
            if (!(contact.getRegion()).equals(ecRegionInput.getText())) {
                noChange = false;
            }
        } else if (!ecRegionInput.getText().isEmpty()) {
            noChange = false;
        }

        if (contact.getEmail() != null) {
            if (!(contact.getEmail()).equals(ecEmailInput.getText())) {
                noChange = false;
            }
        } else if (!ecEmailInput.getText().isEmpty()) {
            noChange = false;
        }

        if (contact.getRelationship() != null) {
            if (!(contact.getRelationship()).equals(ecRelationshipInput.getText())) {
                noChange = false;
            }
        } else if (!ecRelationshipInput.getText().isEmpty()) {
            noChange = false;
        }

        return noChange;
    }


    private void setUserDetails(User user) {
      //personal
      fNameInput.setText(user.getFirstName());
      nhiInput.setText(user.getNhi());
      if (user.getLastName() != null) {
        lNameInput.setText(user.getLastName());
      }
      if (user.getMiddleName() != null) {
        mNameInput.setText(user.getMiddleName());
      }
      if (user.getPrefFirstName() != null) {
        preferredFNameTextField.setText(user.getPrefFirstName());
      }

      dobInput.setValue(user.getDateOfBirth());
      if (user.getDateOfDeath() != null) {
          dodInput.setValue(user.getDateOfDeath());
      }

      //contact
      if (user.getCurrentAddress() != null){
        addressInput.setText(user.getCurrentAddress());
      }
      if (user.getRegion() != null){
        regionInput.setText(user.getRegion());
      }
      if (user.getCellPhone() != null){
        cellInput.setText(user.getCellPhone());
      }
      if (user.getHomePhone() != null){
        phoneInput.setText(user.getHomePhone());
      }
      if (user.getEmail() != null){
        emailInput.setText(user.getEmail());
      }
      //ec
      if (user.getContact().getName() != null) {
        ecNameInput.setText(user.getContact().getName());
      }
      if (user.getContact().getRelationship() != null) {
        ecRelationshipInput.setText(user.getContact().getRelationship());
      }
      if (user.getContact().getRegion() != null) {
        ecRegionInput.setText(user.getContact().getRegion());
      }
      if (user.getContact().getHomePhoneNumber() != null){
        ecPhoneInput.setText(user.getContact().getHomePhoneNumber());
      }
      if (user.getContact().getEmail() != null){
        ecEmailInput.setText(user.getContact().getEmail());
      }
      if (user.getContact().getAddress() != null){
        ecAddressInput.setText(user.getContact().getAddress());

      }
      if (user.getContact().getCellPhoneNumber() != null){
        ecCellInput.setText(user.getContact().getCellPhoneNumber());
      }
      //h
      alcoholComboBox.setValue(user.getAlcoholConsumption());
      if (user.isSmoker()){
        smokerCheckBox.setSelected(true);
      }
      if (user.getBloodType() != null) {
        bloodComboBox.setValue(user.getBloodType());
      }
      if (user.getBirthGender() != null){
        birthGenderComboBox.setValue(user.getBirthGender());
      }
      if (user.getGenderIdentity() != null){
        genderIdComboBox.setValue(user.getGenderIdentity());
      }
      if (user.getWeight() > 0){
        weightInput.setText(Double.toString(user.getWeight()));
      }
      if (user.getHeight() > 0){
        heightInput.setText(Double.toString(user.getHeight()));
      }

    }

    /**
     * Activates when the user clicks the save changes button.
     * Calls a method to check if all field are valid and then updates the changes.
     * Also resets error labels.
     *
     * @param actionEvent The user clicks on the save changes button.
     * @throws IOException input/output exception.
     */
    @FXML
    public void confirmUpdate(ActionEvent actionEvent) throws IOException {
        hideErrorMessages();
        errorLabel.setText("Please make sure your details are correct.");
        validateFields();
    }

    /**
     * Checks if all fields that require validation are valid.
     * Sets error messages visible if fields are invalid.
     * Calls methods to update the changes if all fields are valid.
     */
    private void validateFields() {
        boolean valid = true;
        String nhi = AttributeValidation.validateNHI(nhiInput.getText());
        LocalDate dob = dobInput.getValue();
        LocalDate dod = dodInput.getValue();

        if (nhi == null) {
            invalidNHI.setVisible(true);
            valid = false;
        } else {
            User user = appController.findUser(nhi);
            if (user != null && !user.getNhi().equals(nhi)) { // if a user was found, but it is not the current user
                existingNHI.setVisible(true);
                valid = false;
            }
        }

        String fName = fNameInput.getText();
        if (fName.isEmpty()) {
            invalidFirstName.setVisible(true);
            valid = false;
        }

        if (dob == null) {
            invalidDOB.setVisible(true);
            valid = false;
        } else if (!dob.isBefore(LocalDate.now().plusDays(1))) { // checks that the date of birth is before tomorrow's date
            invalidDOB.setVisible(true);
            valid = false;
        }

        if (dod != null) {
            boolean datesValid = AttributeValidation.validateDates(dob, dod); // checks if the dod is before tomorrow's date and that the dob is before the dod
            if (!datesValid) {
                invalidDOD.setVisible(true);
                valid = false;
            }
        }

        double height = AttributeValidation.validateDouble(heightInput.getText());
        double weight = AttributeValidation.validateDouble(weightInput.getText());
        if (height == -1 || weight == -1) {
            errorLabel.setVisible(true);
            valid = false;
        }

        // validate contact info
        String email = null;
        if (!emailInput.getText().isEmpty()) {
            email = AttributeValidation.validateEmail(emailInput.getText());

            if (email == null) {
                errorLabel.setVisible(true);
                valid = false;
            }
        }

        String homePhone = null;
        if (!phoneInput.getText().isEmpty()) {
            homePhone = AttributeValidation.validatePhoneNumber(phoneInput.getText());

            if (homePhone == null) {
                errorLabel.setVisible(true);
                valid = false;
            }
        }

        String cellPhone = null;
        if (!cellInput.getText().isEmpty()) {
            cellPhone = AttributeValidation.validateCellNumber(cellInput.getText());

            if (cellPhone == null) {
                errorLabel.setVisible(true);
                valid = false;
            }
        }

        // validate emergency contact info
        String emergencyEmail = AttributeValidation.checkString(ecEmailInput.getText());
        if (emergencyEmail != null) {
            emergencyEmail = AttributeValidation.validateEmail(ecEmailInput.getText());

            if (emergencyEmail == null) {
                errorLabel.setVisible(true);
                valid = false;
            }
        }

        String emergencyPhone = AttributeValidation.checkString(ecPhoneInput.getText());
        if (emergencyPhone != null) {
            emergencyPhone = AttributeValidation.validatePhoneNumber(ecPhoneInput.getText());

            if (emergencyPhone == null) {
                errorLabel.setVisible(true);
                valid = false;
            }
        }

        String emergencyCell = AttributeValidation.checkString(ecCellInput.getText());
        if (emergencyCell != null) {
            emergencyCell = AttributeValidation.validateCellNumber(ecCellInput.getText());

            if (emergencyCell == null) {
                errorLabel.setVisible(true);
                valid = false;
            }
        }

        String eName = AttributeValidation.checkString(ecNameInput.getText());
        String eAddress = AttributeValidation.checkString(ecAddressInput.getText());
        String eRegion = AttributeValidation.checkString(ecRegionInput.getText());
        String eRelationship = AttributeValidation.checkString(ecRelationshipInput.getText());

        // the name and cell number are required if any other attributes are filled out
        if ((eName == null || emergencyCell == null) && (emergencyPhone != null || eAddress != null || eRegion != null ||
                emergencyEmail != null || eRelationship != null || eName != null || emergencyCell != null)) {
            valid = false;
            errorLabel.setText("Name and cell phone number are required for an emergency contact.");
            errorLabel.setVisible(true);
        }


        if (valid) { // only updates if everything is valid
            updatePersonalDetails(nhi, fName, dob, dod);
            updateHealthDetails(height, weight);
            updateContactDetails(homePhone, cellPhone, email);
            updateEmergencyContact(eName, emergencyPhone, emergencyCell, eAddress, eRegion, emergencyEmail, eRelationship);

            appController.update(currentUser);
            //ArrayList<Change> diffs = appController.differanceInDonors(oldUser, currentUser);
            //changelog.addAll(diffs);
            AppController appController = AppController.getInstance();
            DonorController donorController = appController.getDonorController();
            try {
                donorController.showUser(currentUser);
            } catch (NullPointerException ex) {
                //TODO causes npe if donor is new in this session
                //the text fields etc. are all null
            }
            stage.close();
        }
    }

    /**
     * Updates all personal details that have changed.
     * @param nhi The national health index to be checked for changes and possibly updated.
     * @param fName The first name to be checked for changes and possibly updated.
     * @param dob The date of birth to be checked for changes and possibly updated.
     * @param dod The date of death to be checked for changes and possibly updated.
     */
    private void updatePersonalDetails(String nhi, String fName, LocalDate dob, LocalDate dod) {
        if (!currentUser.getNhi().equals(nhi)) {
            currentUser.setNhi(nhi);
        }

        if (!currentUser.getFirstName().equals(fName)) {
            currentUser.setFirstName(fName);
        }

        String prefName = preferredFNameTextField.getText();
        if (!currentUser.getPrefFirstName().equals(prefName)) {
            if (prefName.isEmpty()) {
                currentUser.setPreferredFirstName(fName);
            } else {
                currentUser.setPreferredFirstName(preferredFNameTextField.getText());
            }
        }

        String mName = AttributeValidation.checkString(mNameInput.getText());
        String middle = currentUser.getMiddleName();
        if (middle != null && !middle.equals(mName)) {
            currentUser.setMiddleName(mName);
        } else if (middle == null && mName != null) {
            currentUser.setMiddleName(mName);
        }

        String lName = AttributeValidation.checkString(lNameInput.getText());
        String last = currentUser.getLastName();
        if (last != null && !last.equals(lName)) {
            currentUser.setLastName(lName);
        } else if (last == null && lName != null) {
            currentUser.setLastName(lName);
        }

        if (!currentUser.getDateOfBirth().isEqual(dob)) {
            currentUser.setDateOfBirth(dob);
        }

        LocalDate deathDate = currentUser.getDateOfDeath();
        if (deathDate != null && dod != null) {
            if (!deathDate.isEqual(dod)) {
                currentUser.setDateOfDeath(dod);
            }
        } else if ((deathDate == null && dod != null) || deathDate != null) {
            currentUser.setDateOfDeath(dod);
        }
    }

    /**
     * Updates all health details that have changed.
     * @param height The height to be checked for changes and possibly updated.
     * @param weight The weight to be checked for changes and possibly updated.
     */
    private void updateHealthDetails(double height, double weight) {
        if (currentUser.getHeight() != height) {
            currentUser.setHeight(height);
        }

        if (currentUser.getWeight() != weight) {
            currentUser.setWeight(weight);
        }

        String birthGender = currentUser.getBirthGender();
        String bGender = AttributeValidation.validateGender(birthGenderComboBox);
        if (birthGender != null && !birthGender.equals(bGender)) {
            currentUser.setBirthGender(bGender);
        } else if (birthGender == null && bGender != null) {
            currentUser.setBirthGender(bGender);
        }

        String genderIdentity = currentUser.getGenderIdentity();
        String genderID = AttributeValidation.validateGender(genderIdComboBox);
        if (genderIdentity != null && !genderIdentity.equals(genderID)) {
            if (genderID == null) {
                currentUser.setGenderIdentity(birthGender);
            } else {
                currentUser.setGenderIdentity(genderID);
            }
        } else if (genderIdentity == null && genderID != null) {
            currentUser.setGenderIdentity(genderID);
        }

        String bloodType = currentUser.getBloodType();
        String blood = AttributeValidation.validateBlood(bloodComboBox);
        if (bloodType != null && !bloodType.equals(blood)) {
            currentUser.setBloodType(blood);
        } else if (bloodType == null && blood != null) {
            currentUser.setBloodType(blood);
        }

        String alcohol = alcoholComboBox.getValue().toString();
        if (!currentUser.getAlcoholConsumption().equals(alcohol)) {
            currentUser.setAlcoholConsumption(alcohol);
        }

        boolean smoker = smokerCheckBox.isSelected();
        if (currentUser.isSmoker() != smoker) {
            currentUser.setSmoker(smoker);
        }
    }


    /**
     * Updates all contact details that have changed.
     * @param homePhone The home phone number to be checked for changes and possibly updated.
     * @param cellPhone The cell phone number to be checked for changes and possibly updated.
     * @param email The email address to be checked for changes and possibly updated.
     */
    private void updateContactDetails(String homePhone, String cellPhone, String email) {
        if (homePhone != null && !homePhone.equals(currentUser.getHomePhone())) {
            currentUser.setHomePhone(homePhone);
        } else if (homePhone == null && currentUser.getHomePhone() != null) {
            currentUser.setHomePhone(null);
        }

        if (cellPhone != null && !cellPhone.equals(currentUser.getCellPhone())) {
            currentUser.setCellPhone(cellPhone);
        } else if (cellPhone == null && currentUser.getCellPhone() != null) {
            currentUser.setCellPhone(null);
        }

        if (email != null && !email.equals(currentUser.getEmail())) {
            currentUser.setEmail(email);
        } else if (email == null && currentUser.getEmail() != null) {
            currentUser.setEmail(null);
        }

        String address = addressInput.getText();
        if (address.isEmpty() && currentUser.getCurrentAddress() != null) {
            currentUser.setCurrentAddress(null);
        } else if (!address.equals(currentUser.getCurrentAddress())) {
            currentUser.setCurrentAddress(address);
        }

        String region = regionInput.getText();
        if (region.isEmpty() && currentUser.getRegion() != null) {
            currentUser.setRegion(null);
        } else if (!region.equals(currentUser.getRegion())) {
            currentUser.setRegion(region);
        }
    }

    /**
     * Updates all emergency contact details that have changed.
     * @param eName The emergency contact name to be checked for changes and possibly updated.
     * @param emergencyPhone The emergency contact phone number to be checked for changes and possibly updated.
     * @param emergencyCell The emergency contact cell phone number to be checked for changes and possibly updated.
     * @param eAddress The emergency contact address to be checked for changes and possibly updated.
     * @param eRegion The emergency contact region to be checked for changes and possibly updated.
     * @param emergencyEmail The emergency contact email address to be checked for changes and possibly updated.
     * @param eRelationship The relationship between the emergency contact and user to be checked for changes and possibly updated.
     */
    private void updateEmergencyContact(String eName, String emergencyPhone, String emergencyCell, String eAddress,
                                        String eRegion, String emergencyEmail, String eRelationship) {

        EmergencyContact contact = currentUser.getContact();

        String name = contact.getName();
        if (name != null && !name.equals(eName)) {
            contact.setName(eName);
        } else if (name == null && eName != null) {
            contact.setName(eName);
        }

        String ePhone = contact.getHomePhoneNumber();
        if (ePhone != null && !ePhone.equals(emergencyPhone)) {
            contact.setHomePhoneNumber(emergencyPhone);
        } else if (ePhone == null && emergencyPhone != null) {
            contact.setHomePhoneNumber(emergencyPhone);
        }

        String eCell = contact.getCellPhoneNumber();
        if (eCell != null && !eCell.equals(emergencyCell)) {
            contact.setCellPhoneNumber(emergencyCell);
        } else if (eCell == null && emergencyCell != null) {
            contact.setCellPhoneNumber(emergencyCell);
        }

        String address = contact.getAddress();
        if (address != null && !address.equals(eAddress)) {
            contact.setAddress(eAddress);
        } else if (address == null && eAddress != null) {
            contact.setAddress(eAddress);
        }

        String region = contact.getRegion();
        if (region != null && !region.equals(eRegion)) {
            contact.setRegion(eRegion);
        } else if (region == null && eRegion != null) {
            contact.setRegion(eRegion);
        }

        String eEmail = contact.getEmail();
        if (eEmail != null && !eEmail.equals(emergencyEmail)) {
            contact.setEmail(emergencyEmail);
        } else if (eEmail == null && emergencyEmail != null) {
            contact.setEmail(emergencyEmail);
        }

        String relation = contact.getRelationship();
        if (relation != null && !relation.equals(eRelationship)) {
            contact.setRelationship(eRelationship);
        } else if (relation == null && eRelationship != null) {
            contact.setRelationship(eRelationship);
        }
    }

    /**
     * Prompts the user with a warning alert if there are unsaved changes, otherwise cancels immediately.
     * @param event passed in automatically by the gui
     */
    @FXML
    void goBack(ActionEvent event) {
        if (stage.getTitle().equals("Update User: " + currentUser.getFirstName() + " *")) { // has changes
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "You have unsaved changes, are you sure you want to cancel?",
                    ButtonType.YES, ButtonType.NO);

            Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
            yesButton.setId("yesButton");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.YES) {
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
        } else { // has no changes
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
    }

    /**
     * @param actionEvent passed in automatically by the gui
     */
    @FXML
    public void cancelCreation(ActionEvent actionEvent){
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
