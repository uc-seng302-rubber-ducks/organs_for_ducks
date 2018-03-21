package seng302.Controller;


import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import seng302.Model.Donor;
import seng302.Model.Organs;
import seng302.Model.UndoRedoStacks;

import java.util.ArrayList;
import java.util.Collections;

/**
 * class for the Organs view
 *
 * @author Josh Burt
 */
public class OrganController {

    @FXML
    private ListView<Organs> currentlyDonating;

    @FXML
    private ListView<Organs> canDonate;

    @FXML
    private Button donateButton;

    @FXML
    private Button undonateButton;

    @FXML
    private Label donorNameLabel;

    @FXML
    private Button backButton;

    private AppController appController;
    private Donor currentDonor;
    private Stage stage;

    public void init(Donor donor, AppController controller, Stage stage){
        this.stage = stage;
        this.appController = controller;
        currentDonor = donor;
        donorNameLabel.setText(donor.getName());
        ArrayList<Organs> donating;
        try {
            donating= new ArrayList<>(donor.getOrgans());
        }
        catch (NullPointerException ex) {
            donating = new ArrayList<>();
        }
        currentlyDonating.setItems(FXCollections.observableList(donating));
        ArrayList<Organs> leftOverOrgans = new ArrayList<Organs>();
        Collections.addAll(leftOverOrgans, Organs.values());
        for (Organs o : donating){
            leftOverOrgans.remove(o);
        }
        canDonate.setItems(FXCollections.observableList(leftOverOrgans));

    }

    @FXML
    void donate(ActionEvent event) {
        UndoRedoStacks.storeUndoCopy(currentDonor);
        Organs toDonate = canDonate.getSelectionModel().getSelectedItem();
        currentlyDonating.getItems().add(toDonate);
        currentDonor.addOrgan(toDonate);
        appController.update(currentDonor);
        canDonate.getItems().remove(toDonate);
    }

    @FXML
    void undonate(ActionEvent event) {
        UndoRedoStacks.storeUndoCopy(currentDonor);
        Organs toUndonate = currentlyDonating.getSelectionModel().getSelectedItem();
        currentlyDonating.getItems().remove(toUndonate);
        canDonate.getItems().add(toUndonate);
        currentDonor.removeOrgan(toUndonate);
        appController.update(currentDonor);
    }

    @FXML
    void goBack(ActionEvent event) {
        AppController appController = AppController.getInstance();
        DonorController donorController = appController.getDonorController();
        try {
            donorController.showDonor(currentDonor);
        }
        catch (NullPointerException ex) {
            //TODO causes npe if donor is new in this session
            //the text fields etc. are all null
        }
        stage.close();
    }

}

