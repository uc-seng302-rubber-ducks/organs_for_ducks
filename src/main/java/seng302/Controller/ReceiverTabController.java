package seng302.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import seng302.Model.Change;
import seng302.Model.OrganDeregisterReason;
import seng302.Model.Organs;
import seng302.Model.User;
import seng302.Service.OrganListCellFactory;

public class ReceiverTabController {

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

  private User currentUser;
  private AppController application;
  private boolean Clinician;
  private Stage stage;
  private DonorController parent;

  private ObservableList<Organs> currentlyRecieving;
  private ObservableList<Organs> noLongerReceiving;
  private OrganDeregisterReason organDeregisterationReason;
  private boolean isSortedByName = false;
  private boolean isReverseSorted = false;

  /**
   * Gives the donor view the application controller and hides all label and buttons that are not
   * needed on opening
   *
   * @param controller the application controller
   * @param user the current user
   * @param fromClinician boolean value indication if from clinician view
   * @param parent the DonorController class this belongs to
   */
  public void init(AppController controller, Stage stage, User user, boolean fromClinician,
      DonorController parent) {
    application = controller;
    currentUser = user;
    this.stage = stage;
    this.parent = parent;
    if (fromClinician) {
      Clinician = true;
    } else {
      Clinician = false;

      organLabel.setVisible(false);
      organsComboBox.setVisible(false);
      registerButton.setVisible(false);
      reRegisterButton.setVisible(false);
      deRegisterButton.setVisible(false);
    }

    //display registered and deregistered receiver organs if any
    populateReceiverLists(currentUser);

    currentlyReceivingListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    currentlyReceivingListView
        .setCellFactory(column -> OrganListCellFactory.generateListCell(currentUser));

  }

  /**
   * register an organ* for receiver
   */
  @FXML
  public void registerOrgan() {
    if (organsComboBox.getSelectionModel().getSelectedItem() != null) {
      Organs toRegister = organsComboBox.getSelectionModel().getSelectedItem();
      AppController.getInstance().getClinicianController().refreshTables();
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
        parent.updateUndoRedoButtons();
      }

      parent.refreshCurrentlyDonating();
      currentlyReceivingListView.refresh();
    }
  }

  /**
   * re-register an organ for receiver
   */
  @FXML
  public void reRegisterOrgan() {
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
      parent.updateUndoRedoButtons();
    }

    parent.refreshCurrentlyDonating();
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
      deregisterOrganReasonController.init(toDeRegister, parent, currentUser, application, stage);
      stage.setScene(new Scene(root));
      stage.show();
    }
  }

  /**
   * Populates the receiver list of the user
   */
  public void populateReceiverLists(User user) {
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

  public void refreshCurrentlyReceiving() {
    currentlyReceivingListView.refresh();
  }

  public boolean currentlyReceivingContains(Organs toDonate) {
    return currentlyRecieving.contains(toDonate);
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
   * de-register an organ for receiver
   *
   * @param toDeRegister the organ to be removed from the
   */
  public void deRegisterOrgan(Organs toDeRegister) {
    if (toDeRegister != null) {

      if (organDeregisterationReason == OrganDeregisterReason.TRANSPLANT_RECEIVED) {
        currentUser.getReceiverDetails().stopWaitingForOrgan(toDeRegister);

      } else if (organDeregisterationReason == OrganDeregisterReason.REGISTRATION_ERROR) {
        currentUser.getReceiverDetails().stopWaitingForOrgan(toDeRegister);
        currentUser.getChanges().add(new Change(
            "Initial registering of the organ " + toDeRegister.organName
                + " was an error for receiver " + currentUser.getFullName()));

      } else if (organDeregisterationReason == OrganDeregisterReason.DISEASE_CURED) {
        //refresh diseases table
        currentUser.getReceiverDetails().stopWaitingForOrgan(toDeRegister);
        parent.refreshDiseases(this.getIsSortedByName(), this.getIsRevereSorted());

      } else if (organDeregisterationReason == OrganDeregisterReason.RECEIVER_DIED) {
        List<Organs> currentlyReceiving = new ArrayList<>(currentlyReceivingListView.getItems());
        for (Organs organ : currentlyReceiving) {
          notReceivingListView.getItems().add(organ);
          currentlyReceivingListView.getItems().remove(organ);
        }
        currentUser.getReceiverDetails().stopWaitingForAllOrgans();
        registerButton.setDisable(true);
        reRegisterButton.setDisable(true);
      }

      if (organDeregisterationReason != OrganDeregisterReason.RECEIVER_DIED) {
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
      parent.updateUndoRedoButtons();
      application.update(currentUser);
      parent.refreshCurrentlyDonating();
      currentlyReceivingListView.refresh();
      AppController.getInstance().getClinicianController().refreshTables();
    }
  }

  /**
   * Sets the reason for organ deregistration
   *
   * @param organDeregisterationReason OrganDeregisterReason enum
   */
  public void setOrganDeregisterationReason(OrganDeregisterReason organDeregisterationReason) {
    this.organDeregisterationReason = organDeregisterationReason;
  }

  public boolean getIsRevereSorted() {
    return isReverseSorted;
  }

  public boolean getIsSortedByName() {
    return isSortedByName;
  }
}