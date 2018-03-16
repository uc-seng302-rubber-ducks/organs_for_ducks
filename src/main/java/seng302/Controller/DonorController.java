package seng302.Controller;

import java.time.LocalDate;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.joda.time.DateTime;
import seng302.Model.Donor;
import seng302.Model.Organs;

import java.io.IOException;
import java.time.ZoneId;
import java.util.*;

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

  @FXML
  private Label warningLabel;

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

    currentDonor = new Donor(); //TODO: add code here to get donor that is being refered to on login
    //showDonor(currentDonor);
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
   * #TODO: add view to modify these, should be super basic
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
  }

  /**
   * fires when the Confirm button is clicked
   * updates the current donor and overwrites or add it to the list of donors in the application
   * Does not deal with organs  and misc attributes as they are confirmed in their own methods
   */
  @FXML
  private void updateDonor() {
    boolean isInputValid = true;
      warningLabel.setText("");
      if(nameTextField.getText().length() <= 3){
          warningLabel.setText("Names must be longer than 3 characters");
          return;
      }
      String newName = nameTextField.getText();
      if (newName != null) {
        currentDonor.setName(newName);
      } else {
        isInputValid = false;
        warningLabel.setText("Please enter a name");
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
      if (!currentDonor.getDeceased()){
          dateOfDeathPicker.setVisible(false);
          dodLabel.setVisible(false);
      } else {
          isDonorDeceasedCheckBox.setSelected(true);
          dateOfDeathPicker.setValue(donor.getDateOfDeath().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
      }
  }

    private void showDonorHistory(Donor donor) {
        //historyTableView
        //use JsonReader and JsonWriter
        //change log history - look at

        // historyTableView.add(?)(donor.getLastUpdated, donor.getlastactionorwe)

        TableColumn<Map.Entry<String, String>, String> dateCol = new TableColumn<>("Time");
        dateCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, String>, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, String>, String> p) {
             return new SimpleStringProperty(p.getValue().getKey());
            }
        });

        TableColumn<Map.Entry<String, String>, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String, String>, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String, String>, String> p) {
                return new SimpleStringProperty(p.getValue().getValue());
            }
        });

        ObservableList<Map.Entry<String, String>> items = FXCollections.observableArrayList(donor.getUpdateHistory().entrySet());
        // @TODO: fix this so it isn't grumpy
        historyTableView.setItems(items);

        historyTableView.getColumns().setAll(dateCol, actionCol);

    }
}
