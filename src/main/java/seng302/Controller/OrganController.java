package seng302.Controller;


import java.util.ArrayList;
import java.util.Collections;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import seng302.Model.Organs;
import seng302.Model.User;
import seng302.Service.Log;

/**
 * class for the Organs view
 *
 * @author Josh Burt
 */
@Deprecated
public class OrganController {

    @FXML
    private ListView<Organs> currentlyDonating;

    @FXML
    private ListView<Organs> canDonate;

    @FXML
    private Label userNameLabel;

    private AppController appController;
    private User currentUser;

    /**
     *
     * @param user The current user.
     * @param controller An instance of AppController.
     */
    public void init(User user, AppController controller) {
        this.appController = controller;
        currentUser = user;
      userNameLabel.setText(user.getFullName());
        ArrayList<Organs> donating;
        try {
            donating= new ArrayList<>(user.getDonorDetails().getOrgans());
        }
        catch (NullPointerException ex) {
            donating = new ArrayList<>();
        }
        currentlyDonating.setItems(FXCollections.observableList(donating));
        if (!currentUser.getCommonOrgans().isEmpty()){
            for (Organs organ: currentUser.getCommonOrgans()) {
                int index = currentlyDonating.getItems().indexOf(organ);
                currentlyDonating.getSelectionModel().select(index);
            }
        }
      ArrayList<Organs> leftOverOrgans = new ArrayList<>();
        Collections.addAll(leftOverOrgans, Organs.values());
        for (Organs o : donating){
            leftOverOrgans.remove(o);
        }
        canDonate.setItems(FXCollections.observableList(leftOverOrgans));

    }

    /**
     * Adds the selected organ to currently donating for the current user
     */
    @FXML
    void donate() {
        Organs toDonate = canDonate.getSelectionModel().getSelectedItem();
        if(toDonate != null) {
            if (currentUser.getReceiverDetails().isCurrentlyWaitingFor(toDonate)) {
                currentUser.getCommonOrgans().add(toDonate);
                int index = currentlyDonating.getItems().indexOf(toDonate);
                currentlyDonating.getSelectionModel().select(index);
            }
            currentlyDonating.getItems().add(toDonate);
            currentUser.getDonorDetails().addOrgan(toDonate);
            appController.update(currentUser);
            canDonate.getItems().remove(toDonate);
            Log.info("Donate organ successful");
        } else {
            Log.warning("Donate organs failed, no organs selected.");
        }
    }

    /**
     * Removes the selected organ from currently donating for the current user
     */
    @FXML
    void undonate() {
        if (!currentlyDonating.getSelectionModel().isEmpty()) {
            Organs toUndonate = currentlyDonating.getSelectionModel().getSelectedItem();
        if(toUndonate != null) {
            if(currentUser.getCommonOrgans().contains(toUndonate)) {
                currentUser.getCommonOrgans().remove(toUndonate);
            }
            currentlyDonating.getItems().remove(toUndonate);
            canDonate.getItems().add(toUndonate);
            currentUser.getDonorDetails().removeOrgan(toUndonate);
            appController.update(currentUser);
            Log.info("Un-donate organ successful");
            }
        } else {
            Log.warning("Un-donate organs failed, no organs selected.");
        }
    }

}

