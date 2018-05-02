package seng302.Controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import seng302.Model.MedicalProcedure;
import seng302.Model.Organs;
import seng302.Model.User;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class for the Organs Affected view for Medical Procedures
 */
public class OrgansAffectedController {

    @FXML
    private Button addOrganButton;

    @FXML
    private Button removeOrganButton;

    @FXML
    private Button backButton;

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
        ArrayList affectedOrgans;
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
        }
    }

    /**
     * Goes back to the previous window
     */
    @FXML
    void back() {
        appController.update(user);
        stage.close();
    }

}
