package seng302.Controller;


import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import okhttp3.OkHttpClient;
import org.controlsfx.control.textfield.TextFields;
import seng302.Model.*;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Class for the functionality of the User view of the application
 */
public class DonorController {

    //the Home page attributes
    @FXML
    private Button backButton;

    @FXML
    private Label ageValue;

    @FXML
    private Label ageDeathValue;

    @FXML
    private Label NHIValue;

    @FXML
    private Label fNameValue;

    @FXML
    private Label lNameValue;

    @FXML
    private Label pNameValue;

    @FXML
    private Label mNameValue;

    @FXML
    private Label DOBValue;

    @FXML
    private Label DODValue;

    @FXML
    private Label genderIdentityValue;

    @FXML
    private Label birthGenderValue;

    @FXML
    private Label lastModifiedValue;

    @FXML
    private Label createdValue;

    @FXML
    private Label bloodTypeValue;

    @FXML
    private Label heightValue;

    @FXML
    private Label weightValue;

    @FXML
    private Label smokerValue;

    @FXML
    private Label alcoholValue;

    @FXML
    private Label bmiValue;


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


    @FXML
    private TextArea drugDetailsTextArea;

    @FXML
    private Label drugDetailsLabel;

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

    private TableView<MedicalProcedure> currentProcedureList;

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
    private ObservableList<String> currentMeds;
    private ObservableList<String> previousMeds;
    private ObservableList<MedicalProcedure> medicalProcedures;
    private ObservableList<MedicalProcedure> previousProcedures;
    private ObservableList<MedicalProcedure> pendingProcedures;
    private HashMap<Organs, ArrayList<LocalDate>> receiverOrgans = new HashMap<>();
    private ObservableList<Organs> currentlyRecieving;
    private ObservableList<Organs> noLongerReceiving;


  private ObservableList<Disease> currentDisease;
  private ObservableList<Disease> pastDisease;private List<String> possibleGenders = Arrays.asList("M", "F", "U");

    private List<String> possibleBloodTypes = Arrays
            .asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "U");

    private User currentUser;
    private Stage stage;
    private EmergencyContact contact = null;
    private ObservableList<Change> changelog;
    private OkHttpClient client = new OkHttpClient();
    private Boolean Clinician;
    private boolean isSortedByName = false;
    private boolean isReverseSorted = false;

    private OrganDeregisterReason organDeregisterationReason;


    /**
     * Gives the donor view the application controller and hides all label and buttons that are not
     * needed on opening
     * @param controller the application controller
     * @param user the current user
     * @param stage the application stage
     * @param fromClinician boolean value indication if from clinician view
     */
    public void init(AppController controller, User user, Stage stage, Boolean fromClinician) {

        this.stage = stage;
        application = controller;
        //ageValue.setText("");
        //This is the place to set visable and invisable controls for Clinician vs User
    if (fromClinician) {
      Clinician = true;
      logOutButton.setVisible(false);
    addDiseaseButton.setVisible(true);
      updateDiseaseButton.setVisible(true);
      deleteDiseaseButton.setVisible(true);
    }else {
        Clinician = false;
            procedureDateSelector.setEditable(false);
            procedureTextField.setEditable(false);
            descriptionTextArea.setEditable(false);
            addProcedureButton.setVisible(false);
            removeProcedureButton.setVisible(false);
            updateProceduresButton.setVisible(false);
            modifyOrgansProcedureButton.setVisible(false);
        deleteButton.setVisible(false);
        addMedicationButton.setVisible(false);
        medicationTextField.setVisible(false);
        backButton.setVisible(false);
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
    currentUser = user;
    contact = user.getContact();
      ArrayList<Organs> donating;
      try {
        donating= new ArrayList<>(user.getDonorDetails().getOrgans());
      }
      catch (NullPointerException ex) {
        donating = new ArrayList<>();
      }
      currentlyDonating.setItems(FXCollections.observableList(donating));
      ArrayList<Organs> leftOverOrgans = new ArrayList<Organs>();
      Collections.addAll(leftOverOrgans, Organs.values());
      for (Organs o : donating){
        leftOverOrgans.remove(o);
      }
      canDonate.setItems(FXCollections.observableList(leftOverOrgans));

        currentMeds = FXCollections.observableArrayList();

        previousMeds = FXCollections.observableArrayList();
        currentMedicationListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        previousMedicationListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //listeners to move meds from current <--> previous
        previousMeds.addListener((ListChangeListener.Change<? extends String> change) -> {
            previousMedicationListView.setItems(previousMeds);
            application.update(currentUser);
        });
        currentMeds.addListener((ListChangeListener.Change<? extends String> change) -> {
            currentMedicationListView.setItems(currentMeds);
            application.update(currentUser);
        });

        //lambdas for drug interactions
        currentMedicationListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    ObservableList<String> selected = currentMedicationListView.getSelectionModel()
                            .getSelectedItems();
                    displayDetails(selected);
                });
        previousMedicationListView.getSelectionModel().selectedItemProperty()
                .addListener(((observable, oldValue, newValue) -> {
                    ObservableList<String> selected = previousMedicationListView.getSelectionModel()
                            .getSelectedItems();
                    displayDetails(selected);
                }));
        currentMedicationListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                String med = currentMedicationListView.getSelectionModel().getSelectedItem();
                launchMedicationView(med);
            }
        });
        previousMedicationListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                String med = previousMedicationListView.getSelectionModel().getSelectedItem();
                launchMedicationView(med);
            }
        });

        medicationTextField.focusedProperty().addListener((observable, oldValue, newValue) -> new Thread(() -> getDrugSuggestions()).start());
        medicationTextField.textProperty().addListener((observable) -> new Thread(() -> getDrugSuggestions()).start());
        procedureWarningLabel.setText("");
        procedureDateSelector.setValue(LocalDate.now());
        previousProcedures = FXCollections.observableArrayList();
        pendingProcedures = FXCollections.observableArrayList();
        pendingProcedureTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        previousProcedureTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        previousProcedureTableView.getSelectionModel().selectedItemProperty().addListener(ListChangeListener -> {
            pendingProcedureTableView.getSelectionModel().select(null);
            if (previousProcedureTableView.getSelectionModel().getSelectedItem() != null) {
                showProcedure(previousProcedureTableView.getSelectionModel().getSelectedItem());
                modifyOrgansProcedureButton.setVisible(true);
                currentProcedureList = previousProcedureTableView;
            }
        });
        pendingProcedureTableView.getSelectionModel().selectedItemProperty().addListener(ListChangeListener -> {
            previousProcedureTableView.getSelectionModel().select(null);
            if (pendingProcedureTableView.getSelectionModel().getSelectedItem() != null) {
                showProcedure(pendingProcedureTableView.getSelectionModel().getSelectedItem());
                modifyOrgansProcedureButton.setVisible(true);
                currentProcedureList = pendingProcedureTableView;
            }
        });

        TableColumn pendingProcedureColumn = new TableColumn("Procedure");
        TableColumn pendingDateColumn = new TableColumn("Date");
        TableColumn previousProcedureColumn = new TableColumn("Procedure");
        TableColumn previousDateColumn = new TableColumn("Date");
        pendingProcedureColumn.setCellValueFactory(new PropertyValueFactory<MedicalProcedure, String>("summary"));
        previousProcedureColumn.setCellValueFactory(new PropertyValueFactory<MedicalProcedure, String>("summary"));
        pendingDateColumn.setCellValueFactory(new PropertyValueFactory<Change, String>("procedureDate"));
        previousDateColumn.setCellValueFactory(new PropertyValueFactory<Change, String>("procedureDate"));
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
            showUser(currentUser); // Assumes a donor with no name is a new sign up and does not pull values from a template
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
        changelog.addListener((ListChangeListener.Change<? extends Change> change) -> historyTableView.setItems(changelog));
        medicationTextField.setOnMouseClicked(event -> getDrugSuggestions());
        medicationTextField.textProperty().addListener((observable) -> getDrugSuggestions());

        showDonorDiseases(currentUser, true);
        modifyOrgansProcedureButton.setVisible(false);


        currentDiseaseTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        pastDiseaseTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        currentDiseaseTableView.getSelectionModel().selectedItemProperty().addListener(ListChangeListener -> {
            pastDiseaseTableView.getSelectionModel().select(null);
//            if (previousProcedureTableView.getSelectionModel().getSelectedItem() != null) {
//                showProcedure(previousProcedureTableView.getSelectionModel().getSelectedItem());
//                modifyOrgansProcedureButton.setVisible(true);
//                currentProcedureList = previousProcedureTableView;
//            }
        });
        pastDiseaseTableView.getSelectionModel().selectedItemProperty().addListener(ListChangeListener -> {
            currentDiseaseTableView.getSelectionModel().select(null);
//            if (pendingProcedureTableView.getSelectionModel().getSelectedItem() != null) {
//                showProcedure(pendingProcedureTableView.getSelectionModel().getSelectedItem());
//                modifyOrgansProcedureButton.setVisible(true);
//                currentProcedureList = pendingProcedureTableView;
//            }
        });

//        Callback<TableColumn<Disease, String>, TableCell<Disease, String>> cellFactory =
//            new Callback<TableColumn<Disease, String>, TableCell<Disease, String>>() {
//                public TableCell call(TableColumn p) {
//                    TableCell cell = new TableCell<Disease, String>() {
//                        @Override
//                        public void updateItem(String item, boolean empty) {
//                            super.updateItem(item, empty);
//                            setText(empty ? null : getString());
//                            setStyle("-fx-background-color:ff0033");
//                        }
//
//                        private String getString() {
//                            return getItem() == null ? "" : getItem().toString();
//                        }
//                    };
//
//                    return cell;
//                }
//            };


        //init receiver organs combo box
        ArrayList<Organs> organs = new ArrayList<>();
        Collections.addAll(organs, Organs.values());

        //display registered and deregistered receiver organs if any
        Map<Organs, ArrayList<LocalDate>> receiverOrgans = currentUser.getReceiverDetails().getOrgans();
        if (receiverOrgans == null){
            receiverOrgans = new EnumMap<Organs, ArrayList<LocalDate>>(Organs.class);
        }
        currentlyRecieving = FXCollections.observableArrayList();
        noLongerReceiving = FXCollections.observableArrayList();
        if(!receiverOrgans.isEmpty()) {
            Set<Organs> allOrgans = receiverOrgans.keySet();
            for (Organs organ : receiverOrgans.keySet()) {
                if (currentUser.getReceiverDetails().isCurrentlyWaitingFor(organ)) {
                    organs.remove(organ);
                    currentlyRecieving.add(organ);
                } else {
                    organs.remove(organ);
                    noLongerReceiving.add(organ);
                }
            }
        }

        else if (!fromClinician) { //if user is not a receiver and not login as clinician
            currentlyReceivingLabel.setVisible(false);
            notReceivingLabel.setVisible(false);
            currentlyReceivingListView.setVisible(false);
            notReceivingListView.setVisible(false);
            notReceiverLabel.setVisible(true);
        }

        currentlyReceivingListView.setItems(currentlyRecieving);
        notReceivingListView.setItems(noLongerReceiving);
        organsComboBox.setItems(FXCollections.observableList(organs));


        if(!notReceivingListView.getItems().isEmpty()) {
            notReceivingListView.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    Organs notReceivingOrgan = notReceivingListView.getSelectionModel().getSelectedItem();
                    launchReceiverOrganDateView(notReceivingOrgan);
                }
            });
        }

        if(!currentlyReceivingListView.getItems().isEmpty()) {
            currentlyReceivingListView.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    Organs currentlyReceivingOrgan = currentlyReceivingListView.getSelectionModel().getSelectedItem();
                    launchReceiverOrganDateView(currentlyReceivingOrgan);
                }
            });
        }
        currentlyDonating.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        currentlyReceivingListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        currentlyDonating.setCellFactory(column -> new ListCell<Organs>() {
            @Override
            protected void updateItem(Organs item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : getItem().toString());
                setGraphic(null);

                if (item == null) {
                    return;
                }

                if(currentUser.getCommonOrgans().contains(item)) {
                    setTextFill(Color.RED);
                }
                else {
                    setTextFill(Color.BLACK);
                }
            }
        });

        currentlyReceivingListView.setCellFactory(column -> new ListCell<Organs>() {
            @Override
            protected void updateItem(Organs item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : getItem().toString());
                setGraphic(null);

                if (item == null) {
                    return;
                }

                if(currentUser.getCommonOrgans().contains(item)) {
                    setTextFill(Color.RED);
                }
                else {
                    setTextFill(Color.BLACK);
                }
            }
        });
    }


    public OrganDeregisterReason getOrganDeregisterationReason(){
        return organDeregisterationReason;
    }

    public void setOrganDeregisterationReason(OrganDeregisterReason organDeregisterationReason){
        this.organDeregisterationReason = organDeregisterationReason;
    }

    /**
     * takes selected items from lambda functions. handles http requesting and displaying results if
     * one item is selected, active ingredients will be shown. If two are selected, the interactions
     * between the two will be displayed
     *
     * @param selected selected items from listview
     */
    private void displayDetails(ObservableList<String> selected) {
        if (selected.size() > 2) {
            drugDetailsLabel.setText("Drug Details");
            drugDetailsTextArea.setText(
                    "Please select any two drugs from either previous or current medications to view the interactions between them\n"
                            + "or select one drug to see it's active ingredients");
            return;
        }

        try {
            //active ingredients
            if (selected.size() == 1) {
                String[] res = HttpRequester.getActiveIngredients(selected.get(0), client);
                StringBuilder sb = new StringBuilder();
                for (String ingredient : res) {
                    sb.append(ingredient);
                    sb.append("\n");
                }
                drugDetailsLabel.setText("Drug Details: Active Ingredients");
                if (sb.toString().equals("")) {
                    drugDetailsTextArea.setText("Could not find active ingredients");
                    return;
                }
                drugDetailsTextArea.setText(sb.toString());

            } else /*interactions*/ {
                Set<String> res = HttpRequester
                        .getDrugInteractions(selected.get(0), selected.get(1), currentUser.getGender(),
                                currentUser.getAge(), client);
                StringBuilder sb = new StringBuilder();
                for (String symptom : res) {
                    sb.append(symptom);
                    sb.append("\n");
                }
                drugDetailsLabel.setText("Drug Details: Drug Interactions");
                if (sb.toString().equals("")) {
                    drugDetailsTextArea.setText("No interactions");
                    return;
                }
                drugDetailsTextArea.setText(sb.toString());
            }
        } catch (IOException ex) {
            //TODO display connectivity error message
            System.out.println("oof");
        }

    }


    /**
     * Creates a alert pop up to confirm that the user wants to delete the profile
     * @param actionEvent given from the GUI
     * @throws IOException to make sure current I/O is used
     */
  @FXML
  public void delete(ActionEvent actionEvent) throws IOException {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setContentText("Are you sure you want to delete this user?");
    Optional<ButtonType> result = alert.showAndWait();

    if (result.get() == ButtonType.OK) {
      application.deleteDonor(currentUser);
      if (!Clinician) {
        logout();
      }
    }
  }

    /**
     * Takes the information in the medication text fields and then calls the required API to get auto complete information
     * Which is then displayed. Should always be started on a new thread
     */
    private void getDrugSuggestions() {
        String newValue = medicationTextField.getText();
        if (newValue.length() > 1) {
            try {
                String autocompleteRaw = HttpRequester.getSuggestedDrugs(newValue, new OkHttpClient());
                String[] values = autocompleteRaw.replaceAll("^\"", "").replaceAll("\\[", "").replaceAll("\\]", "").split("\"?(,|$)(?=(([^\"]*\"){2})*[^\"]*$) *\"?");
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].replace('"', ' ').trim();
                }
                TextFields.bindAutoCompletion(medicationTextField, values);
            } catch (IOException e) {
                e.printStackTrace();
            }

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
     * Opens the update user details window
     * @param actionEvent An action event.
     * @throws IOException to make sure I/O is correct
     * @throws InterruptedException to make sure there is no interruption
     */
    @FXML
    private void updateDetails(ActionEvent actionEvent) throws IOException, InterruptedException {
        FXMLLoader updateLoader = new FXMLLoader(getClass().getResource("/FXML/updateUser.fxml"));
        Parent root = null;
        try {
            root = updateLoader.load();
            UpdateUserController updateUserController = updateLoader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            updateUserController.init(currentUser, application, stage);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * fires when the Undo button is clicked
     */
    @FXML
    private void undo() {
        currentUser = UndoRedoStacks.loadUndoCopy(currentUser);
        showUser(currentUser); //Error with showing donors


    }

    /**
     * organsDonatingListView.getItems().clear();@FXML
    private ListView<Organs> currentlyDonating;

    @FXML
    private ListView<Organs> canDonate;

    @FXML
    private Button donateButton;

    @FXML
    private Button undonateButton;

    @FXML
    private Label donorNameLabel;
     * organsDonatingListView.getItems().addAll(currentUser.getDonorDetails().getOrgans());
     */


    /**
     * fires when the Redo button is clicked
     */
    @FXML
    private void redo() {
        currentUser = UndoRedoStacks.loadRedoCopy(currentUser);
        showUser(currentUser);
    }

    /**
     * Fires when the logout button is clicked
     * Ends the users session, and takes back to the login window
     */
    @FXML
    private void logout() {
        //updateDonor();
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
        stage.hide();
        stage.show();

    //UndoRedoStacks.clearStacks();
  }

    /**
     *Shows the user profile for the logged in user
     * @param user The current user.
     */
    public void showUser(User user) {
        setContactPage();
        NHIValue.setText(currentUser.getNhi());
        fNameValue.setText(currentUser.getFirstName());
        DOBValue.setText(currentUser.getDateOfBirth().toString());
        if (currentUser.getMiddleName() != null) {
            mNameValue.setText(currentUser.getMiddleName());
        } else {
            mNameValue.setText("");
        }
        //if (currentUser.getPrefFirstName() != null) {
        pNameValue.setText(currentUser.getPrefFirstName());
//        } else {
//            pNameValue.setText("");
//        }
        if (currentUser.getLastName() != null) {
            lNameValue.setText(currentUser.getLastName());
        } else {
            lNameValue.setText("");
        }

        if (currentUser.getGenderIdentity() != null) {
            genderIdentityValue.setText(currentUser.getGenderIdentity());
        } else {
            genderIdentityValue.setText("");
        }
        if (currentUser.getBirthGender() != null) {
            birthGenderValue.setText(currentUser.getBirthGender());
        } else {
            birthGenderValue.setText("");
        }

        ageValue.setText(user.getStringAge().toString().replace("P", "").replace("Y", "") + " Years");
        if (currentUser.getDateOfDeath() != null) {
            DODValue.setText(currentUser.getDateOfDeath().toString());
            ageDeathValue.setText(Long.toString(
                    ChronoUnit.YEARS.between(currentUser.getDateOfBirth(), currentUser.getDateOfDeath())) + " Years");
        } else {
            DODValue.setText("");
        }
        if (currentUser.getBloodType() != null) {
            bloodTypeValue.setText(currentUser.getBloodType());
        }
        if (currentUser.isSmoker()) {
            smokerValue.setText("Yes");
        } else {
            smokerValue.setText("No");
        }
        //String weight;
        weightValue.setText(Double.toString(currentUser.getWeight()));
//        if (currentUser.getWeight() > 0) {
//            weight = java.lang.Double.toString(currentUser.getWeight());
//            weightValue.setText(weight);
//        } else {
//            weightValue.setText(0.0);
//        }
//        String height;
//        if (currentUser.getHeight() > 0) {
//            height = java.lang.Double.toString(currentUser.getHeight());
//            heightValue.setText(height);
//        }
        heightValue.setText(Double.toString(currentUser.getHeight()));
        if (currentUser.getHeight() > 0 && currentUser.getWeight() > 0) {
            //TODO fix BMI kg/m^
            DecimalFormat df = new DecimalFormat("#.00");
            double bmi = currentUser.getWeight() / (currentUser.getHeight() * currentUser.getHeight());
            String formattedBmi = df.format(bmi);
            bmiValue.setText(formattedBmi);
        } else {
            bmiValue.setText("");
        }

        if (currentUser.getLastModified() != null) {
            lastModifiedValue.setText(currentUser.getLastModified().toString());
        }
        createdValue.setText(currentUser.getTimeCreated().toString());
        alcoholValue.setText(currentUser.getAlcoholConsumption());

//    if (user.getMiscAttributes() != null) {
//      miscAttributeslistView.getItems().clear(); // HERE
//      for (String atty : user.getMiscAttributes()) {
//        miscAttributeslistView.getItems().add(atty);
//      }
//    }
    if (currentUser.getCurrentMedication() != null) {
      currentMeds.addAll(currentUser.getCurrentMedication());

            currentMedicationListView.setItems(currentMeds);
        }
        if (currentUser.getPreviousMedication() != null) {
            previousMeds.clear();
            previousMeds.addAll(currentUser.getPreviousMedication());
            previousMedicationListView.setItems(previousMeds);
        }
//        organsDonatingListView.getItems().addAll(currentUser.getDonorDetails().getOrgans());
//        if (!currentUser.getCommonOrgans().isEmpty()) {
//            for (Organs organ: currentUser.getCommonOrgans()) {
//                int index = organsDonatingListView.getItems().indexOf(organ);
//                organsDonatingListView.getSelectionModel().select(index);
//            }
//        }
        //organsDonatingListView.getItems().addAll(currentUser.getDonorDetails().getOrgans());
        setContactPage();
        medicalProcedures = FXCollections.observableList(currentUser.getMedicalProcedures());
        for (MedicalProcedure procedure : medicalProcedures) {
            if (procedure.getProcedureDate().isBefore(LocalDate.now())) {
                previousProcedures.add(procedure);
            } else {
                pendingProcedures.add(procedure);
            }
        }

        previousProcedureTableView.setItems(previousProcedures);
        pendingProcedureTableView.setItems(pendingProcedures);

        currentMedicationListView.setItems(currentMeds);

    if (currentUser.getPreviousMedication() != null) {
      previousMeds.clear();
      previousMeds.addAll(currentUser.getPreviousMedication());
      previousMedicationListView.setItems(previousMeds);
    }
    //organsDonatingListView.getItems().clear();
    //organsDonatingListView.getItems().addAll(currentUser.getDonorDetails().getOrgans());
//        if (!currentUser.getCommonOrgans().isEmpty()) {
//            for (Organs organ: currentUser.getCommonOrgans()) {
//                int index = organsDonatingListView.getItems().indexOf(organ);
//                organsDonatingListView.getSelectionModel().select(index);
//            }
//        }
    setContactPage();
    if (user.getLastName() != null) {
      stage.setTitle("User Profile: " + user.getFirstName() + " " + user.getLastName());
    } else {
      stage.setTitle("User Profile: " + user.getFirstName());

    }
  historyTableView.refresh();}

    /**
     *Adds a medication to the current users profile that they are taking
     * @param event An action event
     */
    @FXML
    void addMedication(ActionEvent event) {
        String medication = medicationTextField.getText();
        if (medication.isEmpty() || medication == null) {
            return;
        }
        if (currentMeds.contains(medication) || previousMeds.contains(medication)) {
            medicationTextField.setText("");
            return;
        }
        medicationTextField.setText("");
        currentMeds.add(medication);
        currentUser.addCurrentMedication(medication);


    }

    /**
     *Deletes a currently taking medication from the current users profile
     * @param event An action event
     */
    @FXML
    void deleteMedication(ActionEvent event) {
        String medCurrent = currentMedicationListView.getSelectionModel().getSelectedItem();
        String medPrevious = previousMedicationListView.getSelectionModel().getSelectedItem();

        if (medCurrent != null) {
            currentMeds.remove(medCurrent);
            currentUser.removeCurrentMedication(medCurrent);
        }
        if (medPrevious != null) {
            previousMeds.remove(medPrevious);
            currentUser.removePreviousMedication(medPrevious);
        }
    }

    /**
     * fires when a medication is moved from previous to current medications
     *
     * @param event An action event
     */
    @FXML
    void takeMedication(ActionEvent event) {
        String med = previousMedicationListView.getSelectionModel().getSelectedItem();
        if (med == null) {
            return;
        }
        if (currentMeds.contains(med)) {
            currentUser.removePreviousMedication(med);
            previousMeds.remove(med);
            return;
        }
        currentMeds.add(med);
        currentUser.addCurrentMedication(med);
        previousMeds.remove(med);
        currentUser.removePreviousMedication(med);

    }

    /**
     * fires when a medication is moved from current to previous
     *
     * @param event An action event
     */
    @FXML
    void untakeMedication(ActionEvent event) {
        String med = currentMedicationListView.getSelectionModel().getSelectedItem();
        if (med == null) {
            return;
        }
        if (previousMeds.contains(med)) {
            currentUser.removeCurrentMedication(med);
            currentMeds.remove(med);
            return;
        }
        currentUser.removeCurrentMedication(med);
        currentMeds.remove(med);
        previousMeds.add(med);
        currentUser.addPreviousMedication(med);
    }

    /**
     *Removes the highlight of the currently selected medication
     * @param event A mouse event
     */
    @FXML
    void clearCurrentMedSelection(MouseEvent event) {
        currentMedicationListView.getSelectionModel().clearSelection();
    }

    /**
     *Removes the highlight of the previously selected medication
     * @param event A mouse event
     */
    @FXML
    void clearPreviousMedSelection(MouseEvent event) {
        previousMedicationListView.getSelectionModel().clearSelection();
    }

    /**
     *Opens the selected medication in a new window with additional information
     * @param med A string of medication
     */
    private void launchMedicationView(String med) {
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
        medicationsTimeController.init(application, currentUser, stage, med);
        stage.show();

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
     * @param event An action event.
     */
    @FXML
    void addProcedure(ActionEvent event) {
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
        MedicalProcedure procedure = new MedicalProcedure(procedureDate, procedureName, descriptionTextArea.getText(), new ArrayList<Organs>());
        medicalProcedures.add(procedure);
        if (procedure.getProcedureDate().isBefore(LocalDate.now())) {
            previousProcedures.add(procedure);
        } else {
            pendingProcedures.add(procedure);
        }
        clearProcedure();
        application.update(currentUser);
    }

    /**
     * Updates an existing procedures information
     */
    @FXML
    void updateProcedures() {
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
            previousProcedures.remove(procedure);
            updateProcedure(procedure, newName, newDate, newDescription);
        } else if (pendingProcedureTableView.getSelectionModel().getSelectedItem() != null) {
            MedicalProcedure procedure = pendingProcedureTableView.getSelectionModel().getSelectedItem();
            pendingProcedures.remove(procedure);
            updateProcedure(procedure, newName, newDate, newDescription);
        }
    }

    /**
     * * Helper function for the updateProcedures button.
     * Takes a procedure and updates it
     *
     * @param procedure      procedure to be updated
     * @param newName        new procedure name
     * @param newDate        new procedure date
     * @param newDescription new procedure description
     */
    private void updateProcedure(MedicalProcedure procedure, String newName, LocalDate newDate, String newDescription) {
        procedure.setSummary(newName);
        if (newDate == null) {

        } else {
            procedure.setProcedureDate(newDate);
        }
        procedure.setDescription(newDescription);
        if (procedure.getProcedureDate().isBefore(LocalDate.now())) {
            previousProcedures.add(procedure);
        } else {
            pendingProcedures.add(procedure);
        }
        application.update(currentUser);
    }

    /**
     * Shows all the information for a given procedure
     * @param procedure current medical procedure
     */
    private void showProcedure(MedicalProcedure procedure) {
        procedureTextField.setText(procedure.getSummary());
        procedureDateSelector.setValue(procedure.getProcedureDate());
        descriptionTextArea.setText(procedure.getDescription());
        organsAffectedByProcedureListView.setItems(FXCollections.observableList(procedure.getOrgansAffected()));
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
     * @param event passed in automatically by the gui
     */
    @FXML
    void removeProcedure(ActionEvent event) {
        if (previousProcedureTableView.getSelectionModel().getSelectedItem() != null) {
            medicalProcedures.remove(previousProcedureTableView.getSelectionModel().getSelectedItem());
            currentUser.removeMedicalProcedure(previousProcedureTableView.getSelectionModel().getSelectedItem());
            previousProcedures.remove(previousProcedureTableView.getSelectionModel().getSelectedItem());
        } else if (pendingProcedureTableView.getSelectionModel().getSelectedItem() != null) {
            medicalProcedures.remove(pendingProcedureTableView.getSelectionModel().getSelectedItem());
            currentUser.removeMedicalProcedure(pendingProcedureTableView.getSelectionModel().getSelectedItem());
            pendingProcedures.remove(pendingProcedureTableView.getSelectionModel().getSelectedItem());
        }
        application.update(currentUser);
    }

    /**
     * Opens the modify procedure organs window for the selected procedure
     */
    @FXML
    void modifyProcedureOrgans() {
        MedicalProcedure procedure = currentProcedureList.getSelectionModel().getSelectedItem();
        FXMLLoader affectedOrganLoader = new FXMLLoader(getClass().getResource("/FXML/organsAffectedView.fxml"));
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
        showProcedure(procedure);
    }

    /**
     * show the current and past diseases
     * of the donor.
     */
    public void showDonorDiseases(User user, boolean init) {
        if (user.getCurrentDiseases().size() != 0) {
            currentDisease = FXCollections.observableList(user.getCurrentDiseases());
            currentDiseaseTableView.setItems(currentDisease);

        } else {
            currentDiseaseTableView.setPlaceholder(new Label("No Current Diseases"));
        }

      if(user.getPastDiseases().size() != 0) {
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

                    if(item) {
                        setText("Chronic");
                        setTextFill(Color.RED);
                    } else {
                      setText("");
                    }
                }
            });currentDiseaseTableView.getColumns().addAll(diagnosisDateColumn, nameColumn, chronicColumn);

            TableColumn<Disease, LocalDate> diagnosisDateColumn2 = new TableColumn<>("Diagnosis Date");
            diagnosisDateColumn2.setMinWidth(110);
            diagnosisDateColumn2.setMaxWidth(110);
            diagnosisDateColumn2.setCellValueFactory(new PropertyValueFactory<>("diagnosisDate"));

            TableColumn<Disease, String> nameColumn2 = new TableColumn<>("Disease Name");
            nameColumn2.setMinWidth(305);
            nameColumn2.setMaxWidth(305);
            nameColumn2.setCellValueFactory(new PropertyValueFactory<>("name"));

          pastDiseaseTableView.getColumns().addAll(diagnosisDateColumn2, nameColumn2);

}  }

  /**
   * fires when the add button at the Disease tab is clicked
   */
  @FXML
  private void addDisease() {

    FXMLLoader addDiseaseLoader = new FXMLLoader(getClass().getResource("/FXML/createNewDisease.fxml"));
    Parent root = null;
    try {
      root = addDiseaseLoader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
    NewDiseaseController newDiseaseController = addDiseaseLoader.getController();
    Stage stage = new Stage();
    Disease disease = new Disease("", false, false, LocalDate.now());
        currentUser.addCurrentDisease(disease);newDiseaseController.init(currentUser, application, stage, disease, this);
    stage.setScene(new Scene(root));
    stage.show();
  }/*Receiver*/

    /**
     * register an organ
     * for receiver
     */
    @FXML
    public void registerOrgan () {
        if (organsComboBox.getSelectionModel().getSelectedItem() != null) {
          Organs toRegister = organsComboBox.getSelectionModel().getSelectedItem();
          if (!currentlyReceivingListView.getItems().contains(toRegister)) {
            currentUser.getReceiverDetails().startWaitingForOrgan(toRegister);
            currentlyRecieving.add(toRegister);
            organsComboBox.getItems().remove(toRegister);
            organsComboBox.setValue(null);
            application.update(currentUser);
            if (currentUser.getDonorDetails().getOrgans().contains(toRegister)) {
                currentUser.getCommonOrgans().add(toRegister);
            }

            //set mouse click for currentlyReceivingListView
            currentlyReceivingListView.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    Organs currentlyReceivingOrgan = currentlyReceivingListView.getSelectionModel().getSelectedItem();
                    launchReceiverOrganDateView(currentlyReceivingOrgan);
                }
            });
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
            application.getClinicianControllerInstance().populateWaitListTable();

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
                    Organs currentlyReceivingOrgan = currentlyReceivingListView.getSelectionModel().getSelectedItem();
                    launchReceiverOrganDateView(currentlyReceivingOrgan);
                }
            });
        }

        currentlyDonating.refresh();
        currentlyReceivingListView.refresh();
    }

    /**
     * opens the deregister organ reason window when the
     * deregister button at the Receiver tab is clicked
     */
    @FXML
    private void deregisterOrganReason () {
        Organs toDeRegister = currentlyReceivingListView.getSelectionModel().getSelectedItem();
        if (toDeRegister != null) {
            FXMLLoader deregisterOrganReasonLoader = new FXMLLoader(getClass().getResource("/FXML/deregisterOrganReasonView.fxml"));
            Parent root = null;
            try {
                root = deregisterOrganReasonLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            DeregisterOrganReasonController deregisterOrganReasonController = deregisterOrganReasonLoader.getController();
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
            notReceivingListView.getItems().add(toDeRegister);
            currentUser.getReceiverDetails().stopWaitingForOrgan(toDeRegister);
            currentlyReceivingListView.getItems().remove(toDeRegister);
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
                    Organs currentlyReceivingOrgan = notReceivingListView.getSelectionModel().getSelectedItem();
                    launchReceiverOrganDateView(currentlyReceivingOrgan);
                }
            });

            currentlyDonating.refresh();
            currentlyReceivingListView.refresh();
        }
    }

    /**
     * Launch the time table which shows the
     * register and deregister date of a particular organ
     *
     * @param organs enum
     */
    private void launchReceiverOrganDateView(Organs organs) {
        FXMLLoader receiverOrganDateViewLoader = new FXMLLoader(getClass().getResource("/FXML/receiverOrganDateView.fxml"));
        Parent root = null;
        try {
            root = receiverOrganDateViewLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        ReceiverOrganDateController receiverOrganDateController = receiverOrganDateViewLoader.getController();
        receiverOrganDateController.init(application, currentUser, stage, organs);
        stage.show();
    }

  /**
   * Moves selected organ from donatable to currently donating
   * @param event passed in automatically by the gui
   */
  @FXML
  void donate(ActionEvent event) {

    UndoRedoStacks.storeUndoCopy(currentUser);
    if (!canDonate.getSelectionModel().isEmpty()){
      Organs toDonate = canDonate.getSelectionModel().getSelectedItem();
      currentlyDonating.getItems().add(toDonate);
      currentUser.getDonorDetails().addOrgan(toDonate);
      if (currentUser.getReceiverDetails().getOrgans().containsKey(toDonate)) {
          currentUser.getCommonOrgans().add(toDonate);
      }
      application.update(currentUser);
      canDonate.getItems().remove(toDonate);
    }
      currentlyDonating.refresh();
    currentlyReceivingListView.refresh();
  }

  /**
   * Moves selected organ from currently donating to donatable
   * @param event passed in automatically by the gui
   */
  @FXML
  void undonate(ActionEvent event) {
    UndoRedoStacks.storeUndoCopy(currentUser);
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
    }

      currentlyDonating.refresh();
      currentlyReceivingListView.refresh();
  }
    /**
     * Checks if a disease is selected in either 'Past' or 'Current' tables. If so, it passes that into NewDiseaseController
     * to open up the 'disease editor' window. NewDiseaseController should probably be renamed to diseaseEditor
     */
    @FXML
    private void updateDisease() {

        FXMLLoader addDiseaseLoader = new FXMLLoader(getClass().getResource("/FXML/createNewDisease.fxml"));
        Parent root = null;
        try {
            root = addDiseaseLoader.load();
            root.requestFocus(); //Currently the below code thinks that focus = selected so will always take the focused
            // thing in currentDiseases over the selected thing in pastDiseases. Trying to fix
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (currentDiseaseTableView.getSelectionModel().getSelectedItem() != null) { //Might error, dunno what it returns if nothing is selected, hopefully -1?
            Disease disease = currentDiseaseTableView.getSelectionModel().getSelectedItem(); //Get the selected disease

            NewDiseaseController newDiseaseController = addDiseaseLoader.getController(); //Load some stuff
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
            if(!currentDiseaseTableView.getSelectionModel().getSelectedItem().getIsChronic()){
                currentUser.getCurrentDiseases().remove(currentDiseaseTableView.getSelectionModel().getSelectedItem());
            } else {
                return;
            }
        } else if (pastDiseaseTableView.getSelectionModel().getSelectedIndex() >= 0) {
            currentUser.getPastDiseases().remove(pastDiseaseTableView.getSelectionModel().getSelectedItem());
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
        Collections.sort(currentUser.getCurrentDiseases(), disease.diseaseDateComparator);
        Collections.sort(currentUser.getPastDiseases(), disease.diseaseNameComparator);
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
        getCurrentDiseaseTableView().refresh();
        getPastDiseaseTableView().refresh();
    }

    /**
     * Closes current window.
     */
    @FXML
    private void closeWindow(){
      application.update(currentUser);stage.close();
    }

}
