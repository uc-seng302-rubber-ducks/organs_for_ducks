package odms.controller.gui.panel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.OrgansWithExpiry;
import odms.commons.utils.Log;
import odms.commons.utils.ProgressBarHelper;
import odms.controller.AppController;
import odms.controller.gui.window.UserController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DonationTabPageController {

    @FXML
    private TableView<OrgansWithExpiry> currentlyDonating;

    @FXML
    private ListView<Organs> canDonate;

    @FXML
    private TableColumn<OrgansWithExpiry, String> donatingOrganColumn;

    @FXML
    private TableColumn<OrgansWithExpiry, Service> organExpiryColumn;

    @FXML
    private TableColumn<OrgansWithExpiry, Boolean> expiredDonationColumn;

    @FXML
    private TableColumn<OrgansWithExpiry, String> expiryReasonColumn;

    private User currentUser;
    private AppController application;
    private UserController parent;

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

        donatingOrganColumn.setCellValueFactory(new PropertyValueFactory<>("organType"));
        expiredDonationColumn.setCellValueFactory(new PropertyValueFactory<>("hasExpired"));
        expiryReasonColumn.setCellValueFactory(new PropertyValueFactory<>("expiryReason"));
        organExpiryColumn.setCellValueFactory(new PropertyValueFactory<>("progressTask"));
        organExpiryColumn.setCellFactory(callback -> ProgressBarHelper.generateProgressBar(organExpiryColumn));

        populateOrganLists(user);
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
        List<OrgansWithExpiry> results = new ArrayList<>();
        for (Organs organ : donating) {
            results.add(new OrgansWithExpiry(organ, currentUser.getMomentDeath()));
        }

        ObservableList<OrgansWithExpiry> donatingOrgans = FXCollections.observableArrayList(results);
        currentlyDonating.setItems(donatingOrgans);
        ArrayList<Organs> leftOverOrgans = new ArrayList<>();
        Collections.addAll(leftOverOrgans, Organs.values());
        for (Organs o : donating) {
            leftOverOrgans.remove(o);
        }
        canDonate.setItems(FXCollections.observableList(leftOverOrgans));
    }

    public void refreshCurrentlyDonating() {
        currentlyDonating.refresh();
    }

    /**
     * Moves selected organ from donatable to currently donating
     */
    @FXML
    void donate() {
        if (!canDonate.getSelectionModel().isEmpty()) {
            Organs toDonate = canDonate.getSelectionModel().getSelectedItem();
            currentlyDonating.getItems().add(new OrgansWithExpiry(toDonate, currentUser.getMomentDeath()));
            currentUser.getDonorDetails().addOrgan(toDonate);
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
     * Moves selected organ from currently donating to donatable
     */
    @FXML
    void undonate() {
        if (!currentlyDonating.getSelectionModel().isEmpty()) {
            OrgansWithExpiry toUndonate = currentlyDonating.getSelectionModel().getSelectedItem();
            currentlyDonating.getItems().remove(toUndonate);
            canDonate.getItems().add(toUndonate.getOrganType());
            if (currentUser.getCommonOrgans().contains(toUndonate.getOrganType())) {
                currentUser.getCommonOrgans().remove(toUndonate.getOrganType());
                currentlyDonating.refresh();
            }

            currentUser.getDonorDetails().removeOrgan(toUndonate.getOrganType());
            currentlyDonating.refresh();
            application.update(currentUser);
            parent.updateUndoRedoButtons();
            Log.info("un-donated organ: " + toUndonate.getOrganType().toString() + "for User NHI: " + currentUser.getNhi());
        } else {
            Log.warning("un-donate organs failed for User NHI: " + currentUser.getNhi() + ", no organs selected.");
        }

        currentlyDonating.refresh();
        parent.refreshCurrentlyReceivingList();
    }

}
