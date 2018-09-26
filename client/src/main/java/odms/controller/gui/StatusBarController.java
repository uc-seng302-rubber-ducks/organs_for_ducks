package odms.controller.gui;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class StatusBarController {


    @FXML
    private Label statusBar;


    @FXML
    public void init() {
        updateStatus("");
    }

    /**
     * when a change is made the status bar is set and is cleared after 5 sec
     *
     * @param update the string that the label is to be changed to
     */
    public void updateStatus(String update) {
        statusBar.setText(update);
        PauseTransition statusBarWaiter = new PauseTransition(Duration.seconds(5));
        statusBarWaiter.setOnFinished(event -> statusBar.setText(""));
        statusBarWaiter.play();
    }
}
