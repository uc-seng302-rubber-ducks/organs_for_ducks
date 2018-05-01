package seng302.Controller;


import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import seng302.Exception.InvalidNhiException;
import seng302.Model.EmergencyContact;
import seng302.Model.Memento;
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

  @FXML
  private Button undoButton;

  @FXML
  private Button redoButton;

  private Stage stage;
  private AppController appController;
  private User currentUser;
  private User oldUser;
  private int undoMarker; //int used to hold the top of the stack before opening this window


  /**
   * @param user The current user.
   * @param controller An instance of the AppController class.
   * @param stage The applications stage.
   */
  public void init(User user, AppController controller, Stage stage) {
    this.stage = stage;
    currentUser = user;
    this.appController = controller;
    setUserDetails(currentUser);
    undoButton.setDisable(true);
    redoButton.setDisable(true);
    errorLabel.setText("");
    undoMarker = currentUser.getUndoStack().size();
    if (user.getLastName() != null) {
      stage.setTitle("Update User: " + user.getFirstName() + " " + user.getLastName());
    } else {
      stage.setTitle("Update User: " + user.getFirstName());
    }
    //UndoRedoStacks.cloneUser(currentUser,oldUser);

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
   *
   * @param dp The current date picker.
   */
  private void datePickerListener(DatePicker dp) {
    dp.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (checkChanges()) {
        stage.setTitle("Update User: " + currentUser.getFirstName());
      } else {
        stage.setTitle("Update User: " + currentUser.getFirstName() + " *");
      }
      updateUndos();
    });
  }

  /**
   * Changes the title bar to add/remove an asterisk when a change is detected on the ComboBox.
   *
   * @param cb The current ComboBox.
   */
  private void comboBoxListener(ComboBox cb) {
    cb.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (checkChanges()) {
        stage.setTitle("Update User: " + currentUser.getFirstName());
      } else {
        stage.setTitle("Update User: " + currentUser.getFirstName() + " *");
      }
      updateUndos();
    });

  }


  /**
   * Changes the title bar to add/remove an asterisk when the smoker checkbox is
   * selected/unselected.
   *
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
    updateUndos();
  }

  /**
   * Changes the title bar to contain an asterisk if a change was detected on the textfields.
   *
   * @param field The current textfield.
   */
  private void textFieldListener(TextField field) {
    field.textProperty().addListener((observable, oldValue, newValue) -> {
      stage.setTitle("Update User: " + currentUser.getFirstName() + " *");
      updateUndos();
    });
  }

  /**
   * Checks if all fields are in their original state (i.e. no change has been made / all changes
   * have been undone).
   *
   * @return true if all fields are in their original state, false if at least one field is
   * different.
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

    if (!currentUser.getPreferredFirstName().equals(preferredFNameTextField.getText())) {
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
    } else if (birthGenderComboBox.getValue() != null && !birthGenderComboBox.getValue().toString()
        .equals("")) {
      noChange = false;
    }

    if (currentUser.getGenderIdentity() != null && genderIdComboBox.getValue() != null) {
      if (!(currentUser.getGenderIdentity()).equals(genderIdComboBox.getValue().toString())) {
        noChange = false;
      }
    } else if (genderIdComboBox.getValue() != null && !genderIdComboBox.getValue().toString()
        .equals("")) {
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
    if (!(currentUser.isSmoker() == smokerCheckBox.isSelected())) {
      noChange = false;
    }

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

  @FXML
  public void setUserDetails(User user) {
    //personal
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
    if (user.getBloodType() != null) {
      bloodComboBox.setValue(user.getBloodType());
    }
    if (user.getBirthGender() != null) {
      birthGenderComboBox.setValue(user.getBirthGender());
    }
    if (user.getGenderIdentity() != null) {
      genderIdComboBox.setValue(user.getGenderIdentity());
    }
    if (user.getWeight() > 0) {
      weightInput.setText(Double.toString(user.getWeight()));
    }
    if (user.getHeight() > 0) {
      heightInput.setText(Double.toString(user.getHeight()));
    }

  }

  @FXML
  public boolean getContactDetails() {
    boolean changed = false;
    if (!(phoneInput.getText().isEmpty() && currentUser.getHomePhone() == null) && !phoneInput
        .getText().equals(currentUser.getHomePhone())) {
      currentUser.setHomePhone(phoneInput.getText());
      changed = true;
    }
    if (!(cellInput.getText().isEmpty() && currentUser.getCellPhone() == null) && !cellInput
        .getText().equals(currentUser.getCellPhone())) {
      currentUser.setCellPhone(cellInput.getText());
      changed = true;
    }
    if (!(addressInput.getText().isEmpty() && currentUser.getCurrentAddress() == null)
        && !addressInput.getText().equals(currentUser.getCurrentAddress())) {
      String address = addressInput.getText();
      currentUser.setCurrentAddress(address);
      changed = true;
    }
    if (!(regionInput.getText().isEmpty() && currentUser.getRegion() == null) && !regionInput
        .getText().equals(currentUser.getRegion())) {
      currentUser.setRegion(regionInput.getText());
      changed = true;
    }
    if (!(emailInput.getText().isEmpty() && currentUser.getEmail() == null) && !emailInput.getText()
        .equals(currentUser.getEmail())) {
      currentUser.setEmail(emailInput.getText());
      changed = true;
    }

    return changed;

  }

  @FXML
  public boolean getEmergencyContact() {
    boolean changed = false;
    if (!ecNameInput.getText().isEmpty() && !ecNameInput.getText()
        .equals(currentUser.getContact().getName())) {
      currentUser.getContact().setName(ecNameInput.getText());
      changed = true;
    }
    if (!ecPhoneInput.getText().isEmpty() && !ecPhoneInput.getText()
        .equals(currentUser.getContact().getHomePhoneNumber())) {
      currentUser.getContact().setHomePhoneNumber(ecPhoneInput.getText());
      changed = true;

    }
    if (!ecCellInput.getText().isEmpty() && !ecCellInput.getText()
        .equals(currentUser.getContact().getCellPhoneNumber())) {
      currentUser.getContact().setCellPhoneNumber(ecCellInput.getText());
      changed = true;
    }
    if (!ecAddressInput.getText().isEmpty() && !ecAddressInput.getText()
        .equals(currentUser.getContact().getAddress())) {
      currentUser.getContact().setAddress(ecAddressInput.getText());
      changed = true;
    }
    if (!ecRegionInput.getText().isEmpty() && !ecRegionInput.getText()
        .equals(currentUser.getContact().getRegion())) {
      currentUser.getContact().setRegion(ecRegionInput.getText());
      changed = true;
    }
    if (!ecEmailInput.getText().isEmpty() && !ecEmailInput.getText()
        .equals(currentUser.getContact().getEmail())) {
      currentUser.getContact().setEmail(ecEmailInput.getText());
      changed = true;

    }
    if (!ecRelationshipInput.getText().isEmpty() && !ecNameInput.getText()
        .equals(currentUser.getContact().getRelationship())) {
      currentUser.getContact().setRelationship(ecRelationshipInput.getText());
      changed = true;

    }
    return changed;

  }

  @FXML
  public boolean getHealthDetails() throws NumberFormatException {
    boolean changed = false;
    if (birthGenderComboBox.getValue() != null && !birthGenderComboBox.getValue()
        .equals(currentUser.getBirthGender())) {
      String birthGender = AttributeValidation.validateGender(birthGenderComboBox);
      currentUser.setBirthGender(birthGender);
      changed = true;
    }
    if (genderIdComboBox.getValue() != null && genderIdComboBox.getValue() != ""
        && !genderIdComboBox.getValue().equals(currentUser.getGenderIdentity())) {
      String genderIdentity = AttributeValidation.validateGender(genderIdComboBox);
      currentUser.setGenderIdentity(genderIdentity);
      changed = true;
    }

//        else if (birthGenderComboBox.getValue() != null) {
//           String birthGender = AttributeValidation.validateGender(birthGenderComboBox);
//           currentUser.setGenderIdentity(birthGender);
//      }

    if (bloodComboBox.getValue() != null && !bloodComboBox.getValue()
        .equals(currentUser.getBloodType())) {
      String blood = AttributeValidation.validateBlood(bloodComboBox);
      //BloodTypes blood = AttributeValidation.validateBlood(bloodComboBox);
      currentUser.setBloodType(blood);
      changed = true;
    }

    if (smokerCheckBox.isSelected() != currentUser.isSmoker()) {
      currentUser.setSmoker(smokerCheckBox.isSelected());
      changed = true;
    }

    if (!(currentUser.getAlcoholConsumption() == null && alcoholComboBox.getValue()
        .equals("None")) && !alcoholComboBox.getValue()
        .equals(currentUser.getAlcoholConsumption())) {
      currentUser.setAlcoholConsumption(alcoholComboBox.getValue().toString());
      changed = true;
    }

    if (!weightInput.getText().equals("")) {
      try {
        if (Double.parseDouble(weightInput.getText()) != currentUser.getWeight()) {
          currentUser.setWeight(Double.parseDouble(weightInput.getText()));
          changed = true;
        }
      } catch (NumberFormatException e) {
        errorLabel.setText("Weight must be a valid number");
        throw e;
      }

    }
    if (!heightInput.getText().equals("")) {
      try {
        if (Double.parseDouble(heightInput.getText()) != currentUser.getHeight()) {
          currentUser.setHeight(Double.parseDouble(heightInput.getText()));
          changed = true;
        }
      } catch (NumberFormatException e) {
        errorLabel.setText("Height must be a number");
        throw e;
      }
    }
    return changed;
  }


  @FXML
  public boolean getPersonalDetails() throws InvalidNhiException {
    //TODO check why dateofbirth fails
    boolean changed = false;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    if (!fNameInput.getText().equals(currentUser.getFirstName())) {
      currentUser.setFirstName(fNameInput.getText());
      changed = true;
    }

    if (!(lNameInput.getText().isEmpty() && currentUser.getMiddleName() == null) && !lNameInput
        .getText().equals(currentUser.getLastName())) {
      currentUser.setLastName(lNameInput.getText());
      changed = true;
    }

    if (!nhiInput.getText().equals(currentUser.getNhi())) {
      if (AttributeValidation.validateNHI(nhiInput.getText()) != null) {
        currentUser.setNhi(nhiInput.getText());
        changed = true;
      } else {
        errorLabel.setText("NHI is in valid please enter it in the form ABC1234");
        throw new InvalidNhiException();
      }
    }

    if (!(mNameInput.getText().isEmpty() && currentUser.getMiddleName() == null) && !mNameInput
        .getText().equals(currentUser.getMiddleName())) {
      currentUser.setMiddleName(mNameInput.getText());
      changed = true;
    }

    if (dobInput.getValue() != null && !dobInput.getValue().equals(currentUser.getDateOfBirth())) {
      currentUser.setDateOfBirth(dobInput.getValue());
      changed = true;
    }

    if (dodInput.getValue() != null && !dodInput.getValue()
        .equals(currentUser.getDateOfDeath())) {
      currentUser.setDateOfDeath(dodInput.getValue());
      changed = true;
    }
    if (!preferredFNameTextField.getText().isEmpty()) {
      if (!preferredFNameTextField.getText().equals(currentUser.getPreferredFirstName())
          || !preferredFNameTextField.getText().equals(currentUser.getFirstName())) {
        currentUser.setPreferredFirstName(preferredFNameTextField.getText());
        changed = true;
      }
    }
    return changed;

  }


  /*
   *
   * @param actionEvent an action event.
   * @throws IOException doesn't look like this even throws..
   */
  @FXML
  public void confirmUpdate(ActionEvent actionEvent) throws IOException {
    boolean changed = false;

    hideErrorMessages();
    errorLabel.setText("Please make sure your details are correct.");
    validateFields();

    //TODO change to be different

    appController.update(currentUser);

    Memento<User> sumChanges = new Memento<>();
    System.out.println(undoMarker);
    System.out.println(currentUser.getUndoStack().size());
    while (currentUser.getUndoStack().size() > undoMarker + 1) {
      System.out.println(currentUser.getUndoStack().size());
      currentUser.getUndoStack().pop();
    }
    sumChanges.setOldObject(currentUser.getUndoStack().peek().getOldObject().clone());
    currentUser.getUndoStack().pop();
    sumChanges.setNewObject(currentUser.clone());
    currentUser.getUndoStack().push(sumChanges);
    System.out.println(sumChanges);
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
    /**
     * Activates when the user clicks the save changes button.
     * Calls a method to check if all field are valid and then updates the changes.
     * Also resets error labels.
     *
     * @param actionEvent The user clicks on the save changes button.
     * @throws IOException input/output exception.
     */

    /**
     * Checks if all fields that require validation are valid.
     * Sets error messages visible if fields are invalid.
     * Calls methods to update the changes if all fields are valid.
     */
    private void validateFields () {
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

      }
    }

  public void updateUndos() {
    boolean changed = false;
    double weight;
    double height;
    changed |= updatePersonalDetails(nhiInput.getText(), fNameInput.getText(), dobInput.getValue(),
        dodInput.getValue());
    try {
      height = Double
          .parseDouble(heightInput.getText().isEmpty() ? "0" : heightInput.getText());
      weight = Double
          .parseDouble(weightInput.getText().isEmpty() ? "0" : weightInput.getText());
    } catch (NumberFormatException e) {
      height = 0;
      weight = 0;
    }
    changed |= updateHealthDetails(height, weight);
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
    undoButton.setDisable(currentUser.getUndoStack().isEmpty());
    redoButton.setDisable(currentUser.getRedoStack().isEmpty());
    appController.update(currentUser);
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
        changed = true;
      }

      String prefName = preferredFNameTextField.getText();
      if (!currentUser.getPreferredFirstName().equals(prefName)) {
        if (prefName.isEmpty()) {
          currentUser.setPreferredFirstName(fName);
          changed = true;
        } else {
          currentUser.setPreferredFirstName(preferredFNameTextField.getText());
          changed = true;
        }
      }

      String mName = AttributeValidation.checkString(mNameInput.getText());
      String middle = currentUser.getMiddleName();
      if (middle != null && !middle.equals(mName)) {
        currentUser.setMiddleName(mName);
        changed = true;
      } else if (middle == null && mName != null) {
        currentUser.setMiddleName(mName);
        changed = true;
      }

      String lName = AttributeValidation.checkString(lNameInput.getText());
      String last = currentUser.getLastName();
      if (last != null && !last.equals(lName)) {
        currentUser.setLastName(lName);
        changed = true;
      } else if (last == null && lName != null) {
        currentUser.setLastName(lName);
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
    private boolean updateHealthDetails(double height, double weight) {
      boolean changed = false;
      if (currentUser.getHeight() != height) {
        currentUser.setHeight(height);
        changed = true;
      }

      if (currentUser.getWeight() != weight) {
        currentUser.setWeight(weight);
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

      if (homePhone == null && currentUser.getHomePhone() != null) {
        currentUser.setHomePhone(null);
        changed = true;
      } else if (homePhone != null && !homePhone.equals(currentUser.getHomePhone())) {
        currentUser.setHomePhone(homePhone);
        changed = true;
      }

      if (cellPhone != null && !cellPhone.equals(currentUser.getCellPhone())) {
        currentUser.setCellPhone(cellPhone);
        changed = true;
      } else if (cellPhone == null && currentUser.getCellPhone() != null) {
        currentUser.setCellPhone(null);
        changed = true;
      }

      if (email != null && !email.equals(currentUser.getEmail())) {
        currentUser.setEmail(email);
        changed = true;
      } else if (email == null && currentUser.getEmail() != null) {
        currentUser.setEmail(null);
        changed = true;
      }

      String address = addressInput.getText();
      if (!address.isEmpty() && !address.equals(currentUser.getCurrentAddress())) {
        currentUser.setCurrentAddress(address);
        changed = true;
      } else if (address.isEmpty() && currentUser.getCurrentAddress() != null) {
        currentUser.setCurrentAddress(null);
        changed = true;
      }

      String region = regionInput.getText();
      if (!region.isEmpty() && !region.equals(currentUser.getRegion())) {
        currentUser.setRegion(region);
        changed = true;
      } else if (region.isEmpty() && currentUser.getRegion() != null) {
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
      if (!name.isEmpty() && !name.equals(eName)) {
        contact.setName(eName);
        changed = true;
      } else if (name.isEmpty() && eName != null) {
        contact.setName(eName);
        changed = true;
      }

      String ePhone = contact.getHomePhoneNumber();
      if (!ePhone.isEmpty() && !ePhone.equals(emergencyPhone)) {
        contact.setHomePhoneNumber(emergencyPhone);
        changed = true;
      } else if (ePhone.isEmpty() && emergencyPhone != null) {
        contact.setHomePhoneNumber(emergencyPhone);
        changed = true;
      }

      String eCell = contact.getCellPhoneNumber();
      if (!eCell.isEmpty() && !eCell.equals(emergencyCell)) {
        contact.setCellPhoneNumber(emergencyCell);
        changed = true;
      } else if (eCell.isEmpty() && emergencyCell != null) {
        contact.setCellPhoneNumber(emergencyCell);
        changed = true;
      }

      String address = contact.getAddress();
      if (!address.isEmpty() && !address.equals(eAddress)) {
        contact.setAddress(eAddress);
        changed = true;
      } else if (address.isEmpty() && eAddress != null) {
        contact.setAddress(eAddress);
        changed = true;
      }

      String region = contact.getRegion();
      if (!region.isEmpty() && !region.equals(eRegion)) {
        contact.setRegion(eRegion);
        changed = true;
      } else if (region.isEmpty() && eRegion != null) {
        contact.setRegion(eRegion);
        changed = true;
      }

      String eEmail = contact.getEmail();
      if (!eEmail.isEmpty() && !eEmail.equals(emergencyEmail)) {
        contact.setEmail(emergencyEmail);
        changed = true;
      } else if (eEmail.isEmpty() && emergencyEmail != null) {
        contact.setEmail(emergencyEmail);
        changed = true;
      }

      String relation = contact.getRelationship();
      if (!relation.isEmpty() && !relation.equals(eRelationship)) {
        contact.setRelationship(eRelationship);
        changed = true;
      } else if (relation.isEmpty() && eRelationship != null) {
        contact.setRelationship(eRelationship);
        changed = true;
      }

      return changed;
    }

  @FXML
    void undo () {
      currentUser.undo();
      undoButton.setDisable(currentUser.getUndoStack().isEmpty());
      setUserDetails(currentUser);
    }

    @FXML
    void redo () {
      currentUser.redo();
      redoButton.setDisable(currentUser.getRedoStack().isEmpty());
      setUserDetails(currentUser);
    }

    /**
     * Prompts the user with a warning alert if there are unsaved changes, otherwise cancels
     * immediately.
     *
     * @param event passed in automatically by the gui
     */
    @FXML
    void goBack (ActionEvent event){
      if (stage.getTitle()
          .equals("Update User: " + currentUser.getFirstName() + " *")) { // has changes
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
          donorController.showUser(currentUser);
        } catch (NullPointerException ex) {
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
    public void cancelCreation (ActionEvent actionEvent){
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
