package seng302.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import seng302.Model.Organs;

public class ReceiverTabController {

  @FXML
  private ComboBox<Organs> organsComboBox;

  @FXML
  private Label organLabel;

  @FXML
  private ListView<Organs> currentlyReceivingListView;

  @FXML
  private ListView<Organs> notReceivingListView;

  @FXML
  private Label currentlyReceivingLabel;

  @FXML
  private Label notReceivingLabel;

  @FXML
  private Label notReceiverLabel;

  @FXML
  private Button registerButton;

  @FXML
  private Button reRegisterButton;

  @FXML
  private Button deRegisterButton;
}
