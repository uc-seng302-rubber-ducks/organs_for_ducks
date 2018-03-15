package seng302.Controller;

import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import seng302.Model.Donor;
import seng302.Model.Organs;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DonorController {


  @FXML
  private Label ageLabel;

  @FXML
  private DatePicker dateOfBirthPicker;

  @FXML
  private DatePicker dateOfDeathPicker;

  @FXML
  private CheckBox isDonorDeceasedCheckBox;

  @FXML
  private TextField nameTextField;

  @FXML
  private TextField heightTextField;

  @FXML
  private TextField weightTextField;

  @FXML
  private ComboBox<String> genderComboBox;

  @FXML
  private ComboBox<String> bloodTypeComboBox;

  @FXML
  private TextField regionTextField;

  @FXML
  private ListView<Organs> organsDonatingListView;

  @FXML
  private Button undoButton;

  @FXML
  private Button redoButton;

  @FXML
  private TextArea currentAddressTextArea;

  @FXML
  private ListView<String> miscAttributeslistView;

  @FXML
  private TableView<?> historyTableView;

  @FXML
  private Label dodLabel;

  @FXML
  private Label warningLabel;

  @FXML
  private Button logoutButton;

  private AppController application;

  private List<String> possibleGenders = Arrays.asList("M","F","U");

  private List<String> possibleBloodTypes = Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "U");

  private Donor currentDonor;
  private Stage stage;



  /**
   * Gives the donor view the application controller and hides all label and buttosns that are not needed on opening
   * @param controller
   */
  public void init(AppController controller, Donor donor, Stage stage){
      this.stage= stage;
    application = controller;
    ageLabel.setText("");
    //arbitrary default values
    dateOfBirthPicker.setValue(LocalDate.of(1970,1,1));
    dateOfDeathPicker.setValue(LocalDate.now());
    changeDeceasedStatus();
    undoButton.setVisible(false);
    redoButton.setVisible(false);
    ObservableList genders = FXCollections.observableList(possibleGenders);
    genderComboBox.getItems().addAll(genders);
    ObservableList bloodTypes = FXCollections.observableList(possibleBloodTypes);
    bloodTypeComboBox.getItems().addAll(bloodTypes);
    warningLabel.setVisible(false);
    currentDonor = donor;
    if(donor.getName() != null) {
        showDonor(currentDonor); // Assumes a donor with no name is a new sign up and does not pull values from a template
    }
  }
  /**
   * fires when the Organs button is clicked
   */
  @FXML
  private void modifyOrgans() {
      FXMLLoader organLoader = new FXMLLoader(getClass().getResource("/FXML/organsView.fxml"));
      Parent root = null;
      try {
          root = organLoader.load();
      } catch (IOException e) {
          e.printStackTrace();
      }
      OrganController organController = organLoader.getController();
      Stage stage = new Stage ();
      organController.init(currentDonor, application,stage);
      stage.setScene(new Scene(root));
      stage.show();

  }

  @FXML
  private void changeDeceasedStatus(){
      if(!isDonorDeceasedCheckBox.isSelected()) {
          dateOfDeathPicker.setVisible(false);
          dodLabel.setVisible(false);
      } else {
          dodLabel.setVisible(true);
          dateOfDeathPicker.setVisible(true);
      }

  }

  /**
   * fires when the Misc button is clicked
   *
   */
  @FXML
  private void modifyMiscAttributes() {
      FXMLLoader attributeLoader = new FXMLLoader(getClass().getResource("/FXML/miscAttributes.fxml"));
      Parent root = null;
      try {
          root = attributeLoader.load();
      } catch (IOException e) {
          e.printStackTrace();
      }
      MiscAttributesController miscAttributesController = attributeLoader.getController();
      Stage stage = new Stage();
      miscAttributesController.init(currentDonor, application, stage);
      stage.setScene(new Scene(root));
      stage.show();
      miscAttributeslistView.getItems().clear();
      miscAttributeslistView.getItems().addAll(currentDonor.getMiscAttributes());
  }

  /**
   * fires when the Confirm button is clicked
   * updates the current donor and overwrites or add it to the list of donors in the application
   * Does not deal with organs  and misc attributes as they are confirmed in their own methods
   */
  @FXML
  private void updateDonor() {
    boolean isInputValid = true;
    warningLabel.setVisible(true);
      warningLabel.setText("");
      if(nameTextField.getText().length() <= 3){
          warningLabel.setText("Names must be longer than 3 characters");
          return;
      }
      String newName = nameTextField.getText();
      if (newName != null) {
        currentDonor.setName(newName);
      } else {
        warningLabel.setText("Please enter a name");
        return;
      }
      Date newDob = Date.from(dateOfBirthPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
      currentDonor.setDateOfBirth(newDob);

      //only if weight has been entered
      if (!weightTextField.getText().equals("")) {
        try {
          currentDonor.setWeight(Double.parseDouble(weightTextField.getText()));
        } catch (NumberFormatException e) {
          warningLabel.setText("Weight must be a number");
          return;
        }
      }
      if (!heightTextField.getText().equals("")) {
        try {
          currentDonor.setHeight(Double.parseDouble(heightTextField.getText()));
        } catch (NumberFormatException e) {
          warningLabel.setText("Height must be a number");
          return;
        }
      }

      currentDonor.setCurrentAddress(currentAddressTextArea.getText());
      currentDonor.setRegion(regionTextField.getText());

      String newGender = (genderComboBox.getValue());
      if (newGender == null) {
        newGender = "U";
      }
      currentDonor.setGender(newGender);
      currentDonor.setBloodType(bloodTypeComboBox.getValue());
      currentDonor.setDeceased(isDonorDeceasedCheckBox.isSelected());
      if(isDonorDeceasedCheckBox.isSelected()){
          currentDonor.setDateOfDeath(Date.from(dateOfDeathPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
      } else {
          currentDonor.setDateOfDeath(null);
      }

      if (isInputValid) {
        application.update(currentDonor);
      }

  }

  /**
   * fires when the Undo button is clicked
   */
  @FXML
  private void undo() {

  }

  /**
   * fires when the Redo button is clicked
   */
  @FXML
  private void redo() {

  }

  @FXML
  private void logout(){
      updateDonor();
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/loginView.fxml"));
      Parent root = null;
      try {
          root = loader.load();
      } catch (IOException e) {
          e.printStackTrace();
      }
      LoginController loginController = loader.getController();
      loginController.init(AppController.getInstance(), stage);
      stage.setScene(new Scene(root));
      stage.show();

  }

  private void showDonor(Donor donor){
    nameTextField.setText(donor.getName());
    dateOfBirthPicker.setValue(donor.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    genderComboBox.getSelectionModel().select(donor.getGender());
    heightTextField.setText(Double.toString(donor.getHeight()));
    weightTextField.setText(Double.toString(donor.getWeight()));
    currentAddressTextArea.setText(donor.getCurrentAddress());
    regionTextField.setText(donor.getRegion());
      if (donor.getOrgans() != null) {
          organsDonatingListView.getItems().addAll(donor.getOrgans());
      }
      bloodTypeComboBox.getSelectionModel().select(donor.getBloodType());
      if (!currentDonor.getDeceased()){
          dateOfDeathPicker.setVisible(false);
          dodLabel.setVisible(false);
      } else {
          isDonorDeceasedCheckBox.setSelected(true);
          dateOfDeathPicker.setValue(donor.getDateOfDeath().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
      }
      if(donor.getMiscAttributes() != null){
          for(String atty : donor.getMiscAttributes()) {
              miscAttributeslistView.getItems().add(atty);
          }
      }
  }
}
