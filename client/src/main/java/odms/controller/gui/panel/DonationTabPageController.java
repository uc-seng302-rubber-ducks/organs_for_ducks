package odms.controller.gui.panel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.OrgansWithExpiry;
import odms.commons.utils.Log;
import odms.commons.utils.ProgressBarService;
import odms.controller.AppController;
import odms.controller.gui.widget.ProgressBarTableCellFactory;
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
    private ListView<Organs> currentOrgans;

    @FXML
    private TableColumn<OrgansWithExpiry, String> donatingOrganColumn;

    @FXML
    private TableColumn<OrgansWithExpiry, ProgressBarService> organExpiryColumn;

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
        expiryReasonColumn.setCellValueFactory(new PropertyValueFactory<>("expiryReason"));
        organExpiryColumn.setCellValueFactory(new PropertyValueFactory<>("progressTask"));
        organExpiryColumn.setCellFactory(callback -> ProgressBarTableCellFactory.generateCell(organExpiryColumn));

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

        if (user.getMomentDeath() != null) {
            populateTableView(donating);
        } else {
            currentlyDonating.setVisible(false);
            currentOrgans.setVisible(true);
            currentOrgans.setItems(FXCollections.observableList(donating));
        }

        ArrayList<Organs> leftOverOrgans = new ArrayList<>();
        Collections.addAll(leftOverOrgans, Organs.values());
        for (Organs o : donating) {
            leftOverOrgans.remove(o);
        }
        canDonate.setItems(FXCollections.observableList(leftOverOrgans));
    }

    /**
     * Populates the table view of currently donating organs.
     * This is only populated when the donor is deceased.
     *
     * @param donating An array list of the organs the user is donating
     */
    private void populateTableView(ArrayList<Organs> donating) {
        currentOrgans.setVisible(false);
        currentlyDonating.setVisible(true);

        List<OrgansWithExpiry> results = new ArrayList<>();
        for (Organs organ : donating) {
            results.add(new OrgansWithExpiry(organ, currentUser.getMomentDeath()));
        }

        ObservableList<OrgansWithExpiry> donatingOrgans = FXCollections.observableArrayList(results);
        currentlyDonating.setItems(donatingOrgans);
    }

    public void refreshCurrentlyDonating() {
        currentlyDonating.refresh();
    }

    /**
     * Moves selected organ from not donating to currently donating
     */
    @FXML
    void donate() {
        if (!canDonate.getSelectionModel().isEmpty()) {
            Organs toDonate = canDonate.getSelectionModel().getSelectedItem();

            if (currentUser.getMomentDeath() != null) {
                currentlyDonating.getItems().add(new OrgansWithExpiry(toDonate, currentUser.getMomentDeath()));
            } else {
                currentOrgans.getItems().add(toDonate);
            }

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
            Organs organ;

            if (currentUser.getMomentDeath() != null) {
                OrgansWithExpiry toUndonate = currentlyDonating.getSelectionModel().getSelectedItem();
                currentlyDonating.getItems().remove(toUndonate);
                organ = toUndonate.getOrganType();
            } else {
                organ = currentOrgans.getSelectionModel().getSelectedItem();
                currentOrgans.getItems().remove(organ);
            }

            canDonate.getItems().add(organ);
            if (currentUser.getCommonOrgans().contains(organ)) {
                currentUser.getCommonOrgans().remove(organ);
                currentlyDonating.refresh();
            }

            currentUser.getDonorDetails().removeOrgan(organ);
            currentlyDonating.refresh();
            application.update(currentUser);
            parent.updateUndoRedoButtons();
            Log.info("un-donated organ: " + organ + "for User NHI: " + currentUser.getNhi());
        } else {
            Log.warning("un-donate organs failed for User NHI: " + currentUser.getNhi() + ", no organs selected.");
        }

        currentlyDonating.refresh();
        parent.refreshCurrentlyReceivingList();
    }

}
