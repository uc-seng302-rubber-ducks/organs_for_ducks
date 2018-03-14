package seng302.Controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seng302.Model.Donor;

import java.util.ArrayList;

public class MiscAttributesController {

    @FXML
    private Label nameLabel;

    @FXML
    private ListView<String> attributesList;

    @FXML
    private Button addAttributeButton;

    @FXML
    private Button removeButton;

    @FXML
    private TextField attributeTextFeild;

    @FXML
    private Button backButton;

    private Donor currentDonor;
    private AppController appController;
    private Stage stage;

    public void init(Donor donor, AppController appController, Stage stage){
        currentDonor = donor;
        this.appController = appController;
        this.stage = stage;
        attributesList.setItems(FXCollections.observableList(donor.getMiscAttributes()));

    }

    @FXML
    void addAttribute(ActionEvent event) {
        String toAdd = attributeTextFeild.getText();
        if(toAdd == null){
            return;
        }
        currentDonor.addAttribute(toAdd);
        attributesList.getItems().add(toAdd);
        appController.update(currentDonor);
    }

    @FXML
    void removeAttribute(ActionEvent event) {
        String selected =  attributesList.getSelectionModel().getSelectedItem();
        attributesList.getItems().remove(selected);
        currentDonor.removeMiscAttribute(selected);
        appController.update(currentDonor);
    }

    @FXML
    void goBack(ActionEvent event){
        stage.close();
    }

}
