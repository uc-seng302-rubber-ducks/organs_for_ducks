package odms.controller.gui.panel;

import javafx.application.Platform;
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
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.ExpiryReason;
import odms.commons.model.datamodel.OrgansWithDisqualification;
import odms.commons.model.datamodel.OrgansWithExpiry;
import odms.commons.utils.Log;
import odms.commons.utils.OrganListCellFactory;
import odms.commons.utils.ProgressTask;
import odms.controller.AppController;
import odms.controller.gui.panel.view.OrganExpiryViewController;
import odms.controller.gui.popup.view.DisqualifyOrganReasonViewController;
import odms.controller.gui.popup.view.RemoveDisqualificationViewController;
import odms.controller.gui.widget.ProgressBarTableCellFactory;
import odms.controller.gui.window.UserController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class DonationTabPageController {

    @FXML
    private Label donatingOrgansTableLabel;

    @FXML
    private Label disqualifiedOrgansTableLabel;

    @FXML
    private TableView<OrgansWithExpiry> currentlyDonating;

    @FXML
    private ListView<Organs> canDonate;

    @FXML
    private ListView<Organs> currentOrgans;

    @FXML
    private TableColumn<OrgansWithExpiry, Organs> donatingOrganColumn;

    @FXML
    private TableColumn<OrgansWithExpiry, ProgressTask> organExpiryColumn;

    @FXML
    private TableColumn<OrgansWithExpiry, LocalDateTime> manualExpiryTimeColumn;

    @FXML
    private TableColumn<OrgansWithExpiry, String> expiryStaffIdColumn;

    @FXML
    private TableColumn<OrgansWithExpiry, String> expiryReasonColumn;

    @FXML
    private Button expireOrganButton;

    @FXML
    private Button removeExpiryReasonButton;

    @FXML
    private Button disqualifyOrganButton;

    @FXML
    private TableView<OrgansWithDisqualification> userDisqualifiedOrgansTable;

    @FXML
    private TableColumn<OrgansWithDisqualification, Organs> disqualifiedOrganColumn;

    @FXML
    private TableColumn<OrgansWithDisqualification, String> disqualifiedReasonColumn;

    @FXML
    private TableColumn<OrgansWithDisqualification, LocalDate> disqualifiedDateColumn;

    @FXML
    private TableColumn<OrgansWithDisqualification, String> disqualifiedStaffIdColumn;

    @FXML
    private Button updateDisqualifiedOrgan;

    @FXML
    private Button removeDisqualificationButton;

    private User currentUser;
    private AppController application;
    private UserController parent;
    private ObservableList<OrgansWithExpiry> organsWithExpiries = FXCollections.observableList(new ArrayList<>());
    private ObservableList<OrgansWithDisqualification> disqualifiedOrgansView = FXCollections.observableList(new ArrayList<>());
    private ObservableList<OrgansWithDisqualification> observableDisqualifiedOrgans;
    private boolean listenFlag = true;

    /**
     * Initializes the columns of the currently donating table
     *
     * @param controller the application controller
     * @param user       the current user
     * @param parent     the UserController class this belongs to
     */
    public void init(AppController controller, User user, UserController parent) {
        application = controller;
        currentUser = user;
        this.parent = parent;

        donatingOrganColumn.setSortable(false);
        expiryReasonColumn.setSortable(false);
        manualExpiryTimeColumn.setSortable(false);
        expiryStaffIdColumn.setSortable(false);
        organExpiryColumn.setSortable(false);

        donatingOrganColumn.setCellValueFactory(new PropertyValueFactory<>("organType"));
        donatingOrganColumn.setCellFactory(cell -> OrganListCellFactory.generateOrganTableCell(donatingOrganColumn, currentUser));
        expiryReasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        manualExpiryTimeColumn.setCellValueFactory(new PropertyValueFactory<>("expiryTime"));
        organExpiryColumn.setCellValueFactory(new PropertyValueFactory<>("progressTask"));
        expiryStaffIdColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        currentlyDonating.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        currentlyDonating.setVisible(false);
        currentlyDonating.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        canDonate.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        populateOrganLists(user);
        updateButton();
        currentlyDonating.getSelectionModel().selectedItemProperty().addListener(a-> {
            if(currentlyDonating.getSelectionModel().getSelectedItem() == null) {
                return;
            }
            if (!currentlyDonating.getSelectionModel().getSelectedItem().getExpired()){
                removeExpiryReasonButton.setDisable(true);
                expireOrganButton.setText("Expire Organ");
            } else{
                removeExpiryReasonButton.setDisable(false);
                expireOrganButton.setText("Edit Expiry Details");
            }
        });

        disqualifiedOrganColumn.setCellValueFactory(new PropertyValueFactory<>("organType"));
        disqualifiedReasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        disqualifiedDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        disqualifiedStaffIdColumn.setCellValueFactory(new PropertyValueFactory<>("staffId"));

        userDisqualifiedOrgansTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        userDisqualifiedOrgansTable.setVisible(false);

        initDisqualifiedOrgans();
        showOrHideExpiryTable();

    }

    /**
     * when an item at UserDisqualifiedOrgansTable is selected,
     * update and remove button is enabled and
     * disqualify button is disabled.
     * both canDonate and currentOrgans listview is cleared.
     */
    @FXML
    public void userDisqualifiedOrgansTableMouseClick() {
        disqualifyOrganButton.setDisable(true);
        if(!canDonate.getSelectionModel().isEmpty()) {
            canDonate.getSelectionModel().clearSelection();
        }
        if(!currentOrgans.getSelectionModel().isEmpty()) {
            currentOrgans.getSelectionModel().clearSelection();
        }

        if (!userDisqualifiedOrgansTable.getSelectionModel().isEmpty()){
            updateDisqualifiedOrgan.setDisable(false);
            removeDisqualificationButton.setDisable(false);
        } else {
            updateDisqualifiedOrgan.setDisable(true);
            removeDisqualificationButton.setDisable(true);
        }
    }

    /**
     * when an item at canDonate listview is selected,
     * update and remove button is disabled and
     * disqualify button is enabled.
     * both updateDisqualifiedOrgan table and currentOrgans listview is cleared.
     */
    @FXML
    public void canDonateMouseClick() {
        updateDisqualifiedOrgan.setDisable(true);
        removeDisqualificationButton.setDisable(true);
        if (!userDisqualifiedOrgansTable.getSelectionModel().isEmpty()){
            userDisqualifiedOrgansTable.getSelectionModel().clearSelection();
        }
        if(!canDonate.getSelectionModel().getSelectedItems().isEmpty()){
            currentOrgans.getSelectionModel().clearSelection();
            disqualifyOrganButton.setDisable(false);
        } else {
            disqualifyOrganButton.setDisable(true);
        }
    }

    /**
     * when an item at currentOrgans listview is selected,
     * update and remove button is disabled and
     * disqualify button is enabled.
     * both updateDisqualifiedOrgan table and canDonate listview is cleared.
     */
    @FXML
    public void currentOrgansMouseClick() {
        updateDisqualifiedOrgan.setDisable(true);
        removeDisqualificationButton.setDisable(true);
        if (!userDisqualifiedOrgansTable.getSelectionModel().isEmpty()){
            userDisqualifiedOrgansTable.getSelectionModel().clearSelection();
        }
        if(!currentOrgans.getSelectionModel().getSelectedItems().isEmpty()) {
            canDonate.getSelectionModel().clearSelection();
            disqualifyOrganButton.setDisable(false);
        } else {
            disqualifyOrganButton.setDisable(true);
        }
    }

    /**
     * Sets up the table view for the disqualified organs and the listener on the observable list of disqualified organs
     */
    private void initDisqualifiedOrgans() {
        for (OrgansWithDisqualification organ : currentUser.getDonorDetails().getDisqualifiedOrgans()) {
            if (organ.isCurrentlyDisqualified()) {
                disqualifiedOrgansView.add(organ);
            }
        }
        userDisqualifiedOrgansTable.setItems(disqualifiedOrgansView);

        observableDisqualifiedOrgans = FXCollections.observableArrayList(currentUser.getDonorDetails().getDisqualifiedOrgans());
        observableDisqualifiedOrgans.addListener((ListChangeListener<? super OrgansWithDisqualification>) a -> {
            if (listenFlag) {
                currentUser.saveStateForUndo();
                this.parent.updateUndoRedoButtons();
                currentUser.getDonorDetails().getDisqualifiedOrgans().clear();
                currentUser.getDonorDetails().getDisqualifiedOrgans().addAll(observableDisqualifiedOrgans);
            }

            disqualifiedOrgansView.clear();
            for (OrgansWithDisqualification organ : observableDisqualifiedOrgans) {
                if (organ.isCurrentlyDisqualified()) {
                    disqualifiedOrgansView.add(organ);
                }
            }
            userDisqualifiedOrgansTable.setItems(disqualifiedOrgansView);

        });
    }

    /**
     * If the user is dead, the expiry table will be shown. If role is clinician
     * or admin the relative expire/disqualify buttons for organs will be shown too.
     */
    private void showOrHideExpiryTable() {
        if (currentUser.getDeathDetails().getMomentOfDeath() == null) {
            //user is alive, only show disqualified table

            //Hide expiry things
            currentlyDonating.setVisible(false);
            expireOrganButton.setVisible(false);
            removeExpiryReasonButton.setVisible(false);

            //Show disqualification things
            disqualifiedOrgansTableLabel.setVisible(true);
            userDisqualifiedOrgansTable.setVisible(true);
            disqualifyOrganButton.setVisible(true);
            updateDisqualifiedOrgan.setVisible(true);
            removeDisqualificationButton.setVisible(true);

            //Show donating things
            donatingOrgansTableLabel.setText("Currently Donating");
            currentOrgans.setVisible(true);


        } else {
            //Hide disqualification things
            disqualifiedOrgansTableLabel.setVisible(false);
            userDisqualifiedOrgansTable.setVisible(false);
            disqualifyOrganButton.setVisible(false);
            updateDisqualifiedOrgan.setVisible(false);
            removeDisqualificationButton.setVisible(false);

            //Hide donating things
            donatingOrgansTableLabel.setText("Expired Organs");
            currentOrgans.setVisible(false);

            //Show expiry things
            currentlyDonating.setVisible(true);
            expireOrganButton.setVisible(true);
            removeExpiryReasonButton.setVisible(true);

        }
    }

    /**
     * Hides the expire organ and cancel expiry buttons if there is no clinician/admin present
     */
    public void updateButton() {
        if (application.getUsername() == null || application.getUsername().isEmpty()) {
            expireOrganButton.setVisible(false);
            removeExpiryReasonButton.setVisible(false);
            disqualifyOrganButton.setVisible(false);
        }
    }


    /**
     * Attempts to cancel the manual expiry of a donated organ.
     * <p>
     * If the organ has no manual expiry or no organ is selected,
     * pop ups will be displayed with appropriate user feedback.
     */
    @FXML
    public void removeExpiry() {
        if (currentlyDonating.getItems().size() > 0) {
            if (currentlyDonating.getSelectionModel().getSelectedItem() != null) {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                        "please confirm you want to remove the manual expiry for ",
                        ButtonType.YES, ButtonType.NO);

                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
                yesButton.setId("yeseButton");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    ExpiryReason expiryReason = currentUser.getDonorDetails().getOrganMap().get(currentlyDonating.getSelectionModel().getSelectedItem().getOrganType());
                    expiryReason.setName("");
                    expiryReason.setTimeOrganExpired(null);
                    expiryReason.setId("");
                    expiryReason.setReason("");
                    refreshCurrentlyDonating();
                    parent.showUser(currentUser);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please select an organ to remove an expiry from", ButtonType.OK);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "the user is not currently donating any organs", ButtonType.OK);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    }

    /**
     * Populates the organ lists of the user
     *
     * @param user user to use to populate
     */
    public void populateOrganLists(User user) {
        currentUser = user;
        ArrayList<Organs> donating;
        try {
            donating = new ArrayList<>(user.getDonorDetails().getOrgans());
        } catch (NullPointerException ex) {
            donating = new ArrayList<>();
        }
        if (user.getMomentDeath() != null) {
            populateTableView(currentUser.getDonorDetails().getOrganMap());
        } else {
            currentlyDonating.setVisible(false);
            currentOrgans.setVisible(true);
            showOrHideExpiryTable();
            currentOrgans.setItems(FXCollections.observableList(donating));
            currentOrgans.setCellFactory(column -> OrganListCellFactory.generateListCell(currentUser));
        }

        ArrayList<Organs> leftOverOrgans = new ArrayList<>();
        Collections.addAll(leftOverOrgans, Organs.values());
        leftOverOrgans.removeAll(donating);
        for (OrgansWithDisqualification organ : currentUser.getDonorDetails().getDisqualifiedOrgans()) {
            if (organ.isCurrentlyDisqualified()) {
                leftOverOrgans.remove(organ.getOrganType());
            }
        }
        canDonate.setItems(FXCollections.observableList(leftOverOrgans));
    }

    /**
     * Populates the table view of currently donating organs.
     * This is only populated when the donor is deceased.
     *
     * @param organsExpiryReasonMap A map containing key,value pairs of organs and expiry reasons
     */
    private void populateTableView(Map<Organs, ExpiryReason> organsExpiryReasonMap) {
        currentOrgans.setVisible(false);
        currentlyDonating.setVisible(true);
        showOrHideExpiryTable();
        organsWithExpiries.clear();
        for (Map.Entry<Organs, ExpiryReason> organEntry : organsExpiryReasonMap.entrySet()) {
            organsWithExpiries.add(new OrgansWithExpiry(organEntry.getKey(), organEntry.getValue(), currentUser.getMomentDeath()));
        }
        //Add the disqualified organs to the expired organs
        for (OrgansWithDisqualification organ : currentUser.getDonorDetails().getDisqualifiedOrgans()) {
            if (organ.isCurrentlyDisqualified()) {
                ExpiryReason expiryDetails = new ExpiryReason(organ.getStaffId(), organ.getDate().atStartOfDay(), organ.getReason(), currentUser.getFullName());
                organsWithExpiries.add(new OrgansWithExpiry(organ.getOrganType(), expiryDetails, currentUser.getMomentDeath()));
            }
        }
        organExpiryColumn.setCellFactory(callback -> ProgressBarTableCellFactory.generateCell(organExpiryColumn));

        currentlyDonating.setItems(organsWithExpiries);
    }

    /**
     * Calls a method to repopulate the donation tables based on changes to the user.
     */
    public void refreshCurrentlyDonating() {
        populateOrganLists(currentUser);
    }

    /**
     * Forces the observable list to refresh itself based on the users disqualified organs list.
     */
    public void refreshDisqualifiedOrgans() {
        listenFlag = false;
        observableDisqualifiedOrgans.clear();
        observableDisqualifiedOrgans.setAll(currentUser.getDonorDetails().getDisqualifiedOrgans());
        listenFlag = true;
    }

    /**
     * Moves selected organ from not donating to currently donating
     */
    @FXML
    void donate() {
        if (!canDonate.getSelectionModel().isEmpty()) {
            Organs toDonate = canDonate.getSelectionModel().getSelectedItem();
            currentUser.getDonorDetails().addOrgan(toDonate, new ExpiryReason());

            if (currentUser.getMomentDeath() != null) {
                populateTableView(currentUser.getDonorDetails().getOrganMap());
            } else {
                currentOrgans.getItems().add(toDonate);
            }

            if (parent.currentlyReceivingContains(toDonate)) {
                currentUser.getCommonOrgans().add(toDonate);
            }
            application.update(currentUser);
            canDonate.getItems().remove(toDonate);
            parent.updateUndoRedoButtons();
            Log.info("Donated organ: " + toDonate.toString() + "for User NHI: " + currentUser.getNhi());
        } else {
            Log.warning("Donate organs failed for User NHI: " + currentUser.getNhi() + ", no organs selected.");
        }
        currentlyDonating.refresh();
        parent.refreshCurrentlyReceivingList();
    }

    /**
     * Opens the window to manually expire the selected organ.
     * If no organ is selected, or the organ has expired naturally,
     * the appropriate user feedback will be displayed in a pop up.
     */
    @FXML
    void expireOrgan() {
        if (currentlyDonating.getItems().size() > 0) {
            if (currentlyDonating.getSelectionModel().getSelectedItem() == null) {
                currentlyDonating.getSelectionModel().select(0);
            }
            if (currentlyDonating.getSelectionModel().getSelectedItem().getProgressTask().calculateTimeLeft(LocalDateTime.now()) != 0) {
                ExpiryReason expir = currentUser.getDonorDetails().getOrganMap().get(currentlyDonating.getSelectionModel().getSelectedItem().getOrganType());
                FXMLLoader organExpiryScreenLoader = new FXMLLoader(getClass().getResource("/FXML/organExpiryScreen.fxml"));
                Parent root;
                try {
                    root = organExpiryScreenLoader.load();
                    OrganExpiryViewController organExpiryViewController = organExpiryScreenLoader.getController();
                    Stage updateStage = new Stage();
                    updateStage.initModality(Modality.APPLICATION_MODAL);
                    updateStage.setScene(new Scene(root));
                    organExpiryViewController.init(this.application, currentlyDonating.getSelectionModel().getSelectedItem().getOrganType(), expir, currentUser, updateStage, this, true);
                    updateStage.show();
                    Log.info("Successfully launched organ expiry window for User NHI: " + currentUser.getNhi());

                } catch (IOException e) {
                    Log.severe("Failed to load update death details window for User NHI: " + currentUser.getNhi(), e);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "The organ is expired, so it can't be changed", ButtonType.OK);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "The user is not currently donating any organs", ButtonType.OK);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    }

    /**
     * Moves selected organ from currently donating to donatable
     */
    @FXML
    void undonate() {
        if (!currentlyDonating.getSelectionModel().isEmpty() || !currentOrgans.getSelectionModel().isEmpty()) {
            Organs organ;

            if (currentUser.getMomentDeath() != null) {
                OrgansWithExpiry toUndonate = currentlyDonating.getSelectionModel().getSelectedItem();
                organsWithExpiries.remove(toUndonate);
                organ = toUndonate.getOrganType();
            } else {
                organ = currentOrgans.getSelectionModel().getSelectedItem();
                currentOrgans.getItems().remove(organ);
            }

            canDonate.getItems().add(organ);
            if (currentUser.getCommonOrgans().contains(organ)) {
                currentUser.getCommonOrgans().remove(organ);
            }

            currentUser.getDonorDetails().removeOrgan(organ);
            application.update(currentUser);
            parent.updateUndoRedoButtons();
            Log.info("un-donated organ: " + organ + "for User NHI: " + currentUser.getNhi());
        } else {
            Log.warning("un-donate organs failed for User NHI: " + currentUser.getNhi() + ", no organs selected.");
        }
        refreshCurrentlyDonating();
        parent.refreshCurrentlyReceivingList();
    }

    /**
     * Shuts down all progress task threads for the donation table
     */
    public synchronized void shutdownThreads() {
        if (!Platform.isFxApplicationThread()) {
            return;
        }
        for (OrgansWithExpiry organ : organsWithExpiries) {
            organ.getProgressTask().cancel(true);
        }
    }

    /**
     * launches disqualify organ pop up in update mode
     */
    @FXML
    private void updateDisqualifiedOrgan(){
        launchDisqualifyOrganReason(true);
    }

    /**
     * launches disqualify organ pop up in non-update mode
     */
    @FXML
    private void disqualifyOrgan() {
        launchDisqualifyOrganReason(false);
    }

    /**
     * Calls the method to launch the remove disqualification window
     */
    @FXML
    private void removeDisqualification() {
        launchRemoveDisqualification();
    }


    /**
     * Launches the pop-up to enter reason for disqualifying organ and
     * eligible date for users to re-donate organ.
     * The pop-up is used for both disqualifying an organ and updating
     * an organ that is already disqualified.
     *
     * @param isUpdate true if this pop up is used for
     *                 updating disqualified organs, false otherwise.
     */
    private void launchDisqualifyOrganReason(Boolean isUpdate) {
        Organs organ = null;
        if (!currentOrgans.getSelectionModel().getSelectedItems().isEmpty()) {
            organ = currentOrgans.getSelectionModel().getSelectedItems().get(0);
        } else if (!canDonate.getSelectionModel().getSelectedItems().isEmpty()) {
            organ = canDonate.getSelectionModel().getSelectedItems().get(0);
        }

        FXMLLoader disqualifyOrganReasonLoader = new FXMLLoader(getClass().getResource("/FXML/disqualifyOrganReason.fxml"));
        Parent root;

        try {
            root = disqualifyOrganReasonLoader.load();
            DisqualifyOrganReasonViewController disqualifyOrganReasonViewController = disqualifyOrganReasonLoader.getController();
            Stage disqualifyOrganReasonStage = new Stage();

            if (isUpdate) {
                OrgansWithDisqualification organsWithDisqualification = userDisqualifiedOrgansTable.getSelectionModel().getSelectedItem();
                disqualifyOrganReasonViewController.init(organsWithDisqualification.getOrganType(), currentUser, disqualifyOrganReasonStage, application.getUsername(), observableDisqualifiedOrgans);
                disqualifyOrganReasonViewController.updateMode(userDisqualifiedOrgansTable.getSelectionModel().getSelectedItem());

            } else {
                disqualifyOrganReasonViewController.init(organ, currentUser, disqualifyOrganReasonStage, application.getUsername(), observableDisqualifiedOrgans);

            }

            disqualifyOrganReasonStage.setScene(new Scene(root));
            disqualifyOrganReasonStage.setTitle("");
            disqualifyOrganReasonStage.initModality(Modality.APPLICATION_MODAL);
            disqualifyOrganReasonStage.show();
            refreshCurrentlyDonating();
            updateDisqualifiedOrgan.setDisable(true);
            removeDisqualificationButton.setDisable(true);
            parent.updateUndoRedoButtons();
            Log.info("Successfully launched the disqualify Organ Reason pop-up window for user: " + currentUser.getNhi());

        } catch (IOException e) {
            Log.severe("Failed to load disqualify Organ Reason pop-up window for user: " + currentUser.getNhi(), e);
        }
    }

    /**
     * Launches the remove disqualification window. This contains a text area to insert the reason that the organ's
     * disqualification is being removed early
     */
    private void launchRemoveDisqualification() {
        FXMLLoader removeDisqualifiedOrgansLoader = new FXMLLoader(getClass().getResource("/FXML/removeDisqualificationView.fxml"));
        Parent root;

        try {
            root = removeDisqualifiedOrgansLoader.load();
            RemoveDisqualificationViewController removeDisqualificationViewController = removeDisqualifiedOrgansLoader.getController();
            Stage removeDisqualifiedStage = new Stage();

            removeDisqualificationViewController.init(userDisqualifiedOrgansTable.getSelectionModel().getSelectedItem(), currentUser, removeDisqualifiedStage, observableDisqualifiedOrgans);

            removeDisqualifiedStage.setScene(new Scene(root));
            removeDisqualifiedStage.setTitle("");
            removeDisqualifiedStage.initModality(Modality.APPLICATION_MODAL);
            removeDisqualifiedStage.show();
            refreshCurrentlyDonating();
            updateDisqualifiedOrgan.setDisable(true);
            removeDisqualificationButton.setDisable(true);
            parent.updateUndoRedoButtons();
            Log.info("Successfully launched the remove disqualified pop-up window for user: " + currentUser.getNhi());
        } catch (IOException e) {
            Log.severe("Failed to load the remove disqualified pop-up window for user: " + currentUser.getNhi(), e);
        }
    }
}
