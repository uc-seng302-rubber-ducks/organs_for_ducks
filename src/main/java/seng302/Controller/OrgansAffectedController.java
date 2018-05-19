package seng302.Controller;

import java.util.*;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import seng302.Model.MedicalProcedure;
import seng302.Model.Organs;
import seng302.Model.User;
import seng302.Service.Log;

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
     * @param appController given app controller
     * @param stage given stage
     * @param procedure current procedure
     * @param user current user
     */
    public void init(AppController appController, Stage stage, MedicalProcedure procedure, User user) {
        organsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        affectedOrgansListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        organsListView.getSelectionModel().selectedItemProperty().addListener(ListChangeListener->{
            if(organsListView.getSelectionModel().getSelectedItem() != null)
            affectedOrgansListView.getSelectionModel().clearSelection();
        });
        affectedOrgansListView.getSelectionModel().selectedItemProperty().addListener(ListChangeListener ->{
                    if(affectedOrgansListView.getSelectionModel().getSelectedItem() != null){
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
        if(toAffect != null) {
            currentProcedure.addOrgan(toAffect);
            affectedOrgansListView.setItems(FXCollections.observableList(currentProcedure.getOrgansAffected()));
            organsListView.getItems().remove(toAffect);
            Log.info("Successfully added the selected organ to the affected list for the current medical procedure");
        } else {
            Log.warning("Unable to add the organ to the affected list as there is no organ selected");
        }
    }

    /**
     * Removes the selected organ from the affected list for the current medical procedure
     */
    @FXML
    void removeOrgan() {
        Organs toAffect = affectedOrgansListView.getSelectionModel().getSelectedItem();
        if(toAffect != null) {
            currentProcedure.removeOrgan(toAffect);
            affectedOrgansListView.setItems(FXCollections.observableList(currentProcedure.getOrgansAffected()));
            organsListView.getItems().add(toAffect);
            Log.info("Successfully removed the selected organ from the affected list for the current medical procedure");
        } else {
            Log.warning("Unable to remove the organ from the affected list as there is no organ selected");
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
