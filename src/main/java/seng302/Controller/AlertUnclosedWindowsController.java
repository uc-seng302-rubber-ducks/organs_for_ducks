package seng302.Controller;

import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertUnclosedWindowsController {
    Stage stage;

    /**
     * Initializes the AlertUnclosedWindowsController
     * @param stage The applications stage.
     */
    public void init(Stage stage){
        this.stage = stage;
        //disable all other stages when this window is opened.
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    /**
     * Closes current window.
     */
    @FXML
    private void closeWindow() {
        stage.close();
    }
}
