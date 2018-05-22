package seng302.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import seng302.Model.User;

public class statusBarController {


  @FXML
  private Label statusBar;

  private AppController application;
  private UserController parent;

  @FXML
  public void init(AppController controller, UserController userController) {
    application = controller;
    parent = userController;
    updateStatus("");
  }


  public void updateStatus(String update){
    statusBar.setText(update);
  }
}
