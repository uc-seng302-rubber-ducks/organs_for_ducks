package seng302.Controller;


import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import seng302.Model.EmergencyContact;
import seng302.Model.Memento;
import seng302.Model.User;
import seng302.Service.AttributeValidation;

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
   * @param user The current user.
   * @param controller An instance of the AppController class.
   * @param stage The applications stage.
   */
  public void init(User user, AppController controller, Stage stage) {
    this.stage = stage;
    currentUser = user;
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
      goBack(new ActionEvent());
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
    dp.valueProperty().addListener((observable, oldValue, newValue) -> {
      update();
    });
  }

  /**
   * Listens for changes on the given checkbox and calls for updates.
   *
   * @param checkBox The given CheckBox.
   */
  private void addCheckBoxListener(CheckBox checkBox) {
    checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      update();
    });
  }

  /**
   * Changes the title bar to add/remove an asterisk when a change is detected on the ComboBox.
   *
   * @param cb The current ComboBox.
   */
  private void comboBoxListener(ComboBox cb) {
    cb.valueProperty().addListener((observable, oldValue, newValue) -> {
      update();
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
   * @param user The current user.
   */
  @FXML
  public void setUserDetails(User user) {
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
      weightInput.setText("");
    }
    if (user.getHeightText() != null) {
      heightInput.setText(user.getHeightText());
    } else {
      heightInput.setText("");
    }
    listen = true;

    undoUpdateButton.setDisable(currentUser.getUndoStack().size() <= undoMarker);
    redoUpdateButton.setDisable(currentUser.getRedoStack().isEmpty());

  }

  /**
   * @param actionEvent an action event.
   * @throws IOException doesn't look like this even throws..
   */
  @FXML
  public void confirmUpdate(ActionEvent actionEvent) throws IOException {
    boolean changed = false;

    hideErrorMessages();
    errorLabel.setText("Please make sure your details are correct.");
    boolean valid = validateFields();

    //TODO change to be different

    if (valid) {
      sumAllChanged();
      //ArrayList<Change> diffs = appController.differanceInDonors(oldUser, currentUser);
      //changelog.addAll(diffs);
      AppController appController = AppController.getInstance();
      DonorController donorController = appController.getDonorController();
      try {
        currentUser.getRedoStack().clear();
        donorController.showUser(currentUser);
      } catch (NullPointerException ex) {
        //TODO causes npe if donor is new in this session
        //the text fields etc. are all null
      }
      stage.close();
    }
  }

  /**
   * Turns all form changes into one memento on the stack
   */
  private void sumAllChanged() {
    Memento<User> sumChanges = new Memento<>();
    removeFormChanges(1);
    if (!currentUser.getUndoStack().isEmpty()) {
      sumChanges.setOldObject(currentUser.getUndoStack().peek().getOldObject().clone());
      currentUser.getUndoStack().pop();
      sumChanges.setNewObject(currentUser.clone());
      currentUser.getUndoStack().push(sumChanges);
    }
  }

  /**
   * Pops all but the specified number of changes off the stack.
   * @param offset an denotes how many changes to leave in the stack.
   */
  private void removeFormChanges(int offset) {
    while (currentUser.getUndoStack().size() > undoMarker + offset) {
      currentUser.getUndoStack().pop();
    }
  }

    /**
     * Checks if all fields that require validation are valid.
     * Sets error messages visible if fields are invalid.
     * Calls methods to update the changes if all fields are valid.
     */
    private boolean validateFields () {
      boolean changed = false;

      boolean valid = true;
      String nhi = AttributeValidation.validateNHI(nhiInput.getText());
      LocalDate dob = dobInput.getValue();
      LocalDate dod = dodInput.getValue();

      if (nhi == null) {
        invalidNHI.setVisible(true);
        valid = false;
      } else {
        User user = appController.findUser(nhi);
        if (user != null && !user.getNhi()
            .equals(nhi)) { // if a user was found, but it is not the current user
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
      } else if (!dob.isBefore(
          LocalDate.now().plusDays(1))) { // checks that the date of birth is before tomorrow's date
        invalidDOB.setVisible(true);
        valid = false;
      }

      if (dod != null) {
        boolean datesValid = AttributeValidation.validateDates(dob,
            dod); // checks if the dod is before tomorrow's date and that the dob is before the dod
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
      } else {
        currentUser.setHeight(height);
        currentUser.setWeight(weight);
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
      if ((eName == null || emergencyCell == null) && (emergencyPhone != null || eAddress != null
          || eRegion != null ||
          emergencyEmail != null || eRelationship != null || eName != null
          || emergencyCell != null)) {
        valid = false;
        errorLabel.setText("Name and cell phone number are required for an emergency contact.");
        errorLabel.setVisible(true);
      }
      if (valid) { // only updates if everything is valid
        appController.update(currentUser);
      }
      return valid;
    }

  /**
   * Updates the undo stacks of the form.
   */
  private void updateUndos() {
    boolean changed = false;
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

  private double getDoubleFromTextField(TextField textField) {
    try {
      return Double
          .parseDouble(textField.getText().isEmpty() ? "0" : textField.getText());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

    /**
     * Updates all personal details that have changed.
     * @param nhi The national health index to be checked for changes and possibly updated.
     * @param fName The first name to be checked for changes and possibly updated.
     * @param dob The date of birth to be checked for changes and possibly updated.
     * @param dod The date of death to be checked for changes and possibly updated.
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
      String bGender = AttributeValidation.validateGender(birthGenderComboBox);

      if (birthGender != null && !birthGender.equals(bGender)) {
        currentUser.setBirthGender(bGender);
        changed = true;
      } else if (birthGender == null && bGender != null) {
        currentUser.setBirthGender(bGender);
        changed = true;
      }

      String genderIdentity = currentUser.getGenderIdentity();
      String genderID = AttributeValidation.validateGender(genderIdComboBox);
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
      String blood = AttributeValidation.validateBlood(bloodComboBox);
      if (bloodType != null && !bloodType.equals(blood)) {
        currentUser.setBloodType(blood);
        changed = true;
      } else if (bloodType == null && blood != null) {
        currentUser.setBloodType(blood);
        changed = true;
      }

      String alcohol = alcoholComboBox.getValue().toString();
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
     * @param homePhone The home phone number to be checked for changes and possibly updated.
     * @param cellPhone The cell phone number to be checked for changes and possibly updated.
     * @param email The email address to be checked for changes and possibly updated.
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
     * @param eName The emergency contact name to be checked for changes and possibly updated.
     * @param emergencyPhone The emergency contact phone number to be checked for changes and possibly updated.
     * @param emergencyCell The emergency contact cell phone number to be checked for changes and possibly updated.
     * @param eAddress The emergency contact address to be checked for changes and possibly updated.
     * @param eRegion The emergency contact region to be checked for changes and possibly updated.
     * @param emergencyEmail The emergency contact email address to be checked for changes and possibly updated.
     * @param eRelationship The relationship between the emergency contact and user to be checked for changes and possibly updated.
     */
    private boolean updateEmergencyContact(String eName, String emergencyPhone, String
    emergencyCell, String eAddress,
        String eRegion, String emergencyEmail, String eRelationship){
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
    void undo () {
      currentUser.undo();
      undoUpdateButton.setDisable(currentUser.getUndoStack().size() <= undoMarker);
      setUserDetails(currentUser);

      if (undoUpdateButton.isDisabled()) {
        stage.setTitle(stage.getTitle().substring(0, stage.getTitle().length() - 1));
      }
    }

  /**
   * Redoes a form change
   */
  @FXML
    void redo () {
      currentUser.redo();
      redoUpdateButton.setDisable(currentUser.getRedoStack().isEmpty());
      setUserDetails(currentUser);
    }

    /**
     * Prompts the user with a warning alert if there are unsaved changes, otherwise cancels
     * immediately.
     *
     * @param event An ActionEvent object usually automatically passed by the GUI.
     */
    @FXML
    void goBack (ActionEvent event){
      if (!undoUpdateButton.isDisabled()) { // has changes
        Alert alert = new Alert(Alert.AlertType.WARNING,
            "You have unsaved changes, are you sure you want to cancel?",
            ButtonType.YES, ButtonType.NO);

        Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
        yesButton.setId("yesButton");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.YES) {
          //currentUser.changeInto(oldUser);
          AppController appController = AppController.getInstance();
          DonorController donorController = appController.getDonorController();
          try {
            currentUser.getRedoStack().clear();
            donorController.showUser(oldUser);
          } catch (NullPointerException ex) {
            //TODO causes npe if donor is new in this session
            //the text fields etc. are all null
          }
          stage.close();
        }
      } else { // has no changes
        AppController appController = AppController.getInstance();
        DonorController donorController = appController.getDonorController();
        try {
          currentUser.getRedoStack().clear();
          donorController.showUser(oldUser);
        } catch (NullPointerException ex) {
          //TODO causes npe if donor is new in this session
          //the text fields etc. are all null
        }
        stage.close();
      }
    }

    /**
     * Makes all the error messages no longer visible.
     */
    private void hideErrorMessages () {
      existingNHI.setVisible(false);
      invalidNHI.setVisible(false);
      invalidDOB.setVisible(false);
      invalidDOD.setVisible(false);
      errorLabel.setVisible(false);
      invalidFirstName.setVisible(false);
    }
  }
