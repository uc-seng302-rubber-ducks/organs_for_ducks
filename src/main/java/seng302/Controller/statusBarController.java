package seng302.Controller;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class statusBarController {


  @FXML
  private Label statusBar;

  private AppController application;
  private UserController parent;

  @FXML
  public void init(AppController controller) {
    application = controller;
    updateStatus("");
  }

  public void updateStatus(String update){
    statusBar.setText(update);
    PauseTransition statusBarWaiter = new PauseTransition(Duration.seconds(5));
    statusBarWaiter.setOnFinished(event -> statusBar.setText(""));
    statusBarWaiter.play();
  }
}
