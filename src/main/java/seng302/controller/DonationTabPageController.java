package seng302.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import seng302.model.Organs;
import seng302.model.User;
import seng302.service.Log;
import seng302.service.OrganListCellFactory;

import java.util.ArrayList;
import java.util.Collections;

public class DonationTabPageController {

    @FXML
    private ListView<Organs> currentlyDonating;

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
        currentlyDonating.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        currentlyDonating.setCellFactory(column -> OrganListCellFactory.generateListCell(currentUser));

        populateOrganLists(user);
    }

    /**
     * Popoulates the organ lists of the user
     *
     * @param user user to use to populate
     */
    public void populateOrganLists(User user) {
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
            Log.info("Donated organ: " + toDonate.organName + "for User NHI: " + currentUser.getNhi());
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
            Log.info("un-donated organ: " + toUndonate.organName + "for User NHI: " + currentUser.getNhi());
        } else {
            Log.warning("un-donate organs failed for User NHI: " + currentUser.getNhi() + ", no organs selected.");
        }

        currentlyDonating.refresh();
        parent.refreshCurrentlyReceivingList();
    }

}
