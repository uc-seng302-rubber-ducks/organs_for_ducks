package seng302.Controller;


import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Stack;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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
        //Stack<Memento<User>> undoStack = user.getUndoStack();
//      Memento<User> bottom = undoStack.peek();
//        oldUser = new User();
        setUserDetails(currentUser);
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
      if (user.getPreferredFirstName() != null) {
        preferredFNameTextField.setText(user.getPreferredFirstName());
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
      if (user.getContact().getHomePhoneNumber() != null) {
        ecPhoneInput.setText(user.getContact().getHomePhoneNumber());
      }
      if (user.getContact().getEmail() != null) {
        ecEmailInput.setText(user.getContact().getEmail());
      }
      if (user.getContact().getAddress() != null) {
        ecAddressInput.setText(user.getContact().getAddress());

      }
      if (user.getContact().getCellPhoneNumber() != null) {
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
    public void getContactDetails() {
        if (phoneInput.getText() != null  && !phoneInput.getText().equals(currentUser.getHomePhone())){
          currentUser.setHomePhone(phoneInput.getText());
        }
        if (cellInput.getText() != null && !phoneInput.getText().equals(currentUser.getCellPhone())){
          currentUser.setCellPhone(cellInput.getText());
        }
        if (addressInput.getText() != null && !phoneInput.getText().equals(currentUser.getCurrentAddress())){
            String address = addressInput.getText();
            currentUser.setCurrentAddress(address);
        }
        if (regionInput.getText() != null && !phoneInput.getText().equals(currentUser.getRegion())){
          currentUser.setRegion(regionInput.getText());
        }
        if (emailInput.getText() != null && !phoneInput.getText().equals(currentUser.getEmail())){
          currentUser.setEmail(emailInput.getText());
        }

    }

    @FXML
    public void getEmergencyContact() {
        if (!ecNameInput.getText().isEmpty() && !ecNameInput.getText().equals(currentUser.getContact().getName())) {
          currentUser.getContact().setName(ecNameInput.getText());
        }
        if (!ecPhoneInput.getText().isEmpty() && !ecPhoneInput.getText().equals(currentUser.getContact().getHomePhoneNumber())) {
          currentUser.getContact().setHomePhoneNumber(ecPhoneInput.getText());

        }
        if (!ecCellInput.getText().isEmpty() && !ecCellInput.getText().equals(currentUser.getContact().getCellPhoneNumber())) {
          currentUser.getContact().setCellPhoneNumber(ecCellInput.getText());
        }
        if (!ecAddressInput.getText().isEmpty() && !ecAddressInput.getText().equals(currentUser.getContact().getAddress()))  {
          currentUser.getContact().setAddress(ecAddressInput.getText());
        }
        if (!ecRegionInput.getText().isEmpty() && !ecRegionInput.getText().equals(currentUser.getContact().getRegion())) {
          currentUser.getContact().setRegion(ecRegionInput.getText());
        }
        if (!ecEmailInput.getText().isEmpty() && !ecEmailInput.getText().equals(currentUser.getContact().getEmail())) {
          currentUser.getContact().setEmail(ecEmailInput.getText());

        }
        if (!ecRelationshipInput.getText().isEmpty() && !ecNameInput.getText().equals(currentUser.getContact().getRelationship())) {
          currentUser.getContact().setRelationship(ecRelationshipInput.getText());

        }

    }

    @FXML
    public void getHealthDetails() {
        if (birthGenderComboBox.getValue() != null && !birthGenderComboBox.getValue().equals(currentUser.getBirthGender())) {
            String birthGender = AttributeValidation.validateGender(birthGenderComboBox);
            currentUser.setBirthGender(birthGender);
        }
        if (genderIdComboBox.getValue() != null && genderIdComboBox.getValue() != "" && !genderIdComboBox.getValue().equals(currentUser.getGenderIdentity())){
            String genderIdentity = AttributeValidation.validateGender(genderIdComboBox);
            currentUser.setGenderIdentity(genderIdentity);
        }

//        else if (birthGenderComboBox.getValue() != null) {
//           String birthGender = AttributeValidation.validateGender(birthGenderComboBox);
//           currentUser.setGenderIdentity(birthGender);
//      }

        if (bloodComboBox.getValue() != null && !bloodComboBox.getValue().equals(currentUser.getBloodType())) {
            String blood = AttributeValidation.validateBlood(bloodComboBox);
            //BloodTypes blood = AttributeValidation.validateBlood(bloodComboBox);
          currentUser.setBloodType(blood);
        }

        if (smokerCheckBox.isSelected() && smokerCheckBox.isSelected() != currentUser.isSmoker()) {
          currentUser.setSmoker(true);
        }else{
          currentUser.setSmoker(false);
        }
        if (alcoholComboBox.getValue() != null && !alcoholComboBox.getValue().equals(currentUser.getAlcoholConsumption())) {
          currentUser.setAlcoholConsumption(alcoholComboBox.getValue().toString());
        }

        if (!weightInput.getText().equals("")) {
          try {
              if (Double.parseDouble(weightInput.getText()) != currentUser.getWeight()) {
                  currentUser.setWeight(Double.parseDouble(weightInput.getText()));
              }
          } catch (NumberFormatException e) {
            System.out.println("nope");
          }

        }
        if (!heightInput.getText().equals("")) {
            if (Double.parseDouble(heightInput.getText()) != currentUser.getHeight()) {
                currentUser.setHeight(Double.parseDouble(heightInput.getText()));
            }
        }
    }


    @FXML
    public void getPersonalDetails() {
        //TODO check why dateofbirth fails
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (!fNameInput.getText().equals("") && !fNameInput.getText().equals(currentUser.getFirstName())) {
          currentUser.setFirstName(fNameInput.getText());
        }

        if (!lNameInput.getText().equals("") && !lNameInput.getText().equals(currentUser.getLastName())) {
          currentUser.setLastName(lNameInput.getText());
        }

        if (!nhiInput.getText().equals("") && !nhiInput.getText().equals(currentUser.getNhi())) {
          currentUser.setNhi(nhiInput.getText());
        }

        if (!mNameInput.getText().equals("") && !mNameInput.getText().equals(currentUser.getMiddleName())) {
          currentUser.setMiddleName(mNameInput.getText());
        }

        if (dobInput.getValue() != null && !dobInput.getValue().equals(currentUser.getDateOfBirth())) {
          currentUser.setDateOfBirth(dobInput.getValue());
        }

         if(dodInput.getValue()!=null && !dodInput.getValue().equals(currentUser.getDateOfDeath())) {
          currentUser.setDateOfDeath(dodInput.getValue());
        }
        if (preferredFNameTextField.getText() != null && !preferredFNameTextField.getText().equals(currentUser.getPreferredFirstName())){
          currentUser.setPreferredFirstName(preferredFNameTextField.getText());
        }

}






    /**
     *
     * @param actionEvent an action event.
     * @throws IOException doesn't look like this even throws..
     */
    @FXML
    public void confirmUpdate(ActionEvent actionEvent) throws IOException {
        //TODO save changes and go back to overview screen
        getPersonalDetails();
        getHealthDetails();
        getContactDetails();
        getEmergencyContact();
        //TODO change to be different

      appController.update(currentUser);
      //ArrayList<Change> diffs = appController.differanceInDonors(oldUser, currentUser);
      //changelog.addAll(diffs);
      currentUser.getRedoStack().clear(); // clear the redo stack if anything else is changed.
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
