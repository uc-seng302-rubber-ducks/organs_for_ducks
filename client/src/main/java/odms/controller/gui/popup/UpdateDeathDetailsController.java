package odms.controller.gui.popup;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.utils.Log;
import odms.controller.AppController;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Controller class for editing death details of a user
 */
public class UpdateDeathDetailsController {

    private AppController controller;
    private Stage stage;
    private User currentUser;

    public void init(AppController controller, Stage stage, User currentUser) {
        this.controller = controller;
        this.stage = stage;
        this.currentUser = currentUser;
    }

    @FXML
    private void cancelUpdateDeathDetails() {
        closeUpdateDeathDetails();
    }

    /**
     * Closes the update death details popup. Called by the Cancel button
     * (Empty for now)
     */
    private void closeUpdateDeathDetails(){
        Log.severe("Not implemented yet", new NotImplementedException());
    }

    @FXML
    private void confirmUpdateDeathDetails() {
        saveUpdateDeathDetails();
        closeUpdateDeathDetails();
    }

    /**
     * Saves the update death details changes to the user. Called by the Confirm button
     * (Empty for now)
     */
    private void saveUpdateDeathDetails() {
        Log.severe("Not implemented yet", new NotImplementedException());
    }

}
