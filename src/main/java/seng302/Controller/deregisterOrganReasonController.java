package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seng302.Model.Disease;
import seng302.Model.User;
import seng302.Service.AttributeValidation;

import java.time.LocalDate;

/**
 * Controller class for  for clinicians to
 * enter reasons for de-registering a receiver's organ.
 * @author acb116
 */
public class deregisterOrganReasonController {

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
     * Cancels the de-registration process
     * @param event passed in automatically by the gui
     */
    @FXML
    void cancel(ActionEvent event) {
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
}
