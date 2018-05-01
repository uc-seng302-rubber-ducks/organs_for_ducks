package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import seng302.Model.OrganDeregisterReason;
import seng302.Model.Organs;
import seng302.Model.User;

/**
 * Controller class for  for clinicians to
 * enter reasons for de-registering a receiver's organ.
 * @author acb116
 */
public class deregisterOrganReasonController {

    @FXML
    private RadioButton registerationErrorRadioButton;

    @FXML
    private RadioButton diseaseCuredRadioButton;

    @FXML
    private RadioButton receiverDiedRadioButton;

    @FXML
    private Label receiverName;

    @FXML
    private Label organName;

    AppController controller;
    Stage stage;
    private User currentUser;
    private DonorController donorController;
    private String toDeRegister;

    /**
     * Initializes the NewDiseaseController
     * @param organ name of receiver organ to be de-registered
     * @param donorController class
     * @param controller The applications controller.
     * @param stage The applications stage.
     */
    public void init(String organ, DonorController donorController, User user, AppController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
        currentUser = user;
        this.donorController = donorController;
        receiverName.setText(user.getName());
        organName.setText(organ);
        this.toDeRegister = organ;
        //stage.setMinWidth(620);
        //stage.setMaxWidth(620);
    }

    /**
     * Cancels the de-registration process
     * and closes this window
     * @param event passed in automatically by the gui
     */
    @FXML
    void cancelDeregistration(ActionEvent event) {
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
     * accepts the de-registration reason
     * and closes this window
     * @param event passed in automatically by the gui
     */
    @FXML
    void acceptDeregistration(ActionEvent event) {

        if(registerationErrorRadioButton.isSelected()){
            donorController.setOrganDeregisterationReason(OrganDeregisterReason.REGISTRATION_ERROR);

        } else if (diseaseCuredRadioButton.isSelected()){
            donorController.setOrganDeregisterationReason(OrganDeregisterReason.DISEASE_CURED);

        } else if(receiverDiedRadioButton.isSelected()){
            donorController.setOrganDeregisterationReason(OrganDeregisterReason.RECEIVER_DIED);
        }

        donorController.deRegisterOrgan(toDeRegister);

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
