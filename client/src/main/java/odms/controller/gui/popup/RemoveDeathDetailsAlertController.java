package odms.controller.gui.popup;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.controller.AppController;
import odms.controller.gui.window.UserController;


/**
 * Controller class for the alert window asking the user if they want to remove death details
 */
public class RemoveDeathDetailsAlertController {

    @FXML
    private Label titleRemoveDeathDetailsAlertLabel;
    @FXML
    private Label infoRemoveDeathDetailsAlertLabel;

    private AppController controller;
    private Stage stage;
    private User user;

    public void init(AppController controller, Stage stage, User user) {
        this.controller = controller;
        this.stage = stage;
        this.user = user;

        String warningTitle = "Do you really want to remove death details for user " + user.getNhi() + "?";
        String warningDetails = "Removing death details will mean this user is treated as alive.\nFor example, " +
                "any organs they were donating will be removed from the currently available organs list.";

        this.titleRemoveDeathDetailsAlertLabel.setText(warningTitle);
        this.infoRemoveDeathDetailsAlertLabel.setText(warningDetails);
        stage.setTitle("Remove death details?");
    }

    /**
     * Returns the user to the update death details window
     */
    @FXML
    private void cancelRemoveDeathDetails() {
        stage.close();
    }

    /**
     * Closes the alert window and the update death details window after setting the related death details fields to null or "".
     */
    @FXML
    public void confirmRemoveDeathDetails() {
        UserController userController = controller.getUserController();
        user.setMomentOfDeath(null);
        user.setDeathCity("");
        user.setDeathRegion("");
        user.setDeathCountry("");
        user.getRedoStack().clear();
        stage.close();
        userController.showUser(user);
    }


}
