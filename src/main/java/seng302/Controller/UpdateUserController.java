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
     * @param user
     * @param controller
     * @param stage
     */
    public void init(User user, AppController controller, Stage stage){
        this.stage = stage;
        currentUser = user;
        this.appController = controller;
        //UndoRedoStacks.storeUndoCopy(currentUser);
        currentUser = user;
        oldUser = new User();
        //UndoRedoStacks.cloneUser(currentUser,oldUser);


    }

    public void getContactDetaisl() {
        if (phoneInput.getText() != null){
          currentUser.setHomePhone(phoneInput.getText());

        }
        if (cellInput.getText() != null){
          currentUser.setCellPhone(cellInput.getText());

        }
        if (addressInput.getText() != null){
            String address = addressInput.getText().toString();
            currentUser.setCurrentAddress(address);

        }
        if (regionInput.getText() != null){
          currentUser.setRegion(regionInput.getText());

        }
        if (emailInput.getText() != null){
          currentUser.setEmail(emailInput.getText());

        }

    }

    public void getEmergencyContact() {
        if (!ecNameInput.getText().isEmpty()) {
          currentUser.getContact().setName(ecNameInput.getText());

        }
        if (!ecPhoneInput.getText().isEmpty()) {
          currentUser.getContact().setHomePhoneNumber(ecPhoneInput.getText());

        }
        if (!ecCellInput.getText().isEmpty() ) {
          currentUser.getContact().setCellPhoneNumber(ecCellInput.getText());

        }
        if (!ecAddressInput.getText().isEmpty())  {
          currentUser.getContact().setAddress(ecAddressInput.getText());

        }
        if (!ecRegionInput.getText().isEmpty()) {
          currentUser.getContact().setRegion(ecRegionInput.getText());

        }
        if (!ecEmailInput.getText().isEmpty()) {
          currentUser.getContact().setEmail(ecEmailInput.getText());

        }
        if (!ecRelationshipInput.getText().isEmpty()) {
          currentUser.getContact().setRelationship(ecRelationshipInput.getText());

        }

    }

    public void getHealthDetails() {
        if (birthGenderComboBox.getValue() != null) {
            String birthGender = AttributeValidation.validateGender(birthGenderComboBox);
            currentUser.setBirthGender(birthGender);



        }
        if (genderIdComboBox.getValue() != null){
            String genderIdentity = AttributeValidation.validateGender(genderIdComboBox);
            currentUser.setGenderIdentity(genderIdentity);

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
        //TODO check why dateofbirth fails
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (!fNameInput.getText().equals("")) {
          currentUser.setFirstName(fNameInput.getText());
        }

        if (!lNameInput.getText().equals("")) {
          currentUser.setLastName(lNameInput.getText());
        }

        if (!nhiInput.getText().equals("")) {
          currentUser.setNHI(nhiInput.getText());
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

}






    /**
     *
     * @param actionEvent
     */
    @FXML
    public void confirmUpdate(ActionEvent actionEvent) throws IOException {
        //TODO save changes and go back to overview screen
        getPersonalDetails();
        getHealthDetails();
        getContactDetaisl();
        getEmergencyContact();
        //TODO change to be different
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
     *
     * @param actionEvent
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
