package seng302.Controller;


import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import seng302.Model.Change;
import seng302.Model.EmergencyContact;
import seng302.Model.OrganDeregisterReason;
import seng302.Model.Organs;
import seng302.Model.User;

/**
 * Class for the functionality of the User view of the application
 */
public class UserController {

// the contact page attributes

  //declaring all variables for the contacts page
  //<editor-fold desc="FXML declarations">.

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

  @FXML
  private Button undoButton;

  @FXML
  private Button redoButton;

  @FXML
  private TableView<Change> historyTableView;

  private AppController application;

  @FXML
  private UserOverviewController userProfileTabPageController;

  @FXML
  private MedicationTabController medicationTabPageController;

  @FXML
  private ProcedureTabController procedureTabPageController;

  @FXML
  private DonationTabPageController donationTabPageController;

  @FXML
  private DiseasesTabPageController diseasesTabPageController;

  @FXML
  private ReceiverTabController receiverTabPageController;

  @FXML
  private statusBarController statusBarPageController;

  //</editor-fold>


  private User currentUser;
  private Stage stage;
  private EmergencyContact contact = null;
  private ObservableList<Change> changelog;

  /**
   * Gives the user view the application controller and hides all label and buttons that are not
   * needed on opening
   *
   * @param controller    the application controller
   * @param user          the current user
   * @param stage         the application stage
   * @param fromClinician boolean value indication if from clinician view
   */
  public void init(AppController controller, User user, Stage stage, boolean fromClinician,
      Collection<PropertyChangeListener> parentListeners) {
    //add change listeners of parent controllers to the current user
    if (parentListeners != null && !parentListeners.isEmpty()) {
      for (PropertyChangeListener listener : parentListeners) {
        user.addPropertyChangeListener(listener);
      }
    }
    this.stage = stage;
    application = controller;

    // This is the place to set visible and invisible controls for Clinician vs User
    medicationTabPageController.init(controller, user, fromClinician);
    procedureTabPageController.init(controller, user, fromClinician, this);
    donationTabPageController.init(controller, user, this);
    diseasesTabPageController.init(controller, user, fromClinician, this);
    receiverTabPageController.init(controller, this.stage, user, fromClinician, this);
    statusBarPageController.init(controller,this);
    //arbitrary default values
    undoButton.setVisible(true);
    redoButton.setVisible(true);
    //warningLabel.setVisible(false);
    changeCurrentUser(user);
    // Sets the button to be disabled
    updateUndoRedoButtons();


    //showUser(currentUser);



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




    //init receiver organs combo box

    stage.onCloseRequestProperty().setValue(event -> {
      if (fromClinician) {
        if (application.getClinicianController() != null) {
          application.getClinicianController().refreshTables();
        }
      }
    });
    userProfileTabPageController.init(controller, user, this.stage, fromClinician);
  }

  public void refreshDiseases() {
    diseasesTabPageController.diseaseRefresh(this.getIsSortedByName(), this.getIsRevereSorted());
  }

  /**
   * Sets the reason for organ deregistration
   * @param organDeregisterationReason OrganDeregisterReason enum
   */
  public void setOrganDeregisterationReason(OrganDeregisterReason organDeregisterationReason) {
    receiverTabPageController.setOrganDeregistrationReason(organDeregisterationReason);
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
    showUser(currentUser);

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
    donationTabPageController.populateOrganLists(user);
    receiverTabPageController.populateReceiverLists(user);

    procedureTabPageController.updateProcedureTables(user);
    if (user.getLastName() != null) {
      stage.setTitle("User Profile: " + user.getFirstName() + " " + user.getLastName());
    } else {
      stage.setTitle("User Profile: " + user.getFirstName());

    }
    updateUndoRedoButtons();
    if (changelog.size() > 0){
      statusBarPageController.updateStatus(user.getNhi() +" " + changelog.get(0).getChange());
    }
  }

  /**
   * Public method to refresh the history table
   */
  public void refreshHistoryTable() {
    historyTableView.refresh();
  }

  /**
   * Shows the history of the Users profile such as added and removed information
   */
  private void showDonorHistory() {
    TableColumn<Change, String> timeColumn = new TableColumn<>("Time");
    TableColumn<Change, String> changeColumn = new TableColumn<>("Change");
    timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
    changeColumn.setCellValueFactory(new PropertyValueFactory<>("change"));
    historyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    historyTableView.setItems(changelog);
    historyTableView.getColumns().addAll(timeColumn, changeColumn);
  }

  /**
   * Public method to clear medications in the medication tab
   */
  public void clearMeds() {
    medicationTabPageController.clearPreviousMeds();
  }


  public void refreshCurrentlyDonating() {
    donationTabPageController.refreshCurrentlyDonating();
  }

  /*Receiver*/
  public void deRegisterOrgan(Organs todeRegister) {
    receiverTabPageController.deRegisterOrgan(todeRegister);
  }

  public boolean currentlyReceivingContains(Organs toDonate) {
    return receiverTabPageController.currentlyReceivingContains(toDonate);
  }

  public void refreshCurrentlyReceivingList() {
    receiverTabPageController.refreshCurrentlyReceiving();
  }

  /**
   * Updates the disabled property of the undo/redo buttons
   */
  public void updateUndoRedoButtons() {
    undoButton.setDisable(currentUser.getUndoStack().isEmpty());
    redoButton.setDisable(currentUser.getRedoStack().isEmpty());
  }

  public void showDonorDiseases(User user, boolean init) {
    diseasesTabPageController.showUserDiseases(user, init);
  }

  private boolean getIsRevereSorted() {
    return receiverTabPageController.getIsRevereSorted();
  }

  private boolean getIsSortedByName() {
    return receiverTabPageController.getIsSortedByName();
  }

    public void diableLogout(){
      userProfileTabPageController.disableLogout();
    }
}
