package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import seng302.Model.User;

import java.awt.*;
import java.io.IOException;

/**
 * Controller class for creating new disease.
 * @author acb116
 */
public class NewDiseaseController {
//    @FXML
//    public TextField diseaseNameInput;
//
//    @FXML
//    public DatePicker  diagnosisDateInput;

    @FXML
    public RadioButton chronicRadioButton;

    @FXML
    public RadioButton curedRadioButton;

    AppController controller;
    Stage stage;
    private User currentUser;

    /**
     * Initializes the NewDiseaseController
     * @param controller The applications controller.
     * @param stage The applications stage.
     */
    public void init(User user, AppController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
        currentUser = user;
        //stage.setMinWidth(620);
        //stage.setMaxWidth(620);
    }

    /**
     * @param event passed in automatically by the gui
     */
    @FXML
    void cancelCreation(ActionEvent event) {
        AppController appController = AppController.getInstance();
        DonorController donorController = appController.getDonorController();
        try {
            donorController.showUser(currentUser);
        }
        catch (NullPointerException ex) {
            //TODO causes npe if donor is new in this session
            //the text fields etc. are all null
        }
        stage.close();
    }

    /**
     * clears all selecton from
     * radio button that are in diseaseStatus toggle group
     * @throws IOException
     */
    @FXML
    private void clearSelection() throws IOException {
        chronicRadioButton.setSelected(false);
        curedRadioButton.setSelected(false);
    }
}
