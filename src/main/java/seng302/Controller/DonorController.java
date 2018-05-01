package seng302.Controller;


import java.time.LocalDate;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.temporal.ChronoUnit;

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
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import okhttp3.OkHttpClient;
import org.controlsfx.control.textfield.TextFields;
import okhttp3.OkHttpClient;
import seng302.Model.*;
import java.io.IOException;
import java.util.*;

public class DonorController {

    //the Home page attributes
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

    private AppController application;
    private ObservableList<String> currentMeds;
    private ObservableList<String> previousMeds;
    private ObservableList<MedicalProcedure> medicalProcedures;
    private ObservableList<MedicalProcedure> previousProcedures;
    private ObservableList<MedicalProcedure> pendingProcedures;

  private ObservableList<Disease> currentDisease;
  private ObservableList<Disease> pastDisease;private List<String> possibleGenders = Arrays.asList("M", "F", "U");

    private List<String> possibleBloodTypes = Arrays
            .asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "U");

    private User currentUser;
    private Stage stage;
    private EmergencyContact contact = null;
    private ObservableList<Change> changelog;
    private OkHttpClient client = new OkHttpClient();

    /**
     * Gives the donor view the application controller and hides all label and buttons that are not
     * needed on opening
     */
    public void init(AppController controller, User user, Stage stage, Boolean fromClinician) {

    this.stage = stage;
    application = controller;
    //ageValue.setText("");
    if (fromClinician) {
      logOutButton.setVisible(false);
    addDiseaseButton.setVisible(true);
      updateDiseaseButton.setVisible(true);
      deleteDiseaseButton.setVisible(true);}else {
            procedureDateSelector.setEditable(false);
            procedureTextField.setEditable(false);
            descriptionTextArea.setEditable(false);
            addProcedureButton.setVisible(false);
            removeProcedureButton.setVisible(false);
            updateProceduresButton.setVisible(false);
            modifyOrgansProcedureButton.setVisible(false);
        }
    //arbitrary default values
    //changeDeceasedStatus();
    undoButton.setVisible(true);
    redoButton.setVisible(true);
    //warningLabel.setVisible(false);
    currentUser = user;
    contact = user.getContact();
      donorNameLabel.setText(user.getName());
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
                    System.out.println(selected);
                    displayDetails(selected);
                }));
        currentMedicationListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    String med = currentMedicationListView.getSelectionModel().getSelectedItem();
                    launchMedicationView(med);
                }
            }
        });
        previousMedicationListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    String med = previousMedicationListView.getSelectionModel().getSelectedItem();
                    launchMedicationView(med);
                }
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
            ArrayList<Change> changes = currentUser.getChanges();
            if (changes != null) { // checks if the changes are null in case the user is a new user
                changelog = FXCollections.observableArrayList(changes);
            }
            changelog.addListener((ListChangeListener.Change<? extends Change> change) -> historyTableView.setItems(changelog));
            showDonorHistory();
        } else {
            changelog = FXCollections.observableArrayList(new ArrayList<Change>());
        }
        //System.out.println(changelog);
        changelog.addListener((ListChangeListener.Change<? extends Change> change) -> historyTableView.setItems(changelog));
        medicationTextField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                getDrugSuggestions();
            }
        });
        medicationTextField.textProperty().addListener((observable) -> getDrugSuggestions());

    showDonorDiseases(currentUser, true);
        modifyOrgansProcedureButton.setVisible(false);

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
   *
   * Creates a alert pop up to confirm that the user wants to delete the profile
   *
   */
  @FXML
  public void delete(ActionEvent actionEvent) throws IOException {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setContentText("Are you sure you want to delete this user?");
    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == ButtonType.OK) {
      application.deleteDonor(currentUser);
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
      stage.setTitle("");
      stage.setWidth(600);
      stage.setHeight(420);
      stage.show();

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
        stage.initModality(Modality.APPLICATION_MODAL);
        organController.init(currentUser, application, stage);
        stage.setScene(new Scene(root));
        stage.show();
        showUser(currentUser);
    }

    /**
     * @param actionEvent An action event.
     */
    @FXML
    private void updateDetails(ActionEvent actionEvent) throws IOException, InterruptedException {
        FXMLLoader updateLoader = new FXMLLoader(getClass().getResource("/FXML/updateUser.fxml"));
        Parent root = null;
        System.out.println(updateLoader);
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

//    @FXML 22.9kg/m2
//    private void changeDeceasedStatus() {
//        if (!isDonorDeceasedCheckBox.isSelected()) {
//            dateOfDeathPicker.setVisible(false);
//            dodLabel.setVisible(false);
//        } else {
//            dodLabel.setVisible(true);
//            dateOfDeathPicker.setVisible(true);
//        }
//
//    }

//    /**
//     * fires when the Misc button is clicked
//     */
//    @FXML
//    private void modifyMiscAttributes() {
//        if (currentUser.getDateOfBirth() == null) {
//            warningLabel.setVisible(true);
//            warningLabel.setText("Plese confirm donor before continuing");
//            return;
//        }
//        FXMLLoader attributeLoader = new FXMLLoader(
//                getClass().getResource("/FXML/miscAttributes.fxml"));
//        Parent root = null;
//        try {
//            root = attributeLoader.load();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        MiscAttributesController miscAttributesController = attributeLoader.getController();
//        Stage stage = new Stage();
//        miscAttributesController.init(currentUser, application, stage);
//        stage.setScene(new Scene(root));
//        stage.show();
//        miscAttributeslistView.getItems().clear();
//        miscAttributeslistView.getItems().addAll(currentUser.getMiscAttributes());
//    }

//  /**
//   * fires when the Confirm button is clicked updates the current donor and overwrites or add it to
//   * the list of donors in the application Does not deal with organs  and misc attributes as they
//   * are confirmed in their own methods
//   */
//  @FXML
//  private void updateDonor() { 22.9kg/m2
//      UndoRedoStacks.storeUndoCopy(currentUser);
//      User oldDonor = new User();
//      UndoRedoStacks.cloneUser(currentUser,oldDonor);
//
//
//    boolean isInputValid = true;
//    warningLabel.setVisible(true);
//    warningLabel.setText("");
//    if (nameTextField.getText().length() <= 3) {
//      warningLabel.setText("Names must be longer than 3 characters");
//      return;
//    }
//    String newName = nameTextField.getText();
//    if (newName != null) {
//      currentUser.setName(newName);
//    } else {
//      warningLabel.setText("Please enter a name");
//      return;
//    }
//    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//    Date newDob = Date
//            .from(dateOfBirthPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
//    LocalDate dob = LocalDate.parse(newDob.toInstant().atZone(ZoneId.systemDefault()).format(format)); //tried to make it one line but it broke - JB
//    currentUser.setDateOfBirth(dob);
//
//    //only if weight has been entered
//    if (!weightTextField.getText().equals("")) {
//      try {
//        currentUser.setWeight(Double.parseDouble(weightTextField.getText()));
//      } catch (NumberFormatException e) {
//        warningLabel.setText("Weight must be a number");
//        return;
//      }    //System.out.println(attachedUser == null);
//    }
//    if (!heightTextField.getText().equals("")) {
//      try {
//        currentUser.setHeight(Double.parseDouble(heightTextField.getText()));
//      } catch (NumberFormatException e) {
//        warningLabel.setText("Height must be a number");
//        return;
//      }
//    }
//
//    currentUser.setCurrentAddress(currentAddressTextArea.getText());
//    currentUser.setRegion(regionTextField.getText());
//
//    String newGender = (genderComboBox.getValue());
//    if (newGender == null) {
//      newGender = "U";
//    }
//    currentUser.setGender(newGender);
//    currentUser.setBloodType(bloodTypeComboBox.getValue());
//    currentUser.setDeceased(isDonorDeceasedCheckBox.isSelected())    //System.out.println(attachedUser == null);;
//    if (isDonorDeceasedCheckBox.isSelected()) {
//      Date newDod = Date.from(dateOfDeathPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
//      LocalDate dod = LocalDate.parse(newDod.toInstant().atZone(ZoneId.systemDefault()).format(format));
//
//      if (dod.isBefore(dob)) {
//        warningLabel.setVisible(true);
//        warningLabel.setText("Date of death must be after date of birth");
//        //dod must be set for other functions to work.
//        //using the best guess based on input
//        currentUser.setDateOfDeath(dod);
//        return;
//      }
//      currentUser.setDateOfDeath(dod);
//    } else {
//      currentUser.setDateOfDeath(null);
//    }
//
//    if (isInputValid) {
//      application.update(currentUser);
//      ArrayList<Change> diffs = application.differanceInDonors(oldDonor, currentUser);    organsDonatingListView.getItems().clear();
//    organsDonatingListView.getItems().addAll(currentUser.getDonorDetails().getOrgans());
    //  changelog.addAll(diffs);
//    }
//
//    showUser(currentUser);
//  }

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

    UndoRedoStacks.clearStacks();
  }

    /**
     *
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
      //System.out.println("current: " +currentMeds);currentMeds.clear();
      currentMeds.addAll(currentUser.getCurrentMedication());

            currentMedicationListView.setItems(currentMeds);
        }
        if (currentUser.getPreviousMedication() != null) {
            //System.out.println("previous: " + previousMeds);
            previousMeds.clear();
            previousMeds.addAll(currentUser.getPreviousMedication());
            previousMedicationListView.setItems(previousMeds);
        }
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
      //System.out.println("previous: " + previousMeds);
      previousMeds.clear();
      previousMeds.addAll(currentUser.getPreviousMedication());
      previousMedicationListView.setItems(previousMeds);
    }
    //organsDonatingListView.getItems().clear();
    //organsDonatingListView.getItems().addAll(currentUser.getDonorDetails().getOrgans());
    setContactPage();
    if (user.getLastName() != null) {
      stage.setTitle("User Profile: " + user.getFirstName() + " " + user.getLastName());
    } else {
      stage.setTitle("User Profile: " + user.getFirstName());

    }
  }

    /**
     *
     * @param event An action event
     */
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

    /**
     *
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
     *
     * @param event A mouse event
     */
    @FXML
    void clearCurrentMedSelection(MouseEvent event) {
        currentMedicationListView.getSelectionModel().clearSelection();
    }

    /**
     *
     * @param event A mouse event
     */
    @FXML
    void clearPreviousMedSelection(MouseEvent event) {
        previousMedicationListView.getSelectionModel().clearSelection();
    }

    /**
     *
     * @param med A string of medication
     */
    private void launchMedicationView(String med){
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


    private void showDonorHistory() {
        TableColumn timeColumn = new TableColumn("Time");
        TableColumn changeColumn = new TableColumn("Change");
        timeColumn.setCellValueFactory(new PropertyValueFactory<Change, String>("time"));
        changeColumn.setCellValueFactory(new PropertyValueFactory<Change, String>("change"));
        historyTableView.setItems(changelog);
        historyTableView.getColumns().addAll(timeColumn, changeColumn);

    }


    @FXML
    void addProcedure(ActionEvent event) {
        String procedureName = procedureTextField.getText();
        if (procedureName.isEmpty()){
            procedureWarningLabel.setText("A name must be entered for a procedure");
            return;
        }
        LocalDate procedureDate = procedureDateSelector.getValue();
        if(procedureDate == null){
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

    @FXML
    void updateProcedures() {
        procedureWarningLabel.setText("");
        String newName = procedureTextField.getText();
        LocalDate newDate = procedureDateSelector.getValue();
        String newDescription = descriptionTextArea.getText();
        if (newName.isEmpty()){
            procedureWarningLabel.setText("A name must be entered for a procedure");
            return;
        }
        if (newDate == null){
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
        if (newDate == null){

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

    private void showProcedure(MedicalProcedure procedure) {
        procedureTextField.setText(procedure.getSummary());
        procedureDateSelector.setValue(procedure.getProcedureDate());
        descriptionTextArea.setText(procedure.getDescription());
        organsAffectedByProcedureListView.setItems(FXCollections.observableList(procedure.getOrgansAffected()));
    }

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
      if(user.getCurrentDiseases().size() != 0) {
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

      if(init) {
          TableColumn<Disease, LocalDate> diagnosisDateColumn = new TableColumn<>("Diagnosis Date");
          diagnosisDateColumn.setMinWidth(140);
          diagnosisDateColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosisDate"));

          TableColumn<Disease, String> nameColumn = new TableColumn<>("Disease Name");
          nameColumn.setMinWidth(285);
          nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

          currentDiseaseTableView.getColumns().addAll(diagnosisDateColumn, nameColumn);

          TableColumn<Disease, LocalDate> diagnosisDateColumn2 = new TableColumn<>("Diagnosis Date");
          diagnosisDateColumn2.setMinWidth(140);
          diagnosisDateColumn2.setCellValueFactory(new PropertyValueFactory<>("diagnosisDate"));

          TableColumn<Disease, String> nameColumn2 = new TableColumn<>("Disease Name");
          nameColumn2.setMinWidth(285);
          nameColumn2.setCellValueFactory(new PropertyValueFactory<>("name"));

          pastDiseaseTableView.getColumns().addAll(diagnosisDateColumn2, nameColumn2);
      }
  }

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
    newDiseaseController.init(currentUser, application, stage);
    stage.setScene(new Scene(root));
    stage.show();
  }

  /**
   * @param event passed in automatically by the gui
   */
  @FXML
  void donate(ActionEvent event) {
    UndoRedoStacks.storeUndoCopy(currentUser);
    if (!canDonate.getSelectionModel().isEmpty()){
      Organs toDonate = canDonate.getSelectionModel().getSelectedItem();
      currentlyDonating.getItems().add(toDonate);
      currentUser.getDonorDetails().addOrgan(toDonate);
      application.update(currentUser);
      canDonate.getItems().remove(toDonate);
    }
  }

  /**
   * @param event passed in automatically by the gui
   */
  @FXML
  void undonate(ActionEvent event) {
    UndoRedoStacks.storeUndoCopy(currentUser);
    if (!currentlyDonating.getSelectionModel().isEmpty()) {
      Organs toUndonate = currentlyDonating.getSelectionModel().getSelectedItem();
      currentlyDonating.getItems().remove(toUndonate);
      canDonate.getItems().add(toUndonate);
      currentUser.getDonorDetails().removeOrgan(toUndonate);
      application.update(currentUser);
    }
  }
}
