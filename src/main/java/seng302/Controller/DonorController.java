package seng302.Controller;

import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.joda.time.DateTime;
import seng302.Model.Change;
import seng302.Model.Donor;
import seng302.Model.Organs;
import seng302.Model.UndoRedoStacks;

import java.io.IOException;
import java.time.ZoneId;
import java.util.*;
import seng302.Model.User;

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
  private TableView<Change> historyTableView;

  @FXML
  private Label dodLabel;

  @FXML
  private Label warningLabel;

  @FXML
  private Button logOutButton;

  @FXML
  private ListView<String> previousMedicationListView;

  @FXML
  private ListView<String> currentMedicationListView;

  @FXML
  private Button untakeMedicationButton;

  @FXML
  private Button takeMedicationButton;

  @FXML
  private Button deleteButton;

  @FXML
  private TextField medicationTextField;

  @FXML
  private Button addMedicationButton;

  private AppController application;
  private ObservableList<String> currentMeds;
  private ObservableList<String> previousMeds;

  private List<String> possibleGenders = Arrays.asList("M", "F", "U");

  private List<String> possibleBloodTypes = Arrays
      .asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "U");

  private User currentUser;
  private Stage stage;
  private ObservableList<Change> changelog;

  /**
   * Gives the donor view the application controller and hides all label and buttosns that are not
   * needed on opening
   */
  public void init(AppController controller, User user, Stage stage, Boolean fromClinician) {
    this.stage = stage;
    application = controller;
    ageLabel.setText("");
    if(fromClinician){
      logOutButton.setVisible(false);
    }
    //arbitrary default values
    dateOfBirthPicker.setValue(LocalDate.of(1970, 1, 1));
    dateOfDeathPicker.setValue(LocalDate.now());
    changeDeceasedStatus();
    undoButton.setVisible(true);
    redoButton.setVisible(true);
    ObservableList genders = FXCollections.observableList(possibleGenders);
    genderComboBox.getItems().addAll(genders);
    ObservableList bloodTypes = FXCollections.observableList(possibleBloodTypes);
    bloodTypeComboBox.getItems().addAll(bloodTypes);
    warningLabel.setVisible(false);
    currentUser = user;
    currentMeds = FXCollections.observableArrayList();
    previousMeds = FXCollections.observableArrayList();
    currentMedicationListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    previousMedicationListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    previousMeds.addListener((ListChangeListener.Change<? extends String> change )-> {
      previousMedicationListView.setItems(previousMeds);
      application.update(currentUser);
    });
    currentMeds.addListener((ListChangeListener.Change<? extends String> change) -> {
      currentMedicationListView.setItems(currentMeds);
      application.update(currentUser);
    });
    if (user.getName() != null) {
      showUser(currentUser); // Assumes a donor with no name is a new sign up and does not pull values from a template
      changelog = FXCollections.observableArrayList(currentUser.getChanges());
      showDonorHistory();
    } else {
      changelog = FXCollections.observableArrayList(new ArrayList<Change>());
    }
    currentMedicationListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2){
                String med = currentMedicationListView.getSelectionModel().getSelectedItem();
                lauchMedicationView(med);
            }
        }
    });
    previousMedicationListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent event) {
              if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2){
                  String med = previousMedicationListView.getSelectionModel().getSelectedItem();
                  lauchMedicationView(med);
              }
          }
      });
    changelog.addListener((ListChangeListener.Change<? extends Change> change ) -> {
      historyTableView.setItems(changelog);
    });
  }

  /**
   * fires when the Organs button is clicked
   */
  @FXML
  private void modifyOrgans() {
    if (currentUser.getDateOfBirth() == null) {
      warningLabel.setVisible(true);
      warningLabel.setText("Plese confirm donor before continuing");
      return;
    }
    FXMLLoader organLoader = new FXMLLoader(getClass().getResource("/FXML/organsView.fxml"));
    Parent root = null;
    try {
      root = organLoader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
    OrganController organController = organLoader.getController();
    Stage stage = new Stage();
    organController.init(currentUser, application, stage);
    stage.setScene(new Scene(root));
    stage.show();
    showUser(currentUser);
  }

  @FXML
  private void changeDeceasedStatus() {
    if (!isDonorDeceasedCheckBox.isSelected()) {
      dateOfDeathPicker.setVisible(false);
      dodLabel.setVisible(false);
    } else {
      dodLabel.setVisible(true);
      dateOfDeathPicker.setVisible(true);
    }

  }

  /**
   * fires when the Misc button is clicked
   */
  @FXML
  private void modifyMiscAttributes() {
    if (currentUser.getDateOfBirth() == null) {
      warningLabel.setVisible(true);
      warningLabel.setText("Plese confirm donor before continuing");
      return;
    }
    FXMLLoader attributeLoader = new FXMLLoader(
        getClass().getResource("/FXML/miscAttributes.fxml"));
    Parent root = null;
    try {
      root = attributeLoader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
    MiscAttributesController miscAttributesController = attributeLoader.getController();
    Stage stage = new Stage();
    miscAttributesController.init(currentUser, application, stage);
    stage.setScene(new Scene(root));
    stage.show();
    miscAttributeslistView.getItems().clear();
    miscAttributeslistView.getItems().addAll(currentUser.getMiscAttributes());
  }

  /**
   * fires when the Confirm button is clicked updates the current donor and overwrites or add it to
   * the list of donors in the application Does not deal with organs  and misc attributes as they
   * are confirmed in their own methods
   */
  @FXML
  private void updateDonor() {
      UndoRedoStacks.storeUndoCopy(currentUser);
      User oldDonor = new User();
      UndoRedoStacks.cloneUser(currentUser,oldDonor);


    boolean isInputValid = true;
    warningLabel.setVisible(true);
    warningLabel.setText("");
    if (nameTextField.getText().length() <= 3) {
      warningLabel.setText("Names must be longer than 3 characters");
      return;
    }
    String newName = nameTextField.getText();
    if (newName != null) {
      currentUser.setName(newName);
    } else {
      warningLabel.setText("Please enter a name");
      return;
    }
    Date newDob = Date
        .from(dateOfBirthPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
    currentUser.setDateOfBirth(newDob);

    //only if weight has been entered
    if (!weightTextField.getText().equals("")) {
      try {
        currentUser.setWeight(Double.parseDouble(weightTextField.getText()));
      } catch (NumberFormatException e) {
        warningLabel.setText("Weight must be a number");
        return;
      }
    }
    if (!heightTextField.getText().equals("")) {
      try {
        currentUser.setHeight(Double.parseDouble(heightTextField.getText()));
      } catch (NumberFormatException e) {
        warningLabel.setText("Height must be a number");
        return;
      }
    }

    currentUser.setCurrentAddress(currentAddressTextArea.getText());
    currentUser.setRegion(regionTextField.getText());

    String newGender = (genderComboBox.getValue());
    if (newGender == null) {
      newGender = "U";
    }
    currentUser.setGender(newGender);
    currentUser.setBloodType(bloodTypeComboBox.getValue());
    currentUser.setDeceased(isDonorDeceasedCheckBox.isSelected());
    if (isDonorDeceasedCheckBox.isSelected()) {
      Date newDod =  Date.from(dateOfDeathPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
      if (newDod.before(newDob)) {
        warningLabel.setVisible(true);
        warningLabel.setText("Date of death must be after date of birth");
        //dod must be set for other functions to work.
        //using the best guess based on input
        currentUser.setDateOfDeath(newDob);
        return;
      }
      currentUser.setDateOfDeath(newDod);
    } else {
      currentUser.setDateOfDeath(null);
    }

    if (isInputValid) {
      application.update(currentUser);
      ArrayList<String> diffs = application.differanceInDonors(oldDonor, currentUser);
      for(String diff : diffs){
        Change c = new Change(DateTime.now(),diff);
        changelog.add(c);
      }
    }

    showUser(currentUser);
  }

  /**
   * fires when the Undo button is clicked
   */
  @FXML
  private void undo() {
    currentUser = UndoRedoStacks.loadUndoCopy(currentUser);
    //System.out.println("Something happened");
    //System.out.println(currentUser.getName());
    showUser(currentUser); //Error with showing donors


  }

  /**
   * fires when the Redo button is clicked
   */
  @FXML
  private void redo() {
    currentUser = UndoRedoStacks.loadRedoCopy(currentUser);
    //System.out.println("Something happened");
    //System.out.println(currentUser.getName());
    showUser(currentUser);
  }

  @FXML
  private void logout() {
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

    UndoRedoStacks.clearStacks();

  }

  public void showUser(User user) {
    nameTextField.setText(user.getName());
    dateOfBirthPicker
        .setValue(user.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    genderComboBox.getSelectionModel().select(user.getGender());
    heightTextField.setText(Double.toString(user.getHeight()));
    weightTextField.setText(Double.toString(user.getWeight()));
    currentAddressTextArea.setText(user.getCurrentAddress());
    regionTextField.setText(user.getRegion());
    if (user.getDonorDetails().getOrgans() != null) {
      organsDonatingListView.getItems().clear();
      organsDonatingListView.getItems().addAll(user.getDonorDetails().getOrgans());
    }
    bloodTypeComboBox.getSelectionModel().select(user.getBloodType());
    if (!currentUser.getDeceased()) {
      dateOfDeathPicker.setVisible(false);
      dodLabel.setVisible(false);
    } else {
      isDonorDeceasedCheckBox.setSelected(true);
      dodLabel.setVisible(true);
      dateOfDeathPicker.setVisible(true);
      dateOfDeathPicker.setValue(
          user.getDateOfDeath().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }
    ageLabel.setText(user.getAge().toString().replace("P", "").replace("Y", "") + " Years");
    if (user.getMiscAttributes() != null) {
      miscAttributeslistView.getItems().clear(); // HERE
      for (String atty : user.getMiscAttributes()) {
        miscAttributeslistView.getItems().add(atty);
      }
    }
    currentMeds.addAll(currentUser.getCurrentMedication());
    currentMedicationListView.setItems(currentMeds);
    previousMeds.addAll(currentUser.getPreviousMedication());
    previousMedicationListView.setItems(previousMeds);
  }

  @FXML
  void addMedication(ActionEvent event) {
    String medication = medicationTextField.getText();
    if (medication.isEmpty() || medication == null){
      return;
    }
    if (currentMeds.contains(medication) || previousMeds.contains(medication)){
        medicationTextField.setText("");
        return;
    }
    medicationTextField.setText("");
    currentMeds.add(medication);
    currentUser.addCurrentMedication(medication);


  }

  @FXML
  void deleteMedication(ActionEvent event) {
    String medCurrent  = currentMedicationListView.getSelectionModel().getSelectedItem();
    String medPrevious = previousMedicationListView.getSelectionModel().getSelectedItem();

    if(medCurrent != null){
      currentMeds.remove(medCurrent);
      currentUser.removeCurrentMedication(medCurrent);
    }
    if (medPrevious != null){
      previousMeds.remove(medPrevious);
      currentUser.removePreviousMedication(medPrevious);
    }
  }

  @FXML
  void takeMedication(ActionEvent event) {
    String med = previousMedicationListView.getSelectionModel().getSelectedItem();
    if (med == null){
        return;
    }
    if (currentMeds.contains(med)){
        currentUser.removePreviousMedication(med);
        previousMeds.remove(med);
        return;
    }
      currentMeds.add(med);
      currentUser.addCurrentMedication(med);
      previousMeds.remove(med);
      currentUser.removePreviousMedication(med);

  }

  @FXML
  void untakeMedication(ActionEvent event) {
    String med = currentMedicationListView.getSelectionModel().getSelectedItem();
      if (med == null){
          return;
      }
      if(previousMeds.contains(med)) {
          currentUser.removeCurrentMedication(med);
          currentMeds.remove(med);
          return;
      }
    currentUser.removeCurrentMedication(med);
    currentMeds.remove(med);
    previousMeds.add(med);
    currentUser.addPreviousMedication(med);
  }

  @FXML
  void clearCurrentMedSelection(MouseEvent event) {
    currentMedicationListView.getSelectionModel().clearSelection();
  }

  @FXML
  void clearPreviousMedSelection(MouseEvent event){
    previousMedicationListView.getSelectionModel().clearSelection();
  }

  private void lauchMedicationView(String med){
      FXMLLoader medicationTimeViewLoader = new FXMLLoader(getClass().getResource("/FXML/medicationsTimeView.fxml"));
      Parent root = null;
      try {
          root = medicationTimeViewLoader.load();
      } catch (IOException e) {
          e.printStackTrace();
      }
      Stage stage = new Stage();
      stage.setScene(new Scene(root));
      MedicationsTimeController medicationsTimeController = medicationTimeViewLoader.getController();
      medicationsTimeController.init(application, currentUser,stage, med);
      stage.show();




  }


    private void showDonorHistory() {
      TableColumn timeColumn = new TableColumn("Time");
        TableColumn changeColumn = new TableColumn("Change");
        timeColumn.setCellValueFactory(new PropertyValueFactory<Change, String>("time"));
        changeColumn.setCellValueFactory(new PropertyValueFactory<Change, String>("change"));
        historyTableView.setItems(changelog);
        historyTableView.getColumns().addAll(timeColumn,changeColumn);

    }
}
