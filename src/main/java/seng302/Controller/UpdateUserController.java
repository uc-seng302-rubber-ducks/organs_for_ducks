package seng302.Controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import seng302.Model.Change;
import seng302.Model.UndoRedoStacks;
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
        errorLabel.setText("");
        if (user.getLastName() != null) {
          stage.setTitle("Update User: " + user.getFirstName() +" " + user.getLastName());
        } else {
          stage.setTitle("Update User: " + user.getFirstName());
        }
        //UndoRedoStacks.cloneUser(currentUser,oldUser);



    }

    @FXML
    public void setUserDetails(User user) {
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

    @FXML
    public boolean getContactDetaisl() {
        boolean passes = true;
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
        return passes;
    }

    @FXML
    public boolean getEmergencyContact() {
        boolean passes = true;
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
        return passes;

    }

    @FXML
    public boolean getHealthDetails() {
        boolean passes = true;
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
            errorLabel.setText("Weight must be a valid number");
            passes = false;
          }

        }
        if (!heightInput.getText().equals("")) {
            try {
                currentUser.setHeight(Double.parseDouble(heightInput.getText()));
            } catch (NumberFormatException e){
                errorLabel.setText("Height must be a number");
                passes = false;
            }
        }
        return passes;
    }


    @FXML
    public boolean getPersonalDetails() {
        //TODO check why dateofbirth fails
        boolean passes = true;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (!fNameInput.getText().equals("")) {
          currentUser.setFirstName(fNameInput.getText());
        }

        if (!lNameInput.getText().equals("")) {
          currentUser.setLastName(lNameInput.getText());
        }

        if (!nhiInput.getText().equals("")) {
            if (nhiInput.getText().matches("[A-Z]{3}[0-9]{4}")) {
                currentUser.setNhi(nhiInput.getText());
            } else {
                errorLabel.setText("NHI is in valid please enter it in the form ABC1234");
                passes = false;
            }
        }

        if (!mNameInput.getText().equals("")) {
          currentUser.setMiddleName(mNameInput.getText());
        }

        if (dobInput.getValue() != null) {
          currentUser.setDateOfBirth(dobInput.getValue());
        }

         if(dodInput.getValue()!=null) {
          currentUser.setDateOfDeath(dodInput.getValue());
        }
        if (preferredFNameTextField.getText() != null){
          currentUser.setPreferredFirstName(preferredFNameTextField.getText());
        } else {
          currentUser.setPreferredFirstName(fNameInput.getText());
        }

        return passes;

}






    /**
     *
     * @param actionEvent an action event.
     * @throws IOException doesn't look like this even throws..
     */
    @FXML
    public void confirmUpdate(ActionEvent actionEvent) throws IOException {
        //TODO save changes and go back to overview screen
        errorLabel.setText("");
        boolean personalDetails = getPersonalDetails();
        boolean healthDetails = getHealthDetails();
        boolean contactDetails  = getContactDetaisl();
        boolean emergencyDetails = getEmergencyContact();
        //TODO change to be different
        if (!(personalDetails && healthDetails && contactDetails && emergencyDetails)){
            return;
        }
      appController.update(currentUser);
      //ArrayList<Change> diffs = appController.differanceInDonors(oldUser, currentUser);
      //changelog.addAll(diffs);
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
     * @param event passed in automatically by the gui
     */
    @FXML
    void goBack(ActionEvent event) {
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
}
