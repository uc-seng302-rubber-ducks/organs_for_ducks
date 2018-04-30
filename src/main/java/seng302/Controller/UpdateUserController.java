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

        if (!currentUser.getDateOfBirth().isEqual(dobInput.getValue())) {
            noChange = false;
        }

        if (currentUser.getDateOfDeath() != null) {
            if (!currentUser.getDateOfDeath().isEqual(dodInput.getValue())) {
                noChange = false;
            }
        } else if (dodInput.getValue() != null) {
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

    public void getContactDetaisl() {
        if (phoneInput.getText() != null){
          currentUser.setHomePhone(phoneInput.getText());
        } else {
          currentUser.setHomePhone(null);
        }
        if (cellInput.getText() != null){
          currentUser.setCellPhone(cellInput.getText());
        } else{
          currentUser.setCellPhone(null);
        }
        if (addressInput.getText() != null){
            String address = addressInput.getText().toString();
            currentUser.setCurrentAddress(address);
        } else{
          currentUser.setCurrentAddress(null);
        }
        if (regionInput.getText() != null){
          currentUser.setRegion(regionInput.getText());
        } else {
          currentUser.setRegion(null);
        }
        if (emailInput.getText() != null){
          currentUser.setEmail(emailInput.getText());
        } else {
          currentUser.setEmail(null);
        }

    }

    public void getEmergencyContact() {
        if (!ecNameInput.getText().isEmpty()) {
          currentUser.getContact().setName(ecNameInput.getText());
        }
        if (!ecPhoneInput.getText().isEmpty()) {
          currentUser.getContact().setHomePhoneNumber(ecPhoneInput.getText());

        } else {
          currentUser.getContact().setHomePhoneNumber(null);
        }
        if (!ecCellInput.getText().isEmpty() ) {
          currentUser.getContact().setCellPhoneNumber(ecCellInput.getText());
        } else{
          currentUser.getContact().setCellPhoneNumber(null);
        }
        if (!ecAddressInput.getText().isEmpty())  {
          currentUser.getContact().setAddress(ecAddressInput.getText());
        } else {
          currentUser.getContact().setAddress(null);
        }
        if (!ecRegionInput.getText().isEmpty()) {
          currentUser.getContact().setRegion(ecRegionInput.getText());
        } else {
          currentUser.getContact().setRegion(null);
        }
        if (!ecEmailInput.getText().isEmpty()) {
          currentUser.getContact().setEmail(ecEmailInput.getText());

        } else {
          currentUser.getContact().setEmail(null);
        }
        if (!ecRelationshipInput.getText().isEmpty()) {
          currentUser.getContact().setRelationship(ecRelationshipInput.getText());

        } else {
          currentUser.getContact().setRelationship(null);
        }

    }

    public void getHealthDetails() {
        if (birthGenderComboBox.getValue() != null) {
            String birthGender = AttributeValidation.validateGender(birthGenderComboBox);
            currentUser.setBirthGender(birthGender);
        }
        if (genderIdComboBox.getValue() != null && genderIdComboBox.getValue() != ""){
            String genderIdentity = AttributeValidation.validateGender(genderIdComboBox);
            currentUser.setGenderIdentity(genderIdentity);
        } else if (birthGenderComboBox.getValue() != null) {
           String birthGender = AttributeValidation.validateGender(birthGenderComboBox);
           currentUser.setGenderIdentity(birthGender);
      }
        if (bloodComboBox.getValue() != null) {
            String blood = AttributeValidation.validateBlood(bloodComboBox);
            //BloodTypes blood = AttributeValidation.validateBlood(bloodComboBox);
          currentUser.setBloodType(blood);


        }
        if (smokerCheckBox.isSelected()) {
          currentUser.setSmoker(true);
        }else{
          currentUser.setSmoker(false);
        }
        if (alcoholComboBox.getValue() != null) {
          currentUser.setAlcoholConsumption(alcoholComboBox.getValue().toString());
        }

        if (!weightInput.getText().equals("")) {
          try {
            currentUser.setWeight(Double.parseDouble(weightInput.getText()));
          } catch (NumberFormatException e){
            System.out.println("nope");
          }

        }
        if (!heightInput.getText().equals("")) {
          currentUser.setHeight(Double.parseDouble(heightInput.getText()));
        }
    }


    public void getPersonalDetails() {
        if (!fNameInput.getText().equals("")) {
          currentUser.setFirstName(fNameInput.getText());
        }

        if (!lNameInput.getText().equals("")) {
          currentUser.setLastName(lNameInput.getText());
        } else {
          currentUser.setLastName("");
        }

        if (!nhiInput.getText().equals("")) {
          currentUser.setNhi(nhiInput.getText());
        }

        if (!mNameInput.getText().equals("")) {
          currentUser.setMiddleName(mNameInput.getText());
        } else {
          currentUser.setMiddleName("");
        }

        if (dobInput.getValue() != null) {
          currentUser.setDateOfBirth(dobInput.getValue());
        }

         if(dodInput.getValue()!=null) {
          currentUser.setDateOfDeath(dodInput.getValue());
        }
        if (!preferredFNameTextField.getText().isEmpty()){
          System.out.println(preferredFNameTextField.getText());
          currentUser.setPreferredFirstName(preferredFNameTextField.getText());
        } else {
          currentUser.setPreferredFirstName(fNameInput.getText());
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
//        getPersonalDetails();
//        getHealthDetails();
//        getContactDetaisl();
//        getEmergencyContact();

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
            getPersonalDetails();
            getHealthDetails();
            getContactDetaisl();
            getEmergencyContact();

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
