package seng302.Controller;


import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import seng302.Model.Change;
import seng302.Model.Disease;
import seng302.Model.EmergencyContact;
import seng302.Model.MedicalProcedure;
import seng302.Model.Memento;
import seng302.Model.OrganDeregisterReason;
import seng302.Model.Organs;
import seng302.Model.User;
import seng302.Service.OrganListCellFactory;

/**
 * Class for the functionality of the User view of the application
 */
public class DonorController {

// the contact page attributes

  //declaring all variables for the contacts page
  @FXML
  private Label pCellPhone;
  @FXML
  private Label pHomePhone;
  @FXML
  private Label pAddress;
  @FXML
  private Label pRegion;
  @FXML
  private Label pEmail;
  @FXML
  private Label eCellPhone;
  @FXML
  private Label eHomePhone;
  @FXML
  private Label eAddress;
  @FXML
  private Label eRegion;
  @FXML
  private Label eEmail;
  @FXML
  private Label relationship;
  @FXML
  private Label eName;

  //@FXML
  //private ListView<Organs> organsDonatingListView;

  @FXML
  private Button undoButton;

  @FXML
  private Button redoButton;

  //@FXML
  //private ListView<String> miscAttributeslistView;

  @FXML
  private TableView<Change> historyTableView;



  @FXML
  private TableView<Disease> currentDiseaseTableView;

  @FXML
  private TableView<Disease> pastDiseaseTableView;

  @FXML
  private Button addDiseaseButton;

  @FXML
  private Button updateDiseaseButton;

  @FXML
  private Button deleteDiseaseButton;


  //procedures

  @FXML
  private Button removeProcedureButton;

  @FXML
  private Button addProcedureButton;

  @FXML
  private Button updateProceduresButton;

  @FXML
  private Button modifyOrgansProcedureButton;

  @FXML
  private Button clearProcedureButton;

  @FXML
  private DatePicker procedureDateSelector;

  @FXML
  private TextField procedureTextField;

  @FXML
  private Label procedureWarningLabel;

  @FXML
  private TableView<MedicalProcedure> previousProcedureTableView;

  @FXML
  private TableView<MedicalProcedure> pendingProcedureTableView;

  @FXML
  private ListView<Organs> organsAffectedByProcedureListView;

  @FXML
  private TextArea descriptionTextArea;

  private TableView<MedicalProcedure> currentProcedureList;
  @FXML
  private ListView<Organs> currentlyDonating;

  @FXML
  private ListView<Organs> canDonate;

  @FXML
  private Button donateButton;

  @FXML
  private Button undonateButton;

  @FXML
  private Label donorNameLabel;
  //Receiver

  @FXML
  private ComboBox<Organs> organsComboBox;

  @FXML
  private Label organLabel;

  @FXML
  private ListView<Organs> currentlyReceivingListView;

  @FXML
  private ListView<Organs> notReceivingListView;

  @FXML
  private Label currentlyReceivingLabel;

  @FXML
  private Label notReceivingLabel;

  @FXML
  private Label notReceiverLabel;

  @FXML
  private Button registerButton;

  @FXML
  private Button reRegisterButton;

  @FXML
  private Button deRegisterButton;

  private AppController application;

  @FXML
  private DonorOverviewController userProfileTabPageController;

  @FXML
  private MedicationTabController medicationTabPageController;




  private ObservableList<MedicalProcedure> medicalProcedures;
  private ObservableList<MedicalProcedure> previousProcedures;
  private ObservableList<MedicalProcedure> pendingProcedures;
  private HashMap<Organs, ArrayList<LocalDate>> receiverOrgans = new HashMap<>();
  private ObservableList<Organs> currentlyRecieving;
  private ObservableList<Organs> noLongerReceiving;


  private ObservableList<Disease> currentDisease;
  private ObservableList<Disease> pastDisease;
  private List<String> possibleGenders = Arrays.asList("M", "F", "U");

  private List<String> possibleBloodTypes = Arrays
          .asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "U");

  private User currentUser;
  private Stage stage;
  private EmergencyContact contact = null;
  private ObservableList<Change> changelog;

  private Boolean Clinician;
  private boolean isSortedByName = false;
  private boolean isReverseSorted = false;

  private OrganDeregisterReason organDeregisterationReason;

  /**
   * Gives the donor view the application controller and hides all label and buttons that are not
   * needed on opening
   *
   * @param controller    the application controller
   * @param user          the current user
   * @param stage         the application stage
   * @param fromClinician boolean value indication if from clinician view
   */
  public void init(AppController controller, User user, Stage stage, boolean fromClinician) {
    this.stage = stage;
    application = controller;
    //ageValue.setText("");
    //This is the place to set visable and invisable controls for Clinician vs User
    medicationTabPageController.init(controller, user, stage, fromClinician);
    if (fromClinician) {
      Clinician = true;
      addDiseaseButton.setVisible(true);
      updateDiseaseButton.setVisible(true);
      deleteDiseaseButton.setVisible(true);
    } else {
      Clinician = false;
      procedureDateSelector.setEditable(false);
      procedureTextField.setEditable(false);
      descriptionTextArea.setEditable(false);
      addProcedureButton.setVisible(false);
      removeProcedureButton.setVisible(false);
      updateProceduresButton.setVisible(false);
      modifyOrgansProcedureButton.setVisible(false);

      organLabel.setVisible(false);
      organsComboBox.setVisible(false);
      registerButton.setVisible(false);
      reRegisterButton.setVisible(false);
      deRegisterButton.setVisible(false);
    }
    //arbitrary default values
    //changeDeceasedStatus();
    undoButton.setVisible(true);
    redoButton.setVisible(true);
    //warningLabel.setVisible(false);
    changeCurrentUser(user);

    populateOrganLists(user);

    // Sets the button to be disabled
    updateUndoRedoButtons();

    procedureWarningLabel.setText("");
    procedureDateSelector.setValue(LocalDate.now());
    previousProcedures = FXCollections.observableArrayList();
    pendingProcedures = FXCollections.observableArrayList();
    pendingProcedureTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    previousProcedureTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    moveSelectedProcedureTo(previousProcedureTableView, pendingProcedureTableView);
    moveSelectedProcedureTo(pendingProcedureTableView, previousProcedureTableView);
    //showUser(currentUser);

    TableColumn pendingProcedureColumn = new TableColumn("Procedure");
    TableColumn pendingDateColumn = new TableColumn("Date");
    TableColumn previousProcedureColumn = new TableColumn("Procedure");
    TableColumn previousDateColumn = new TableColumn("Date");
    pendingProcedureColumn
            .setCellValueFactory(new PropertyValueFactory<MedicalProcedure, String>("summary"));
    previousProcedureColumn
            .setCellValueFactory(new PropertyValueFactory<MedicalProcedure, String>("summary"));
    pendingDateColumn
            .setCellValueFactory(new PropertyValueFactory<Change, String>("procedureDate"));
    previousDateColumn
            .setCellValueFactory(new PropertyValueFactory<Change, String>("procedureDate"));
    previousProcedureTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    pendingProcedureTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    previousProcedureTableView.getColumns().addAll(previousProcedureColumn, previousDateColumn);
    pendingProcedureTableView.getColumns().addAll(pendingProcedureColumn, pendingDateColumn);
    organsAffectedByProcedureListView.setCellFactory(oabp -> {
      TextFieldListCell<Organs> cell = new TextFieldListCell<>();
      cell.setConverter(new StringConverter<Organs>() {
        @Override
        public String toString(Organs object) {
          return object.toString();
        }

        @Override
        public Organs fromString(String string) {
          return null;
        }
      });
      return cell;
    });
    if (user.getNhi() != null) {
      showUser(
              currentUser); // Assumes a donor with no name is a new sign up and does not pull values from a template
      List<Change> changes = currentUser.getChanges();
      if (changes != null) {
        changelog = FXCollections.observableList(changes);
      } else {
        changelog = FXCollections.observableArrayList(new ArrayList<Change>());
      }
    } else {
      changelog = FXCollections.observableArrayList(new ArrayList<Change>());
    }
    showDonorHistory();
    changelog.addListener((ListChangeListener.Change<? extends Change> change) -> historyTableView
            .setItems(changelog));


    showDonorDiseases(currentUser, true);
    modifyOrgansProcedureButton.setVisible(false);

    currentDiseaseTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    pastDiseaseTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    currentDiseaseTableView.getSelectionModel().selectedItemProperty()
            .addListener(ListChangeListener -> pastDiseaseTableView.getSelectionModel().select(null));
    pastDiseaseTableView.getSelectionModel().selectedItemProperty().addListener(
            ListChangeListener -> currentDiseaseTableView.getSelectionModel().select(null));

    //init receiver organs combo box

    //display registered and deregistered receiver organs if any
    populateReceiverLists(currentUser);
    currentlyDonating.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    currentlyReceivingListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    currentlyDonating.setCellFactory(column -> OrganListCellFactory.generateListCell(currentUser));

    currentlyReceivingListView
        .setCellFactory(column -> OrganListCellFactory.generateListCell(currentUser));

    stage.onCloseRequestProperty().setValue(event -> {
      if (fromClinician) {
        application.getClinicianController().refreshTables();
      }
    });
    userProfileTabPageController.init(controller, user, this.stage, fromClinician);
  }

  /**
   * Populates the receiver list of the user
   */
  private void populateReceiverLists(User user) {
    ArrayList<Organs> organs = new ArrayList<>();
    Collections.addAll(organs, Organs.values());
    Map<Organs, ArrayList<LocalDate>> receiverOrgans = user.getReceiverDetails().getOrgans();
    if (receiverOrgans == null) {
      receiverOrgans = new EnumMap<>(Organs.class);
    }
    currentlyRecieving = FXCollections.observableArrayList();
    noLongerReceiving = FXCollections.observableArrayList();
    if (!receiverOrgans.isEmpty()) {
      for (Organs organ : receiverOrgans.keySet()) {
        if (user.getReceiverDetails().isCurrentlyWaitingFor(organ)) {
          organs.remove(organ);
          currentlyRecieving.add(organ);
        } else {
          organs.remove(organ);
          noLongerReceiving.add(organ);
        }
      }
    } else if (!Clinician) { //if user is not a receiver and not login as clinician
      currentlyReceivingLabel.setVisible(false);
      notReceivingLabel.setVisible(false);
      currentlyReceivingListView.setVisible(false);
      notReceivingListView.setVisible(false);
      notReceiverLabel.setVisible(true);
    }

    currentlyReceivingListView.setItems(currentlyRecieving);
    notReceivingListView.setItems(noLongerReceiving);
    organsComboBox.setItems(FXCollections.observableList(organs));

    if (!notReceivingListView.getItems().isEmpty()) {
      openOrganFromDoubleClick(notReceivingListView);
    }

    if (!currentlyReceivingListView.getItems().isEmpty()) {
      openOrganFromDoubleClick(currentlyReceivingListView);
    }

    stage.onCloseRequestProperty().setValue(event -> {
      if (Clinician) {
        AppController.getInstance().getClinicianController().refreshTables();
      }
    });

    //if user already died, user cannot receive organs
    if (currentUser.getDeceased())

    {//TODO add listener so that if user is updated to not be diseased, these buttons will activate
      registerButton.setDisable(true);
      reRegisterButton.setDisable(true);
    }
  }
  /**
   * Popoulates the organ lists of the user
   *
   * @param user user to use to populate
   */
  private void populateOrganLists(User user) {
    ArrayList<Organs> donating;
    try {
      donating = new ArrayList<>(user.getDonorDetails().getOrgans());
    } catch (NullPointerException ex) {
      donating = new ArrayList<>();
    }
    currentlyDonating.setItems(FXCollections.observableList(donating));
    ArrayList<Organs> leftOverOrgans = new ArrayList<Organs>();
    Collections.addAll(leftOverOrgans, Organs.values());
    for (Organs o : donating) {
      leftOverOrgans.remove(o);
    }
    canDonate.setItems(FXCollections.observableList(leftOverOrgans));
  }

  /**
   * A method to add a listener to the from TableView to unselect from one list and show procedure from the appropriate list
   * @param from a TableView object holding medical procedures
   * @param to a TableView object to deselect from
   */
  private void moveSelectedProcedureTo(TableView<MedicalProcedure> from,
      TableView<MedicalProcedure> to) {
    from.getSelectionModel().selectedItemProperty()
        .addListener(ListChangeListener -> {
          to.getSelectionModel().select(null);
          if (from.getSelectionModel().getSelectedItem() != null) {
            showProcedure(from.getSelectionModel().getSelectedItem());
            modifyOrgansProcedureButton.setVisible(true);
            currentProcedureList = from;
          }
        });
  }

  /**
   * Opens the selected organ from a doubleClick event
   *
   * @param list A ListView object to add the
   */
  private void openOrganFromDoubleClick(ListView<Organs> list) {
    list.setOnMouseClicked(event -> {
      if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
        Organs currentlyReceivingOrgan = list.getSelectionModel().getSelectedItem();
        launchReceiverOrganDateView(currentlyReceivingOrgan);
      }
    });
  }

  /**
   * @return The reason for an organ deRegistration
   */
  public OrganDeregisterReason getOrganDeregisterationReason() {
    return organDeregisterationReason;
  }

  /**
   * Sets the reason for organ deregistration
   * @param organDeregisterationReason OrganDeregisterReason enum
   */
  public void setOrganDeregisterationReason(OrganDeregisterReason organDeregisterationReason) {
    this.organDeregisterationReason = organDeregisterationReason;
  }

  /**
   * Changes the currentUser to the provided user
   * @param user user to change currentUser to
   */
  private void changeCurrentUser(User user) {
    currentUser = user;
    contact = user.getContact();
    if (user.getChanges() != null) {
      changelog = FXCollections.observableArrayList(user.getChanges());
    } else {
      changelog = FXCollections.observableArrayList(new ArrayList<Change>());
    }
  }


  /**
   * Sets the users contact information on the contact tab of the user profile
   */
  @FXML
  private void setContactPage() {
    if (contact != null) {
      eName.setText(contact.getName());
      eCellPhone.setText(contact.getCellPhoneNumber());
      if (contact.getAddress() != null) {
        eAddress.setText(contact.getAddress());
      } else {
        eAddress.setText("");
      }

      if (contact.getEmail() != null) {
        eEmail.setText(contact.getEmail());
      } else {
        eEmail.setText("");
      }

      if (contact.getHomePhoneNumber() != null) {
        eHomePhone.setText(contact.getHomePhoneNumber());
      } else {
        eHomePhone.setText("");
      }

      if (contact.getRegion() != null) {
        eRegion.setText(contact.getRegion());
      } else {
        eRegion.setText("");
      }

      if (contact.getRelationship() != null) {
        relationship.setText(contact.getRelationship());
      } else {
        relationship.setText("");
      }
    }
    if (currentUser.getCurrentAddress() != null) {
      pAddress.setText(currentUser.getCurrentAddress());
    } else {
      pAddress.setText("");
    }
    if (currentUser.getRegion() != null) {
      pRegion.setText(currentUser.getRegion());
    } else {
      pRegion.setText("");
    }
    if (currentUser.getEmail() != null) {
      pEmail.setText(currentUser.getEmail());
    } else {
      pEmail.setText("");
    }
    if (currentUser.getHomePhone() != null) {
      pHomePhone.setText(currentUser.getHomePhone());
    } else {
      pHomePhone.setText("");
    }
    if (currentUser.getCellPhone() != null) {
      pCellPhone.setText(currentUser.getCellPhone());
    } else {
      pCellPhone.setText("");
    }


  }

  /**
   * fires when the Undo button is clicked
   */
  @FXML
  private void undo() {
    currentUser.undo();
    updateUndoRedoButtons();
    showUser(currentUser); //Error with showing donors

  }


  /**
   * fires when the Redo button is clicked
   */
  @FXML
  private void redo() {
    currentUser.redo();
    updateUndoRedoButtons();
    showUser(currentUser);
  }

  public void showUser(User user) {
    changeCurrentUser(user);
    userProfileTabPageController.showUser(user);
    setContactPage();
    medicationTabPageController.refreshLists(user);
    populateOrganLists(user);
    populateReceiverLists(user);

    updateProcedureTables(user);
    if (user.getLastName() != null) {
      stage.setTitle("User Profile: " + user.getFirstName() + " " + user.getLastName());
    } else {
      stage.setTitle("User Profile: " + user.getFirstName());

    }
    updateUndoRedoButtons();
  }

  /**
   * Updates the procedure tables and ensure that the selected item is not changed.
   */
  private void updateProcedureTables(User user) {
    boolean pendingProceduresTableSelected = (
        pendingProcedureTableView.getSelectionModel().getSelectedItem() != null);
    int index = pendingProceduresTableSelected ? pendingProcedureTableView.getSelectionModel()
        .getSelectedIndex() : previousProcedureTableView.getSelectionModel().getSelectedIndex();
    medicationTabPageController.clearPreviousMeds();
    pendingProcedures.clear();
    medicalProcedures = FXCollections.observableList(user.getMedicalProcedures());
    for (MedicalProcedure procedure : medicalProcedures) {
      if (procedure.getProcedureDate().isBefore(LocalDate.now())) {
        previousProcedures.add(procedure);
      } else {
        pendingProcedures.add(procedure);
      }
    }
    historyTableView.refresh();

    previousProcedureTableView.setItems(previousProcedures);
    pendingProcedureTableView.setItems(pendingProcedures);
    if (pendingProceduresTableSelected) {
      try {
        pendingProcedureTableView.getSelectionModel().select(index);
      } catch (IndexOutOfBoundsException e) {
        previousProcedureTableView.getSelectionModel().select(previousProcedures.size() - 1);
      }
    } else {
      try {
        previousProcedureTableView.getSelectionModel().select(index);
      } catch (IndexOutOfBoundsException e) {
        pendingProcedureTableView.getSelectionModel().select(pendingProcedures.size() - 1);
      }
    }
    previousProcedureTableView.refresh();
    pendingProcedureTableView.refresh();
  }

  /**
   * Shows the history of the Users profile such as added and removed information
   */
  private void showDonorHistory() {
    TableColumn<Change, String> timeColumn = new TableColumn<>("Time");
    TableColumn<Change, String> changeColumn = new TableColumn<Change, String>("Change");
    timeColumn.setCellValueFactory(new PropertyValueFactory<Change, String>("time"));
    changeColumn.setCellValueFactory(new PropertyValueFactory<Change, String>("change"));
    historyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    historyTableView.setItems(changelog);
    historyTableView.getColumns().addAll(timeColumn, changeColumn);

  }

  /**
   * Adds a procedure to the current user when a procedure name is entered
   *
   * @param event An action event.
   */
  @FXML
  void addProcedure(ActionEvent event) {
    Memento<User> memento = new Memento<>();
    memento.setOldObject(currentUser.clone());
    String procedureName = procedureTextField.getText();
    if (procedureName.isEmpty()) {
      procedureWarningLabel.setText("A name must be entered for a procedure");
      return;
    }
    LocalDate procedureDate = procedureDateSelector.getValue();
    if (procedureDate == null) {
      procedureWarningLabel.setText("A valid date must be entered for a procedure");
      return;
    }
    if (procedureDate.isBefore(currentUser.getDateOfBirth())) {
      procedureWarningLabel.setText("Procedures may not occur before a patient has been born");
      return;
    }
    MedicalProcedure procedure = new MedicalProcedure(procedureDate, procedureName,
        descriptionTextArea.getText(), new ArrayList<Organs>());
    medicalProcedures.add(procedure);
    if (procedure.getProcedureDate().isBefore(LocalDate.now())) {
      previousProcedures.add(procedure);
    } else {
      pendingProcedures.add(procedure);
    }
    clearProcedure();
    application.update(currentUser);
    memento.setNewObject(currentUser.clone());
    currentUser.getUndoStack().push(memento);
  }

  /**
   * Updates an existing procedures information
   */
  @FXML
  void updateProcedures() {
    Memento<User> memento = new Memento<>();
    memento.setOldObject(currentUser.clone());
    procedureWarningLabel.setText("");
    String newName = procedureTextField.getText();
    LocalDate newDate = procedureDateSelector.getValue();
    String newDescription = descriptionTextArea.getText();
    if (newName.isEmpty()) {
      procedureWarningLabel.setText("A name must be entered for a procedure");
      return;
    }
    if (newDate == null) {
      procedureWarningLabel.setText("A valid date must be entered for a procedure");
      return;
    }
    if (newDate.isBefore(currentUser.getDateOfBirth())) {
      procedureWarningLabel.setText("Procedures may not occur before a patient has been born");
      return;
    }
    if (previousProcedureTableView.getSelectionModel().getSelectedItem() != null) {
      MedicalProcedure procedure = previousProcedureTableView.getSelectionModel().getSelectedItem();

      updateProcedure(procedure, newName, newDate, newDescription);
    } else if (pendingProcedureTableView.getSelectionModel().getSelectedItem() != null) {
      MedicalProcedure procedure = pendingProcedureTableView.getSelectionModel().getSelectedItem();

      updateProcedure(procedure, newName, newDate, newDescription);
    }
    memento.setNewObject(currentUser.clone());
    currentUser.getUndoStack().push(memento);
  }

  /**
   * * Helper function for the updateProcedures button. Takes a procedure and updates it
   *
   * @param procedure procedure to be updated
   * @param newName new procedure name
   * @param newDate new procedure date
   * @param newDescription new procedure description
   */
  private void updateProcedure(MedicalProcedure procedure, String newName, LocalDate newDate,
      String newDescription) {
    Memento<User> memento = new Memento<>();
    memento.setOldObject(currentUser.clone());
    procedure.setSummary(newName);
    procedure.setDescription(newDescription);
    LocalDate oldDate = procedure.getProcedureDate();
    if (newDate == null) {

    } else {
      procedure.setProcedureDate(newDate);
    }
    if (procedure.getProcedureDate().isBefore(LocalDate.now()) && !oldDate
        .isBefore(LocalDate.now())) {
      pendingProcedures.remove(procedure);
      previousProcedures.add(procedure);
    } else if (procedure.getProcedureDate().isAfter(LocalDate.now()) && !oldDate
        .isAfter(LocalDate.now())) {
      previousProcedures.remove(procedure);
      pendingProcedures.add(procedure);
    }
    application.update(currentUser);
    memento.setNewObject(currentUser.clone());
    currentUser.getUndoStack().push(memento);
  }

  /**
   * Shows all the information for a given procedure
   *
   * @param procedure current medical procedure
   */
  private void showProcedure(MedicalProcedure procedure) {
    procedureTextField.setText(procedure.getSummary());
    procedureDateSelector.setValue(procedure.getProcedureDate());
    descriptionTextArea.setText(procedure.getDescription());
    organsAffectedByProcedureListView
        .setItems(FXCollections.observableList(procedure.getOrgansAffected()));
    updateUndoRedoButtons();
    pendingProcedureTableView.refresh();
    previousProcedureTableView.refresh();
  }

  /**
   * Clears the information of a shown procedure
   */
  @FXML
  void clearProcedure() {
    procedureWarningLabel.setText("");
    procedureTextField.setText("");
    procedureDateSelector.setValue(LocalDate.now());
    descriptionTextArea.setText("");
    pendingProcedureTableView.getSelectionModel().select(null);
    previousProcedureTableView.getSelectionModel().select(null);
    organsAffectedByProcedureListView.setItems(FXCollections.observableList(new ArrayList<>()));
    modifyOrgansProcedureButton.setVisible(false);
    currentProcedureList = null;
  }

  /**
   * Removes a procedure from the curernt users profile
   *
   * @param event passed in automatically by the gui
   */
  @FXML
  void removeProcedure(ActionEvent event) {
    Memento<User> memento = new Memento<>();
    memento.setOldObject(currentUser.clone());
    if (previousProcedureTableView.getSelectionModel().getSelectedItem() != null) {
      medicalProcedures.remove(previousProcedureTableView.getSelectionModel().getSelectedItem());
      currentUser
          .removeMedicalProcedure(previousProcedureTableView.getSelectionModel().getSelectedItem());
      previousProcedures.remove(previousProcedureTableView.getSelectionModel().getSelectedItem());
    } else if (pendingProcedureTableView.getSelectionModel().getSelectedItem() != null) {
      medicalProcedures.remove(pendingProcedureTableView.getSelectionModel().getSelectedItem());
      currentUser
          .removeMedicalProcedure(pendingProcedureTableView.getSelectionModel().getSelectedItem());
      pendingProcedures.remove(pendingProcedureTableView.getSelectionModel().getSelectedItem());
    }
    application.update(currentUser);
    memento.setNewObject(currentUser.clone());
    currentUser.getUndoStack().push(memento);
  }

  /**
   * Opens the modify procedure organs window for the selected procedure
   */
  @FXML
  void modifyProcedureOrgans() {
    Memento<User> memento = new Memento<>();
    memento.setOldObject(currentUser.clone());
    MedicalProcedure procedure = currentProcedureList.getSelectionModel().getSelectedItem();
    FXMLLoader affectedOrganLoader = new FXMLLoader(
        getClass().getResource("/FXML/organsAffectedView.fxml"));
    Parent root = null;
    try {
      root = affectedOrganLoader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }

    Stage s = new Stage();
    s.setScene(new Scene(root));
    s.initModality(Modality.APPLICATION_MODAL);
    OrgansAffectedController organsAffectedController = affectedOrganLoader.getController();
    organsAffectedController.init(application, s, procedure, currentUser);
    s.showAndWait();
    memento.setNewObject(currentUser.clone());
    currentUser.getUndoStack().push(memento);
    showProcedure(procedure);
  }

  /**
   * show the current and past diseases of the donor.
   */
  public void showDonorDiseases(User user, boolean init) {
    if (user.getCurrentDiseases().size() != 0) {
      currentDisease = FXCollections.observableList(user.getCurrentDiseases());
      currentDiseaseTableView.setItems(currentDisease);

    } else {
      currentDiseaseTableView.setPlaceholder(new Label("No Current Diseases"));
    }

    if (user.getPastDiseases().size() != 0) {
      pastDisease = FXCollections.observableList(user.getPastDiseases());
      pastDiseaseTableView.setItems(pastDisease);

    } else {
      pastDiseaseTableView.setPlaceholder(new Label("No Past Diseases"));
    }

    if (init) {
      TableColumn<Disease, LocalDate> diagnosisDateColumn = new TableColumn<>("Diagnosis Date");
      diagnosisDateColumn.setMinWidth(110);
      diagnosisDateColumn.setMaxWidth(110);
      diagnosisDateColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosisDate"));

      TableColumn<Disease, String> nameColumn = new TableColumn<>("Disease Name");
      nameColumn.setMinWidth(235);
      nameColumn.setMaxWidth(235);
      nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

      TableColumn<Disease, Boolean> chronicColumn = new TableColumn<>("Chronic");
      chronicColumn.setMinWidth(70);
      chronicColumn.setMaxWidth(70);
      chronicColumn.setCellValueFactory(new PropertyValueFactory<>("isChronic"));

      chronicColumn.setCellFactory(column -> new TableCell<Disease, Boolean>() {
        @Override
        protected void updateItem(Boolean item, boolean empty) {
          super.updateItem(item, empty);

          setText(empty ? "" : getItem().toString());
          setGraphic(null);

          if (item == null) {
            return;
          }

          if (item) {
            setText("Chronic");
            setTextFill(Color.RED);
          } else {
            setText("");
          }
        }
      });
      currentDiseaseTableView.getColumns().addAll(diagnosisDateColumn, nameColumn, chronicColumn);

      TableColumn<Disease, LocalDate> diagnosisDateColumn2 = new TableColumn<>("Diagnosis Date");
      diagnosisDateColumn2.setMinWidth(110);
      diagnosisDateColumn2.setMaxWidth(110);
      diagnosisDateColumn2.setCellValueFactory(new PropertyValueFactory<>("diagnosisDate"));

      TableColumn<Disease, String> nameColumn2 = new TableColumn<>("Disease Name");
      nameColumn2.setMinWidth(305);
      nameColumn2.setMaxWidth(305);
      nameColumn2.setCellValueFactory(new PropertyValueFactory<>("name"));

      pastDiseaseTableView.getColumns().addAll(diagnosisDateColumn2, nameColumn2);

    }
  }

  /**
   * fires when the add button at the Disease tab is clicked
   */
  @FXML
  private void addDisease() {

    FXMLLoader addDiseaseLoader = new FXMLLoader(
        getClass().getResource("/FXML/createNewDisease.fxml"));
    Parent root = null;
    try {
      root = addDiseaseLoader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
    NewDiseaseController newDiseaseController = addDiseaseLoader.getController();
    Stage stage = new Stage();
    Disease disease = new Disease("", false, false, LocalDate.now());
    currentUser.addCurrentDisease(disease);
    newDiseaseController.init(currentUser, application, stage, disease, this);
    stage.setScene(new Scene(root));
    stage.show();


  }

  /*Receiver*/

  /**
   * register an organ* for receiver
   */
  @FXML
  public void registerOrgan() {
    if (organsComboBox.getSelectionModel().getSelectedItem() != null) {
      Organs toRegister = organsComboBox.getSelectionModel().getSelectedItem();AppController.getInstance().getClinicianController().refreshTables();
      if (!currentlyReceivingListView.getItems().contains(toRegister)) {
        currentUser.getReceiverDetails().startWaitingForOrgan(toRegister);
        currentlyRecieving.add(toRegister);
        organsComboBox.getItems().remove(toRegister);
        organsComboBox.setValue(null);// reset the combobox
        application.update(currentUser);
        if (currentUser.getDonorDetails().getOrgans().contains(toRegister)) {
          currentUser.getCommonOrgans().add(toRegister);
        }

        //set mouse click for currentlyReceivingListView
        currentlyReceivingListView.setOnMouseClicked(event -> {
          if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            Organs currentlyReceivingOrgan = currentlyReceivingListView.getSelectionModel()
                .getSelectedItem();
            launchReceiverOrganDateView(currentlyReceivingOrgan);
          }
        });
        updateUndoRedoButtons();
      }

      currentlyDonating.refresh();
      currentlyReceivingListView.refresh();
    }
  }

    /**
     * re-register an organ
     * for receiver
     */
    @FXML
    public void reRegisterOrgan () {
        Organs toReRegister = notReceivingListView.getSelectionModel().getSelectedItem();
        if (toReRegister != null) {
            currentlyReceivingListView.getItems().add(toReRegister);
            currentUser.getReceiverDetails().startWaitingForOrgan(toReRegister);
            notReceivingListView.getItems().remove(toReRegister);
            AppController.getInstance().getClinicianController().refreshTables();
            if (currentUser.getReceiverDetails().isDonatingThisOrgan(toReRegister)) {
                currentUser.getCommonOrgans().add(toReRegister);
            }

      //if notReceiving list view is empty, disable mouse click to prevent null pointer exception
      if (notReceivingListView.getItems().isEmpty()) {
        notReceivingListView.setOnMouseClicked(null);
      }
      //set mouse click for currentlyReceivingListView
      currentlyReceivingListView.setOnMouseClicked(event -> {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
          Organs currentlyReceivingOrgan = currentlyReceivingListView.getSelectionModel()
              .getSelectedItem();
          launchReceiverOrganDateView(currentlyReceivingOrgan);
        }
      });
      updateUndoRedoButtons();
    }

    currentlyDonating.refresh();
    currentlyReceivingListView.refresh();
  }

  /**
   * opens the deregister organ reason window when the deregister button at the Receiver tab is
   * clicked
   */
  @FXML
  private void deregisterOrganReason() {
    Organs toDeRegister = currentlyReceivingListView.getSelectionModel().getSelectedItem();
    if (toDeRegister != null) {
      FXMLLoader deregisterOrganReasonLoader = new FXMLLoader(
          getClass().getResource("/FXML/deregisterOrganReasonView.fxml"));
      Parent root = null;
      try {
        root = deregisterOrganReasonLoader.load();
      } catch (IOException e) {
        e.printStackTrace();
      }
      DeregisterOrganReasonController deregisterOrganReasonController = deregisterOrganReasonLoader
          .getController();
      Stage stage = new Stage();
      deregisterOrganReasonController.init(toDeRegister, this, currentUser, application, stage);
      stage.setScene(new Scene(root));
      stage.show();
    }
  }

    /**
     * de-register an organ
     * for receiver
     * @param toDeRegister the organ to be removed from the
     */
    public void deRegisterOrgan (Organs toDeRegister) {
        if (toDeRegister != null) {

            if(organDeregisterationReason == OrganDeregisterReason.TRANSPLANT_RECEIVED){
                currentUser.getReceiverDetails().stopWaitingForOrgan(toDeRegister);

            } else if(organDeregisterationReason == OrganDeregisterReason.REGISTRATION_ERROR){
              currentUser.getReceiverDetails().stopWaitingForOrgan(toDeRegister);
              currentUser.getChanges().add(new Change("Initial registering of the organ " + toDeRegister.organName + " was an error for receiver " + currentUser.getFullName()));

            } else if (organDeregisterationReason == OrganDeregisterReason.DISEASE_CURED){
              //refresh diseases table
                currentUser.getReceiverDetails().stopWaitingForOrgan(toDeRegister);
                diseaseRefresh(this.getIsSortedByName(), this.getIsRevereSorted());


            } else if(organDeregisterationReason == OrganDeregisterReason.RECEIVER_DIED){
              List<Organs> currentlyReceiving = new ArrayList<>(currentlyReceivingListView.getItems());
              for(Organs organ : currentlyReceiving){
                notReceivingListView.getItems().add(organ);
                currentlyReceivingListView.getItems().remove(organ);
              }
              currentUser.getReceiverDetails().stopWaitingForAllOrgans();
              registerButton.setDisable(true);
              reRegisterButton.setDisable(true);
            }

            if(organDeregisterationReason != OrganDeregisterReason.RECEIVER_DIED) {
              notReceivingListView.getItems().add(toDeRegister);
              currentlyReceivingListView.getItems().remove(toDeRegister);
            }

            if (currentUser.getCommonOrgans().contains(toDeRegister)) {
                currentUser.getCommonOrgans().remove(toDeRegister);
            }

      //if currentlyReceivingListView is empty, disable mouse click to prevent null pointer exception
      if (currentlyReceivingListView.getItems().isEmpty()) {
        currentlyReceivingListView.setOnMouseClicked(null);
      }
      //set mouse click for notReceivingListView
      notReceivingListView.setOnMouseClicked(event -> {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
          Organs currentlyReceivingOrgan = notReceivingListView.getSelectionModel()
              .getSelectedItem();
          launchReceiverOrganDateView(currentlyReceivingOrgan);
        }
      });
          updateUndoRedoButtons();
            application.update(currentUser);
          currentlyDonating.refresh();
          currentlyReceivingListView.refresh();
          AppController.getInstance().getClinicianController().refreshTables();
        }
  }

  /**
   * Updates the disabled property of the undo/redo buttons
   */
  public void updateUndoRedoButtons() {
    undoButton.setDisable(currentUser.getUndoStack().isEmpty());
    redoButton.setDisable(currentUser.getRedoStack().isEmpty());
  }

  /**
   * Launch the time table which shows the register and deregister date of a particular organ
   *
   * @param organs enum
   */
  private void launchReceiverOrganDateView(Organs organs) {
    FXMLLoader receiverOrganDateViewLoader = new FXMLLoader(
        getClass().getResource("/FXML/receiverOrganDateView.fxml"));
    Parent root = null;
    try {
      root = receiverOrganDateViewLoader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
    Stage stage = new Stage();
    stage.setScene(new Scene(root));
    ReceiverOrganDateController receiverOrganDateController = receiverOrganDateViewLoader
        .getController();
    receiverOrganDateController.init(application, currentUser, stage, organs);
    stage.show();
  }

  /**
   * Moves selected organ from donatable to currently donating
   *
   * @param event passed in automatically by the gui
   */
  @FXML
  void donate(ActionEvent event) {

    if (!canDonate.getSelectionModel().isEmpty()) {
      Organs toDonate = canDonate.getSelectionModel().getSelectedItem();
      currentlyDonating.getItems().add(toDonate);
      currentUser.getDonorDetails().addOrgan(toDonate);
      if (currentlyRecieving.contains(toDonate)) {
        currentUser.getCommonOrgans().add(toDonate);
      }
      application.update(currentUser);
      canDonate.getItems().remove(toDonate);
      updateUndoRedoButtons();
    }
    currentlyDonating.refresh();
    currentlyReceivingListView.refresh();
  }

  /**
   * Moves selected organ from currently donating to donatable
   *
   * @param event passed in automatically by the gui
   */
  @FXML
  void undonate(ActionEvent event) {
    if (!currentlyDonating.getSelectionModel().isEmpty()) {
      Organs toUndonate = currentlyDonating.getSelectionModel().getSelectedItem();
      currentlyDonating.getItems().remove(toUndonate);
      canDonate.getItems().add(toUndonate);
      if (currentUser.getCommonOrgans().contains(toUndonate)) {
        currentUser.getCommonOrgans().remove(toUndonate);
        currentlyDonating.refresh();
      }

      currentUser.getDonorDetails().removeOrgan(toUndonate);
      currentlyDonating.refresh();
      application.update(currentUser);
      updateUndoRedoButtons();
    }

    currentlyDonating.refresh();
    currentlyReceivingListView.refresh();
  }

  /**
   * Checks if a disease is selected in either 'Past' or 'Current' tables. If so, it passes that
   * into NewDiseaseController to open up the 'disease editor' window. NewDiseaseController should
   * probably be renamed to diseaseEditor
   */
  @FXML
  private void updateDisease() {

    FXMLLoader addDiseaseLoader = new FXMLLoader(
        getClass().getResource("/FXML/createNewDisease.fxml"));
    Parent root = null;
    try {
      root = addDiseaseLoader.load();
      root.requestFocus(); //Currently the below code thinks that focus = selected so will always take the focused
      // thing in currentDiseases over the selected thing in pastDiseases. Trying to fix
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (currentDiseaseTableView.getSelectionModel().getSelectedItem()
        != null) { //Might error, dunno what it returns if nothing is selected, hopefully -1?
      Disease disease = currentDiseaseTableView.getSelectionModel()
          .getSelectedItem(); //Get the selected disease

      NewDiseaseController newDiseaseController = addDiseaseLoader
          .getController(); //Load some stuff
      Stage stage = new Stage();
      newDiseaseController.init(currentUser, application, stage, disease, this);
      stage.setScene(new Scene(root));
      stage.show();

    } else if (pastDiseaseTableView.getSelectionModel().getSelectedItem() != null) {
      Disease disease = pastDiseaseTableView.getSelectionModel().getSelectedItem();

      NewDiseaseController newDiseaseController = addDiseaseLoader.getController();
      Stage stage = new Stage();
      //Disease disease = currentUser.getD
      newDiseaseController.init(currentUser, application, stage, disease, this);
      stage.setScene(new Scene(root));
      stage.show();
    }
  }


  /**
   * Deletes the currently selected disease by moving it to the past diseases table
   */
  @FXML
  private void deleteDisease() {
    if (currentDiseaseTableView.getSelectionModel().getSelectedIndex() >= 0) {
      if (!currentDiseaseTableView.getSelectionModel().getSelectedItem().getIsChronic()) {
        currentUser.getCurrentDiseases()
            .remove(currentDiseaseTableView.getSelectionModel().getSelectedItem());
      } else {
        return;
      }
    } else if (pastDiseaseTableView.getSelectionModel().getSelectedIndex() >= 0) {
      currentUser.getPastDiseases()
          .remove(pastDiseaseTableView.getSelectionModel().getSelectedItem());
    }

    this.application.update(currentUser);
    showDonorDiseases(currentUser, false); //Reload the scene?
  }

  //Yuck
  public TableView<Disease> getPastDiseaseTableView() {
    return pastDiseaseTableView;
  }

  public TableView<Disease> getCurrentDiseaseTableView() {
    return currentDiseaseTableView;
  }

  public boolean getIsRevereSorted() {
    return isReverseSorted;
  }

  public void setIsReverseSorted(boolean bool) {
    isReverseSorted = bool;
  }

  public boolean getIsSortedByName() {
    return isSortedByName;
  }

  public void setIsSortedByName(boolean bool) {
    isSortedByName = bool;
  }


  public void diseaseRefresh(boolean isSortedByName, boolean isReverseSorted) {
    Disease disease = new Disease("", false, false, LocalDate.now());
    Collections.sort(currentUser.getCurrentDiseases(), disease.diseaseNameComparator);
    Collections.sort(currentUser.getPastDiseases(), disease.diseaseDateComparator);

    if (isSortedByName) {
      Collections.sort(currentUser.getCurrentDiseases(), disease.diseaseNameComparator);
      Collections.sort(currentUser.getPastDiseases(), disease.diseaseNameComparator);

    }
    if (isReverseSorted) {
      Collections.sort(currentUser.getCurrentDiseases(), Collections.reverseOrder());
      Collections.sort(currentUser.getPastDiseases(), disease.diseaseNameComparator);
    }
    Collections.sort(currentUser.getCurrentDiseases(), disease.diseaseChronicComparator);

//    getCurrentDiseaseTableView().refresh();
//    getPastDiseaseTableView().refresh();
    showDonorDiseases(currentUser, false);
  }

}
