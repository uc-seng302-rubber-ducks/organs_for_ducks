package seng302.Controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import seng302.Model.Organs;
import seng302.Model.User;

public class receiverOrganDateController {

    @FXML
    private Label organNameLabel;

    @FXML
    private TableView<?> organTimeTable;

    @FXML
    private Button backButton;

    private AppController appController;
    private User user;
    private Stage stage;
    private ObservableList medicationDurations;

    /**
     * Initializes the receiverOrganDateView and passes important settings into the controller
     *
     * @param appController application state itself
     * @param user current user for this view
     * @param stage stage that the scene is shown on
     * @param organ having its history shown
     */
    public void init(AppController appController, User user, Stage stage, Organs organ){
        this.appController = appController;
        this.user = user;
        this.stage = stage;
        organNameLabel.setText(organ.organName);

    }


    /**
     * Closes the stage on back button being pressed
     * @param event passed in automatically by the gui
     */
    @FXML
    void back(ActionEvent event) {
        stage.close();

    }

}
