package odms.controller.gui.panel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import odms.commons.model.Change;
import odms.commons.model.ReceiverDetails;
import odms.commons.model.User;
import odms.commons.model._enum.OrganDeregisterReason;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.OrgansWithDates;
import odms.commons.model.datamodel.ReceiverOrganDetailsHolder;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.popup.ReceiverOrganDateController;
import odms.controller.gui.window.UserController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class ReceiverTabController {

    @FXML
    private ComboBox<Organs> organsComboBox;

    @FXML
    private Label organLabel;

    @FXML
    private TableView<OrgansWithDates> currentlyWaitingFor;

    @FXML
    private TableView<OrgansWithDates> noLongerWaitingForOrgan;

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
    private UserController parent;

    private ObservableList<OrgansWithDates> currentlyRecieving;
    private OrganDeregisterReason organDeregisterationReason;

    /**
     * Gives the user view the application controller and hides all label and buttons that are not
     * needed on opening
     *
     * @param controller    the application controller
     * @param user          the current user
     * @param fromClinician boolean value indication if from clinician view
     * @param parent        the UserController class this belongs to
     */
    public void init(AppController controller, Stage stage, User user, boolean fromClinician,
                     UserController parent) {
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

            currentlyReceivingLabel.setVisible(false);
            notReceivingLabel.setVisible(false);
            currentlyWaitingFor.setVisible(false);
            noLongerWaitingForOrgan.setVisible(false);

            if (user.getReceiverDetails().getOrgans().isEmpty()) {
                notReceiverLabel.setVisible(true);
            } else {
                currentlyReceivingLabel.setVisible(true);
                notReceivingLabel.setVisible(true);
                currentlyWaitingFor.setVisible(true);
                noLongerWaitingForOrgan.setVisible(true);
            }
        }

        //display registered and deregistered receiver organs if any

        TableColumn currentOrganNameColumn = new TableColumn("Organ");
        TableColumn currentOrganDateColumn = new TableColumn("Date");
        TableColumn noLongerOrganNameColumn = new TableColumn("Organ");
        TableColumn noLongerOrganDateColumn = new TableColumn("Date");
        currentOrganNameColumn.setCellValueFactory(new PropertyValueFactory<OrgansWithDates,String>("organName"));
        noLongerOrganNameColumn.setCellValueFactory(new PropertyValueFactory<OrgansWithDates,String>("organName"));
        currentOrganDateColumn.setCellValueFactory(new PropertyValueFactory<OrgansWithDates,LocalDate>("latestRegistration"));
        noLongerOrganDateColumn.setCellValueFactory(new PropertyValueFactory<OrgansWithDates,LocalDate>("latestRegistration"));
        currentlyWaitingFor.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        noLongerWaitingForOrgan.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        currentlyWaitingFor.getColumns().addAll(currentOrganNameColumn,currentOrganDateColumn);
        noLongerWaitingForOrgan.getColumns().addAll(noLongerOrganNameColumn,noLongerOrganDateColumn);
        currentlyWaitingFor.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        populateReceiverLists(currentUser);


    }

    /**
     * register an organ* for receiver
     */
    @FXML
    public void registerOrgan() {
        if (organsComboBox.getSelectionModel().getSelectedItem() != null) {
            Organs toRegister = organsComboBox.getSelectionModel().getSelectedItem();
            currentUser.getReceiverDetails().startWaitingForOrgan(toRegister);
            currentlyRecieving.add(new OrgansWithDates(toRegister, LocalDate.now()));
            organsComboBox.getItems().remove(toRegister);
            organsComboBox.setValue(null);// reset the combobox
            application.update(currentUser);
            if (currentUser.getDonorDetails().getOrgans().contains(toRegister)) {
                currentUser.getCommonOrgans().add(toRegister);

                //set mouse click for currentlyWaitingFor
/*                currentlyWaitingFor.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                        Organs currentlyReceivingOrgan = currentlyWaitingFor.getSelectionModel()
                                .getSelectedItem();
                        launchReceiverOrganDateView(currentlyReceivingOrgan);
                    }
                });*/
                Log.info("Successfully registered organ:" + toRegister.toString() + " for receiver NHI: " + currentUser.getNhi());
            } else {
                Log.warning("Unable to register organ for receiver as organ: " + toRegister.toString() + " has already been registered for receiver NHI: " + currentUser.getNhi());
            }
            parent.updateUndoRedoButtons();
            parent.refreshCurrentlyDonating();
            currentlyWaitingFor.refresh();
        } else {
            Log.warning("Unable to register organ: null for receiver NHI: " + currentUser.getNhi());
        }
    }

    /**
     * re-register an organ for receiver
     */
    @FXML
    public void reRegisterOrgan() {
        OrgansWithDates toReRegister = noLongerWaitingForOrgan.getSelectionModel().getSelectedItem();
        if (toReRegister != null) {
            toReRegister.setLatestRegistration(LocalDate.now());
            currentlyWaitingFor.getItems().add(toReRegister);
            currentUser.getReceiverDetails().startWaitingForOrgan(toReRegister.getOrganName());
            noLongerWaitingForOrgan.getItems().remove(toReRegister);
            Log.info("Successfully re-registered organ:" + toReRegister.toString() + " for receiver NHI: " + currentUser.getNhi());
            if (currentUser.getReceiverDetails().isDonatingThisOrgan(toReRegister.getOrganName())) {
                currentUser.getCommonOrgans().add(toReRegister.getOrganName());
            }

            //if notReceiving list view is empty, disable mouse click to prevent null pointer exception
            if (noLongerWaitingForOrgan.getItems().isEmpty()) {
                noLongerWaitingForOrgan.setOnMouseClicked(null);
            }
            //set mouse click for currentlyWaitingFor
/*            currentlyWaitingFor.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    Organs currentlyReceivingOrgan = currentlyWaitingFor.getSelectionModel()
                            .getSelectedItem();
                    launchReceiverOrganDateView(currentlyReceivingOrgan);
                }
            });*/
            parent.updateUndoRedoButtons();
        } else {
            Log.warning("Unable to re-register organ: null for receiver as no organ selected for receiver NHI: " + currentUser.getNhi());
        }

        parent.refreshCurrentlyDonating();
        currentlyWaitingFor.refresh();
    }

    /**
     * opens the deregister organ reason window when the deregister button at the Receiver tab is
     * clicked
     */
    @FXML
    private void deregisterOrganReason() {
/*        OrgansWithDates toDeRegister = currentlyWaitingFor.getSelectionModel().getSelectedItem();
        if (toDeRegister != null) {
            FXMLLoader deregisterOrganReasonLoader = new FXMLLoader(
                    getClass().getResource("/FXML/deregisterOrganReasonView.fxml"));
            Parent root;
            try {
                root = deregisterOrganReasonLoader.load();
                DeregisterOrganReasonController deregisterOrganReasonController = deregisterOrganReasonLoader
                        .getController();
                Stage stage = new Stage();
                deregisterOrganReasonController.init(toDeRegister, parent, currentUser, application, stage);
                stage.setScene(new Scene(root));
                stage.show();
                Log.info("Successfully launched deregister organ reason window for receiver NHI: " + currentUser.getNhi());
            } catch (IOException e) {
                Log.severe("unable to launch deregister organ reason window for receiver NHI: " + currentUser.getNhi(), e);
            }
        }*/
    }

    /**
     * Populates the receiver list of the user
     */
    public void populateReceiverLists(User user) {
        currentUser = user;
        ArrayList<Organs> organs = new ArrayList<>();
        Collections.addAll(organs, Organs.values());
        Map<Organs, ArrayList<ReceiverOrganDetailsHolder>> receiverOrgans = user.getReceiverDetails().getOrgans();

        if (receiverOrgans == null) {
            receiverOrgans = new EnumMap<>(Organs.class);
        }
        currentlyRecieving = FXCollections.observableArrayList(getOrgansAndDates(user.getReceiverDetails(), true));
        ObservableList<OrgansWithDates> noLongerReceiving = FXCollections.observableArrayList(getOrgansAndDates(user.getReceiverDetails(), false));


        currentlyWaitingFor.setItems(currentlyRecieving);
        noLongerWaitingForOrgan.setItems(noLongerReceiving);
        organsComboBox.setItems(FXCollections.observableList(organs));
/*

        if (!noLongerWaitingForOrgan.getItems().isEmpty()) {
            openOrganFromDoubleClick(noLongerWaitingForOrgan.getSelectionModel());
        }

        if (!currentlyWaitingFor.getItems().isEmpty()) {
            openOrganFromDoubleClick(currentlyWaitingFor);
        }
*/


        //if user already died, user cannot receive organs
        if (currentUser.getDeceased()) {
            registerButton.setDisable(true);
            reRegisterButton.setDisable(true);
        }
    }

    /**
     * Opens the selected organ from a doubleClick event
     *
     * @param list A ListView object to add the
     */
    private void openOrganFromDoubleClick(TableView<Organs> list) {
        list.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Organs currentlyReceivingOrgan = list.getSelectionModel().getSelectedItem();
                launchReceiverOrganDateView(currentlyReceivingOrgan);
            }
        });
    }

    /**
     * Refreshes the currently Receiving List
     */
    public void refreshCurrentlyReceiving() {
        currentlyWaitingFor.refresh();
    }

    public boolean currentlyReceivingContains(Organs toDonate) {
        return currentlyRecieving.contains(toDonate);
    }

    /**
     * Launch the time table which shows the register and deregister date of a particular organ
     *
     * @param organs Organ to show extra details for
     */
    private void launchReceiverOrganDateView(Organs organs) {
        FXMLLoader receiverOrganDateViewLoader = new FXMLLoader(
                getClass().getResource("/FXML/receiverOrganDateView.fxml"));
        Parent root;
        try {
            root = receiverOrganDateViewLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            ReceiverOrganDateController receiverOrganDateController = receiverOrganDateViewLoader
                    .getController();
            receiverOrganDateController.init(currentUser, stage, organs);
            stage.show();
            Log.info("Successfully launched register and deregister time table window for receiver NHI: " + currentUser.getNhi());
        } catch (IOException e) {
            Log.severe("unable to launch register and deregister time table window for receiver NHI: " + currentUser.getNhi(), e);
        }
    }

    /**
     * de-register an organ for receiver
     *
     * @param toDeRegister the organ to be removed from the
     */
    public void deRegisterOrgan(Organs toDeRegister) {
        if (toDeRegister != null) {

            if (organDeregisterationReason == OrganDeregisterReason.TRANSPLANT_RECEIVED) {
                currentUser.getReceiverDetails().stopWaitingForOrgan(toDeRegister, OrganDeregisterReason.TRANSPLANT_RECEIVED);
                Log.info("Successfully de-registered organ:" + toDeRegister.toString() + " for receiver NHI: " + currentUser.getNhi());

            } else if (organDeregisterationReason == OrganDeregisterReason.REGISTRATION_ERROR) {
                currentUser.getReceiverDetails().stopWaitingForOrgan(toDeRegister, OrganDeregisterReason.REGISTRATION_ERROR);
                Log.info("Successfully de-registered organ:" + toDeRegister.toString() + " for receiver NHI: " + currentUser.getNhi());
                currentUser.getChanges().add(new Change(
                        "Initial registering of the organ " + toDeRegister.toString()
                                + " was an error for receiver " + currentUser.getFullName()));

            } else if (organDeregisterationReason == OrganDeregisterReason.DISEASE_CURED) {
                //refresh diseases table
                currentUser.getReceiverDetails().stopWaitingForOrgan(toDeRegister, OrganDeregisterReason.DISEASE_CURED);
                Log.info("Successfully de-registered organ:" + toDeRegister.toString() + " for receiver NHI: " + currentUser.getNhi());
                parent.refreshDiseases();

            } else if (organDeregisterationReason == OrganDeregisterReason.RECEIVER_DIED) {
                List<OrgansWithDates> currentlyReceiving = new ArrayList<>(currentlyWaitingFor.getItems());
                for (OrgansWithDates organ : currentlyReceiving) {

                    currentlyWaitingFor.getItems().remove(organ);
                    organ.setLatestRegistration(LocalDate.now());
                    noLongerWaitingForOrgan.getItems().add(organ);
                }
                currentUser.getReceiverDetails().stopWaitingForAllOrgans();
                registerButton.setDisable(true);
                reRegisterButton.setDisable(true);
            }

            if (organDeregisterationReason != OrganDeregisterReason.RECEIVER_DIED) {
                noLongerWaitingForOrgan.getItems().add(new OrgansWithDates(toDeRegister,LocalDate.now()));
                currentlyWaitingFor.getItems().remove(toDeRegister);
            }

            if (currentUser.getCommonOrgans().contains(toDeRegister)) {
                currentUser.getCommonOrgans().remove(toDeRegister);
            }

            //if currentlyWaitingFor is empty, disable mouse click to prevent null pointer exception
            if (currentlyWaitingFor.getItems().isEmpty()) {
                currentlyWaitingFor.setOnMouseClicked(null);
            }
            //set mouse click for noLongerWaitingForOrgan
/*            noLongerWaitingForOrgan.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    Organs currentlyReceivingOrgan = noLongerWaitingForOrgan.getSelectionModel()
                            .getSelectedItem();
                    launchReceiverOrganDateView(currentlyReceivingOrgan);
                }
            });*/
            parent.updateUndoRedoButtons();
            application.update(currentUser);
            parent.refreshCurrentlyDonating();
            currentlyWaitingFor.refresh();

        } else {
            Log.warning("Unable to de-register organ: null for receiver NHI: " + currentUser.getNhi());
        }
    }

    /**
     * Sets the reason for organ deregistration
     *
     * @param organDeregistrationReason OrganDeregisterReason enum
     */
    public void setOrganDeregistrationReason(OrganDeregisterReason organDeregistrationReason) {
        this.organDeregisterationReason = organDeregistrationReason;
    }

    public boolean getIsRevereSorted() {
        return false;
    }

    public boolean getIsSortedByName() {
        return false;
    }


    /**
     * Takes a receivers details and returns the list of latest dates associated with that organ specified by the boolean flag
     *
     * @param organDetails Receiver details to be dealt with
     * @param wantCurrentlyAwaiting If we are after the current organs or previous organs
     *
     * @return A list of the orgnas with the dates attached.
     */
    private List<OrgansWithDates> getOrgansAndDates(ReceiverDetails organDetails, boolean wantCurrentlyAwaiting) {
        List<OrgansWithDates> results = new ArrayList<>();
            Set<Organs> organs = organDetails.getOrgans().keySet();
            for (Organs o : organs) {
                if (organDetails.isCurrentlyWaitingFor(o) == wantCurrentlyAwaiting) {
                    LocalDate latestDate = getLatestDate(organDetails, o);
                    results.add(new OrgansWithDates(o, latestDate));
                }
            }

        return results;
    }

    private LocalDate getLatestDate(ReceiverDetails organDetails, Organs organ){
        List<LocalDate> organDates = organDetails.getOrganDates(organ);
        organDates.sort((o1, o2) -> {
            if(o1.isAfter(o2)) return 1;
            if(o1.isBefore(o2)) return -1;
            else return 0;
        });
        return organDates.get(0);
    }
}
