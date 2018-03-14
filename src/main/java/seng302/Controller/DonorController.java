package seng302.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import seng302.Model.Donor;
import seng302.Model.Organs;

import java.time.ZoneId;
import java.util.Arrays;
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
  private ListView<?> miscAttributeslistView;

  @FXML
  private TableView<?> historyTableView;

  @FXML
  private Label dodLabel;

  private AppController application;

  private List<String> possibleGenders = Arrays.asList("M","F","U");

  private List<String> possibleBloodTypes = Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "U");

  private Donor currentDonor;


  /**
   * Gives the donor view the application controller and hides all label and buttosns that are not needed on opening
   * @param controller
   */
  public void init(AppController controller){
    application = controller;
    ageLabel.setVisible(false);
    dateOfDeathPicker.setVisible(false);
    dodLabel.setVisible(false);
    undoButton.setVisible(false);
    redoButton.setVisible(false);
    ObservableList genders = FXCollections.observableList(possibleGenders);
    genderComboBox.getItems().addAll(genders);
    ObservableList bloodTypes = FXCollections.observableList(possibleBloodTypes);
    bloodTypeComboBox.getItems().addAll(bloodTypes);

    currentDonor = application.getDonors().get(0); //TODO: add code here to get donor that is being refered to on login
    showDonor(currentDonor);
  }
  /**
   * fires when the Organs button is clicked
   */
  @FXML
  private void modifyOrgans() {

  }

  @FXML
  private void changeDeceasedStatus(){
    if(isDonorDeceasedCheckBox.isSelected()){
      dodLabel.setVisible(true);
      dateOfDeathPicker.setVisible(true);
    } else if(!isDonorDeceasedCheckBox.isSelected()){
      dodLabel.setVisible(false);
      dateOfDeathPicker.setVisible(false);
      dateOfDeathPicker.getEditor().clear();
    }
  }

  /**
   * fires when the Misc button is clicked
   */
  @FXML
  private void modifyMiscAttributes() {

  }

  /**
   * fires when the Confirm button is clicked
   */
  @FXML
  private void updateDonor() {

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

  private void showDonor(Donor donor){
    nameTextField.setText(donor.getName());
    dateOfBirthPicker.setValue(donor.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    genderComboBox.getSelectionModel().select(donor.getGender());
    heightTextField.setText(Double.toString(donor.getHeight()));
    weightTextField.setText(Double.toString(donor.getWeight()));
    currentAddressTextArea.setText(donor.getCurrentAddress());
    regionTextField.setText(donor.getRegion());
    organsDonatingListView.getItems().addAll(donor.getOrgans());
    bloodTypeComboBox.getSelectionModel().select(donor.getBloodType());
  }
}
