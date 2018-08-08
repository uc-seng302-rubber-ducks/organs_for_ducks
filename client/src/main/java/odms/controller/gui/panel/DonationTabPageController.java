package odms.controller.gui.panel;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.utils.Log;
import odms.commons.utils.OrganListCellFactory;
import odms.controller.AppController;
import odms.controller.gui.window.UserController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

public class DonationTabPageController {

    @FXML
    private TableView<Organs> currentlyDonating;

    @FXML
    private ListView<Organs> canDonate;

    private User currentUser;
    private AppController application;
    private UserController parent;

    /**
     * Gives the user view the application controller and hides all label and buttons that are not
     * needed on opening
     *
     * @param controller the application controller
     * @param user       the current user
     * @param parent     the UserController class this belongs to
     */
    public void init(AppController controller, User user, UserController parent) {
        application = controller;
        currentUser = user;
        this.parent = parent;

        TableColumn<Organs, String> donatingOrganName = new TableColumn("Organ");
        TableColumn<Organs, LocalDateTime> donatingOrganExpiry = new TableColumn("Expiry Countdown"); // todo: find out how the expiry time is being stored - jen 8/8

        donatingOrganName.setCellValueFactory(new PropertyValueFactory<>("organName"));
        donatingOrganExpiry.setCellValueFactory(new PropertyValueFactory<>("expiryTime"));

        currentlyDonating.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        currentlyDonating.getColumns().addAll(donatingOrganName, donatingOrganExpiry);
        currentlyDonating.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        populateOrganLists(user);
    }

    /**
     * Popoulates the organ lists of the user
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
        currentlyDonating.setItems(FXCollections.observableList(donating));
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
            currentlyDonating.getItems().add(toDonate);
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
            parent.updateUndoRedoButtons();
            Log.info("un-donated organ: " + toUndonate.toString() + "for User NHI: " + currentUser.getNhi());
        } else {
            Log.warning("un-donate organs failed for User NHI: " + currentUser.getNhi() + ", no organs selected.");
        }

        currentlyDonating.refresh();
        parent.refreshCurrentlyReceivingList();
    }

}
