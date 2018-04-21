package seng302.Controller;

import javafx.stage.Stage;
import seng302.Model.User;

public class ReceiverOrganController {

    AppController controller;
    Stage stage;
    private User currentUser;


    /**
     * Initializes the ReceiverOrganController
     * @param controller The applications controller.
     * @param stage The applications stage.
     */
    public void init(User user, AppController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
        currentUser = user;
        //showCurrentDate();
        //stage.setMinWidth(620);
        //stage.setMaxWidth(620);
    }

}
