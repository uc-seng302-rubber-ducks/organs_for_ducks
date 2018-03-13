package seng302.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import seng302.Model.Organs;

public class DonorController {

  @FXML
  private TextField nameTextField;
  @FXML
  private DatePicker dateOfBirthPicker;
  @FXML
  private ComboBox<String> genderComboBox;
  @FXML
  private ComboBox<String> bloodTypeComboBox;
  @FXML
  private TextField heightTextField;
  @FXML
  private TextField weightTextField;
  @FXML
  private CheckBox isDonorDeceasedCheckBox;
  @FXML
  private DatePicker dateOfDeathPicker;

  @FXML
  private ListView<Organs> organsDonatingListView;
  @FXML
  private ListView<String> miscAttributeslistView;

  @FXML
  private TextArea currentAddressTextArea;
  @FXML
  private TextField regionTextField;

  @FXML
  private Button undoButton;
  @FXML
  private Button redoButton;

  //NOTE this is a TableView of Objects.
  //specify a type if you need any particular details e.g. from the selected item
  @FXML
  private TableView historyTableView;

  /**
   * fires when the Organs button is clicked
   */
  @FXML
  private void modifyOrgans() {

  }

  /**
   * fires when the Misc button is clicked
   */
  @FXML
  private void modifyMiscAttributes() {

  }

  /**
   * fires when the Confirm button is clicked
   */
  @FXML
  private void updateDonor() {

  }

  /**
   * fires when the Undo button is clicked
   */
  @FXML
  private void undo() {

  }

  /**
   * fires when the Redo button is clicked
   */
  @FXML
  private void redo() {

  }
}
