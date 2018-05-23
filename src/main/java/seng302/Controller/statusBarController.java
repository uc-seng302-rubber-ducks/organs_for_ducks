package seng302.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

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
  }
}
