package odms.controller.gui.window;


import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import odms.commons.model.Change;
import odms.commons.model.EmergencyContact;
import odms.commons.model.User;
import odms.commons.model._enum.EventTypes;
import odms.commons.model._enum.OrganDeregisterReason;
import odms.commons.model._enum.Organs;
import odms.commons.model.event.UpdateNotificationEvent;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.StatusBarController;
import odms.controller.gui.UnsavedChangesAlert;
import odms.controller.gui.panel.*;
import odms.controller.gui.panel.view.UserAppointmentViewController;
import odms.controller.gui.popup.UserAppointmentAlertController;
import odms.socket.ServerEventNotifier;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static odms.commons.utils.PhotoHelper.deleteTempDirectory;

/**
 * Class for the functionality of the User view of the application
 */
public class UserController implements PropertyChangeListener {

// the contact page attributes

    //declaring all variables for the contacts page
    //<editor-fold desc="FXML declarations">
    @FXML
    private Label pCellPhone;
    @FXML
    private Label pHomePhone;
    @FXML
    private Label pAddress;
    @FXML
    private Label city;
    @FXML
    private Label zipCode;
    @FXML
    private Label country;
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
    private Label ecCity;
    @FXML
    private Label ecZipCode;
    @FXML
    private Label ecCountry;
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

    @FXML
    private MenuItem saveUserMenuItem;

    @FXML
    private MenuItem logoutUser;

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
    private StatusBarController statusBarPageController;

    @FXML
    private UserAppointmentViewController appointmentTabPageController; //</editor-fold>

    private User currentUser;
    private boolean fromClinician;
    private Stage stage;
    private EmergencyContact contact = null;
    private ObservableList<Change> changelog;
    private UserAppointmentAlertController userAppointmentAlertController = new UserAppointmentAlertController();

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
        if (user == null) {
            return;
        }
        //add change listeners of parent controllers to the current user
        if (parentListeners != null && !parentListeners.isEmpty()) {
            for (PropertyChangeListener listener : parentListeners) {
                user.addPropertyChangeListener(listener);
            }
        }
        this.stage = stage;
        application = controller;
        this.fromClinician = fromClinician;
        stage.setMinWidth(1200);
        stage.setMinHeight(800);
        changeCurrentUser(user);
        stage.setMaximized(true);

        // This is the place to set visible and invisible controls for Clinician vs User
        medicationTabPageController.init(controller, user, fromClinician, this);
        procedureTabPageController.init(controller, user, fromClinician, this);
        donationTabPageController.init(controller, user, this);
        diseasesTabPageController.init(controller, user, fromClinician, this);
        receiverTabPageController.init(controller, this.stage, user, fromClinician, this);
        appointmentTabPageController.init(user);
        statusBarPageController.init();
        //arbitrary default values

        undoButton.setVisible(true);
        redoButton.setVisible(true);

        // Sets the button to be disabled
        updateUndoRedoButtons();

        if (fromClinician) {
            logoutUser.setText("Go Back");
            logoutUser.setOnAction(e -> closeWindow());
            try {
                // ༼ つ ◕ ◕ ༽つ FIX APP ༼ つ ◕ ◕ ༽つ
                currentUser.setProfilePhotoFilePath(application.getUserBridge().getProfilePicture(user.getNhi()));
            } catch (IOException e) {
                ClassLoader classLoader = getClass().getClassLoader();
                File inFile = new File(classLoader.getResource("default-profile-picture.jpg").getFile());
                user.setProfilePhotoFilePath(inFile.getPath());
            }
        } else {
            logoutUser.setText("Log Out");
            logoutUser.setOnAction(e -> logout());
        }

        if (user.getNhi() != null) {
            showUser(currentUser); // Assumes a donor with no name is a new sign up and does not pull values from a template
            List<Change> changes = currentUser.getChanges();
            changelog = FXCollections.observableList(changes);
        } else {
            changelog = FXCollections.observableArrayList(new ArrayList<Change>());
        }

        showDonorHistory();
        changelog.addListener((ListChangeListener.Change<? extends Change> change) -> historyTableView
                .setItems(changelog));

        userProfileTabPageController.init(controller, user, this.stage, fromClinician);

        ServerEventNotifier.getInstance().addPropertyChangeListener(this);

        userAppointmentAlertController.setAppController(controller);
        userAppointmentAlertController.checkForUnseenUpdates(user.getNhi());
    }


    /**
     * Opens the update user details window
     */
    @FXML
    private void updateDetails() {
        FXMLLoader updateLoader = new FXMLLoader(getClass().getResource("/FXML/updateUser.fxml"));
        Parent root;
        try {
            root = updateLoader.load();
            UpdateUserController updateUserController = updateLoader.getController();
            Stage updateStage = new Stage();
            updateStage.initModality(Modality.APPLICATION_MODAL);
            updateStage.setScene(new Scene(root));
            updateUserController.init(currentUser, application, updateStage, this, this.fromClinician);
            updateStage.show();
            Log.info("Successfully launched update user window for User NHI: " + currentUser.getNhi());

        } catch (IOException e) {
            Log.severe("Failed to load update user window for User NHI: " + currentUser.getNhi(), e);
        }
    }

    /**
     * Closes current window.
     */
    @FXML
    private void closeWindow() {
        checkSave();
        currentUser.getUndoStack().clear();
        currentUser.getRedoStack().clear();
        stage.close();
        Log.info("Successfully closed update user window for User NHI: " + currentUser.getNhi());
    }

    public void refreshDiseases() {
        diseasesTabPageController.diseaseRefresh(this.getIsSortedByName(), this.getIsRevereSorted());
    }

    /**
     * Sets the reason for organ deregistration
     *
     * @param organDeregisterationReason OrganDeregisterReason enum
     */
    public void setOrganDeregisterationReason(OrganDeregisterReason organDeregisterationReason) {
        receiverTabPageController.setOrganDeregistrationReason(organDeregisterationReason);
    }

    /**
     * Changes the currentUser to the provided user
     *
     * @param user user to change currentUser to
     */
    public void changeCurrentUser(User user) {
        currentUser = user;
        contact = user.getContact();
        if (user.getChanges() != null) {
            changelog = FXCollections.observableArrayList(user.getChanges());
        } else {
            changelog = FXCollections.observableArrayList(new ArrayList<Change>());
        }
    }

    /**
     * Popup that prompts the user if they want to save any unsaved changes before logging out or exiting the application
     */
    private void checkSave() {
        boolean noChanges = currentUser.getUndoStack().isEmpty();
        if (!noChanges) {
            Optional<ButtonType> result = UnsavedChangesAlert.getAlertResult();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                application.update(currentUser);
                application.saveUser(currentUser);

            } else {
                User revertUser = currentUser.getUndoStack().firstElement().getState();
                application.update(revertUser);
            }
        }
    }

    /**
     * Fires when the logout button is clicked Ends the users session, and takes back to the login
     * window.
     * When fired, it also deleted the temp folder.
     */
    @FXML
    private void logout() {
        checkSave();
        currentUser.getUndoStack().clear();
        currentUser.getRedoStack().clear();
        try {
            deleteTempDirectory();
        } catch (IOException e) {
            Log.severe("An Issue occurred while removing the temporary files", e);
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/loginView.fxml"));
        Parent root;
        try {
            root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();
            stage.close();
            LoginController loginController = loader.getController();
            loginController.init(AppController.getInstance(), newStage);

            Log.info("successfully launched login window after logged out for User NHI: " + currentUser.getNhi());
        } catch (IOException e) {
            Log.severe("failed to launch login window after logged out for User NHI: " + currentUser.getNhi(), e);
        }
    }


    /**
     * Sets the users contact information on the contact tab of the user profile
     */
    @FXML
    private void setContactPage() {
        if (contact != null) {
            showContactInfo();
        }
        pRegion.setText(currentUser.getRegion());
        pAddress.setText(currentUser.getContactDetails().getAddress().getStringAddress());
        city.setText(currentUser.getCity());
        country.setText(currentUser.getCountry());
        zipCode.setText(currentUser.getZipCode());

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

    private void showContactInfo() {
        eName.setText(contact.getName());
        eCellPhone.setText(contact.getCellPhoneNumber());
        if (contact.getAddress() != null) {
            eAddress.setText(contact.getAddress().toString());
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

        eAddress.setText(contact.getAddress().getStringAddress());

        ecCity.setText(contact.getCity());

        ecCountry.setText(contact.getCountry());

        eRegion.setText(contact.getRegion());

        ecZipCode.setText(contact.getZipCode());

        if (contact.getRelationship() != null) {
            relationship.setText(contact.getRelationship());
        } else {
            relationship.setText("");
        }
        pRegion.setText(currentUser.getRegion());
        pAddress.setText(currentUser.getContactDetails().getAddress().getStringAddress());
        city.setText(currentUser.getCity());
        country.setText(currentUser.getCountry());
        zipCode.setText(currentUser.getZipCode());

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

    /**
     * saves the user when the save menu item is clicked
     */
    @FXML
    void save() {
        application.update(currentUser);
        application.saveUser(currentUser);
        currentUser.getRedoStack().clear();
        currentUser.getUndoStack().clear();
        donationTabPageController.shutdownThreads();
        updateUndoRedoButtons();
    }

    /**
     * Updates all tab pages to display the information of the current user
     *
     * @param user Current user to be displayed
     */
    public void showUser(User user) {
        changeCurrentUser(user);
        setContactPage();
        userProfileTabPageController.showUser(user);
        medicationTabPageController.refreshLists(user);
        donationTabPageController.populateOrganLists(user);
        receiverTabPageController.populateReceiverLists(user);
        diseasesTabPageController.diseaseRefresh(false, false);
        procedureTabPageController.updateProcedureTables(user);
        refreshCurrentlyReceivingList();
        refreshCurrentlyDonating();

        if (user.getLastName() != null) {
            stage.setTitle("User Profile: " + user.getFirstName() + " " + user.getLastName());
        } else {
            stage.setTitle("User Profile: " + user.getFirstName());

        }
        updateUndoRedoButtons();
        changelog = FXCollections.observableList(user.getChanges());
        showDonorHistory();
        if (changelog.size() > 0) {
            statusBarPageController.updateStatus(user.getNhi() + " " + changelog.get(changelog.size() - 1).getChange());
        }
        donationTabPageController.updateButton();
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
        historyTableView.getColumns().clear();
        TableColumn<Change, String> timeColumn = new TableColumn<>("Time");
        TableColumn<Change, String> changeColumn = new TableColumn<>("Change");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        changeColumn.setCellValueFactory(new PropertyValueFactory<>("change"));
        historyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        historyTableView.setItems(changelog);
        historyTableView.getColumns().addAll(timeColumn, changeColumn);

    }


    /**
     * Creates a alert pop up to confirm that the user wants to delete the profile
     */
    @FXML
    public void delete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to delete this user? This action cannot be undone.");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent()) {
            return;
        }
        if (result.get() == ButtonType.OK) {
            currentUser.setDeleted(true);
            Log.info("Successfully deleted user profile for User NHI: " + currentUser.getNhi());
            application.deleteUser(currentUser);
            if (!fromClinician) {
                logout();
            } else {
                stage.close();
            }
        }
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
        receiverTabPageController.populateReceiverLists(currentUser);
    }

    /**
     * Updates the disabled property of the undo/redo buttons
     */
    public void updateUndoRedoButtons() {
        undoButton.setDisable(currentUser.getUndoStack().isEmpty());
        redoButton.setDisable(currentUser.getRedoStack().isEmpty());
    }

    @FXML
    private void refreshUser() {
        currentUser = application.findUser(currentUser.getNhi());
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

    public void disableLogout() {
        logoutUser.setText("Go Back");
        logoutUser.setOnAction(e -> closeWindow());
    }

    /**
     * handles events fired by objects this is listening to.
     * currently only handles UpdateNotificationEvents. Updates shown user to the one specified in that event
     *
     * @param evt PropertyChangeEvent to be handled.
     * @see UpdateNotificationEvent
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        UpdateNotificationEvent event;
        try {
            event = (UpdateNotificationEvent) evt;
        } catch (ClassCastException ex) {
            return;
        }
        if (event == null) {
            return;
        }
        if (event.getType().equals(EventTypes.USER_UPDATE)
                && event.getOldIdentifier().equalsIgnoreCase(currentUser.getNhi())
                || event.getNewIdentifier().equalsIgnoreCase(currentUser.getNhi())) {

            try {
                currentUser = application.getUserBridge().getUser(event.getNewIdentifier());
                if (currentUser != null) {
                    showUser(currentUser); //TODO: Apply change once we solve the DB race 7/8/18 JB
                }
            } catch (IOException ex) {
                Log.warning("failed to get updated user", ex);
            }

        } else if (event.getType().equals(EventTypes.REQUEST_UPDATE)) {
            userAppointmentAlertController.checkForUnseenUpdates(currentUser.getNhi());
        }
    }
}
