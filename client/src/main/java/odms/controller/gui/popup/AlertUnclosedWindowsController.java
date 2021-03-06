package odms.controller.gui.popup;

import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.Stage;
import odms.controller.gui.window.AdministratorViewController;

public class AlertUnclosedWindowsController {
    private Stage stage;
    private AdministratorViewController controller;

    /**
     * Initializes the AlertUnclosedWindowsController
     *
     * @param stage The applications stage.
     * @param controller the admin controller to close all windows of
     */
    public void init(Stage stage, AdministratorViewController controller) {
        this.stage = stage;
        this.controller = controller;
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

    /**
     * closes all other windows apart from
     * administrator overview.
     */
    @FXML
    private void closeAllWindows() {
        controller.closeAllWindows();
        stage.close();
    }
}
