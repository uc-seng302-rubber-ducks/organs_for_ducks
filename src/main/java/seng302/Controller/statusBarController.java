package seng302.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class statusBarController {
  @FXML
  private Label statusBar;


  public void updateStatus(String update){
    statusBar.setText(update);
  }
}
