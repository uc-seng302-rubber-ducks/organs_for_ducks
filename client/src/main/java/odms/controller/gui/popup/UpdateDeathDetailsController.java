package odms.controller.gui.popup;

import javafx.stage.Stage;
import odms.commons.model.User;
import odms.controller.AppController;

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
}
