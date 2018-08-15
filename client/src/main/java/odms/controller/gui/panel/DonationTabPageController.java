package odms.controller.gui.panel;

import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import odms.commons.model.datamodel.OrgansWithExpiry;
import odms.commons.utils.Log;
import odms.commons.utils.OrganListCellFactory;
import odms.commons.utils.ProgressTask;
import odms.controller.AppController;
import odms.controller.gui.panel.view.OrganExpiryViewController;
import odms.controller.gui.widget.ProgressBarTableCellFactory;
import odms.controller.gui.window.UserController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class DonationTabPageController {

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

    private User currentUser;
    private AppController application;
    private UserController parent;
    private ObservableList<OrgansWithExpiry> organsWithExpiries = FXCollections.observableList(new ArrayList<>());

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
        expiryStaffIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        populateOrganLists(user);
        updateButton();
    }

    public void updateButton() {
        if (currentUser.getDeathDetails().getMomentOfDeath() == null || application.getUsername().isEmpty()) {
            expireOrganButton.setVisible(false);
            removeExpiryReasonButton.setVisible(false);

        } else {
            expireOrganButton.setVisible(true);
            removeExpiryReasonButton.setVisible(true);
        }

    }

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
                if (result.get() == ButtonType.YES) {
                    ExpiryReason expiryReason = currentUser.getDonorDetails().getOrganMap().get(currentlyDonating.getSelectionModel().getSelectedItem().getOrganType());
                    expiryReason.setName("");
                    expiryReason.setTimeOrganExpired(null);
                    expiryReason.setId("");
                    expiryReason.setReason("");
                    refreshCurrentlyDonating();
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
            currentOrgans.setItems(FXCollections.observableList(donating));
            currentOrgans.setCellFactory(column -> OrganListCellFactory.generateListCell(currentUser));
        }

        ArrayList<Organs> leftOverOrgans = new ArrayList<>();
        Collections.addAll(leftOverOrgans, Organs.values());
        leftOverOrgans.removeAll(donating);
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
        organsWithExpiries.clear();
        for (Map.Entry<Organs, ExpiryReason> organEntry : organsExpiryReasonMap.entrySet()) {
            organsWithExpiries.add(new OrgansWithExpiry(organEntry.getKey(), organEntry.getValue(), currentUser.getMomentDeath()));
        }
        organExpiryColumn.setCellFactory(callback -> ProgressBarTableCellFactory.generateCell(organExpiryColumn));

        currentlyDonating.setItems(organsWithExpiries);
    }

    public void refreshCurrentlyDonating() {
        populateOrganLists(currentUser);
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

    @FXML
    void expireOrgan() {
        if (currentlyDonating.getItems().size() > 0) {
            if (currentlyDonating.getSelectionModel().getSelectedItem() == null) {
                currentlyDonating.getSelectionModel().select(0);
            }
            ExpiryReason expir = currentUser.getDonorDetails().getOrganMap().get(currentlyDonating.getSelectionModel().getSelectedItem().getOrganType());
            FXMLLoader organExpiryScreenLoader = new FXMLLoader(getClass().getResource("/FXML/organExpiryScreen.fxml"));
            Parent root;
            try {
                root = organExpiryScreenLoader.load();
                OrganExpiryViewController organExpiryViewController = organExpiryScreenLoader.getController();
                Stage updateStage = new Stage();
                updateStage.initModality(Modality.APPLICATION_MODAL);
                updateStage.setScene(new Scene(root));
                organExpiryViewController.init(this.application, currentlyDonating.getSelectionModel().getSelectedItem().getOrganType(), expir, currentUser, updateStage, this);
                updateStage.show();
                Log.info("Successfully launched organ expiry window for User NHI: " + currentUser.getNhi());

            } catch (IOException e) {
                Log.severe("Failed to load update death details window for User NHI: " + currentUser.getNhi(), e);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "the user is not currently donating any organs", ButtonType.OK);
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

        currentlyDonating.refresh();
        parent.refreshCurrentlyReceivingList();
    }

    public synchronized void shutdownThreads() {
        if (!Platform.isFxApplicationThread()) {
            return;
        }
        for (OrgansWithExpiry organ : organsWithExpiries) {
            organ.getProgressTask().cancel(true);
        }
    }
}
