package seng302.controller.gui.popup;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import seng302.controller.AppController;
import seng302.model.MedicalProcedure;
import seng302.model._enum.Organs;
import seng302.model.User;
import seng302.utils.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class for the Organs Affected view for Medical Procedures
 */
public class OrgansAffectedController {

    @FXML
    private ListView<Organs> organsListView;

    @FXML
    private ListView<Organs> affectedOrgansListView;

    private MedicalProcedure currentProcedure;

    private AppController appController;

    private Stage stage;

    private User user;

    /**
     * initialises the organs affected window for medical procedure
     *
     * @param appController given app controller
     * @param stage         given stage
     * @param procedure     current procedure
     * @param user          current user
     */
    public void init(AppController appController, Stage stage, MedicalProcedure procedure, User user) {
        organsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        affectedOrgansListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        organsListView.getSelectionModel().selectedItemProperty().addListener(ListChangeListener -> {
            if (organsListView.getSelectionModel().getSelectedItem() != null)
                affectedOrgansListView.getSelectionModel().clearSelection();
        });
        affectedOrgansListView.getSelectionModel().selectedItemProperty().addListener(ListChangeListener -> {
            if (affectedOrgansListView.getSelectionModel().getSelectedItem() != null) {
                        organsListView.getSelectionModel().clearSelection();
                    }
                }
        );
        this.appController = appController;
        this.stage = stage;
        currentProcedure = procedure;
        this.user = user;
        ArrayList<Organs> allOrgans = new ArrayList<>();
        Collections.addAll(allOrgans, Organs.values());
        List<Organs> affectedOrgans;
        affectedOrgans = procedure.getOrgansAffected();
        allOrgans.removeAll(affectedOrgans);
        affectedOrgansListView.setItems(FXCollections.observableList(affectedOrgans));
        organsListView.setItems(FXCollections.observableList(allOrgans));
    }

    /**
     * Adds the selected organ to the affected list for the current medical procedure
     */
    @FXML
    void addOrgan() {
        Organs toAffect = organsListView.getSelectionModel().getSelectedItem();
        if (toAffect != null) {
            currentProcedure.addOrgan(toAffect);
            affectedOrgansListView.setItems(FXCollections.observableList(currentProcedure.getOrgansAffected()));
            organsListView.getItems().remove(toAffect);
            Log.info("Successfully added the selected organ: " + toAffect + " to the affected list at current medical procedure for User NHI: " + user.getNhi());
        } else {
            Log.warning("Unable to add the organ: null to the affected list for User NHI: " + user.getNhi());
        }
    }

    /**
     * Removes the selected organ from the affected list for the current medical procedure
     */
    @FXML
    void removeOrgan() {
        Organs toAffect = affectedOrgansListView.getSelectionModel().getSelectedItem();
        if (toAffect != null) {
            currentProcedure.removeOrgan(toAffect);
            affectedOrgansListView.setItems(FXCollections.observableList(currentProcedure.getOrgansAffected()));
            organsListView.getItems().add(toAffect);
            Log.info("Successfully removed the selected organ: " + toAffect + "  from the affected list for the current medical procedure for User NHI: " + user.getNhi());
        } else {
            Log.warning("Unable to remove the organ: null from the affected list");
        }
    }

    /**
     * Goes back to the previous window
     */
    @FXML
    void back() {
        appController.update(user);
        stage.close();
        Log.info("Back button pressed");
    }

}
