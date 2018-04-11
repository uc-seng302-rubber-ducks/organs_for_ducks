package seng302.Controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seng302.Model.UndoRedoStacks;
import seng302.Model.User;

/**
 * Class for controlling the miscallanous attributes view
 *
 * @author Josh Burt
 */
public class MiscAttributesController {

  @FXML
  private Label nameLabel;

  @FXML
  private ListView<String> attributesList;

  @FXML
  private Button addAttributeButton;

  @FXML
  private Button removeButton;

  @FXML
  private TextField attributeTextFeild;

  @FXML
  private Button backButton;

  private User currentUser;
  private AppController appController;
  private Stage stage;

  /**
   * Initializes the Misc Attributes Controller
   * @param user The current user.
   * @param appController An instance of AppController.
   * @param stage The applications stage.
   */
  public void init(User user, AppController appController, Stage stage) {
    currentUser = user;
    this.appController = appController;
    this.stage = stage;
    attributesList.setItems(FXCollections.observableList(user.getMiscAttributes()));

  }

  /**
   * @param event passed in automatically by the gui
   */
  @FXML
  void addAttribute(ActionEvent event) {
    UndoRedoStacks.storeUndoCopy(currentUser);
    String toAdd = attributeTextFeild.getText();
    attributeTextFeild.setText("");
    if (toAdd == null) {
      return;
    }
    currentUser.addAttribute(toAdd);
    attributesList.setItems(FXCollections.observableList(currentUser.getMiscAttributes()));
    appController.update(currentUser);
  }

  /**
   * @param event passed in automatically by the gui
   */
  @FXML
  void removeAttribute(ActionEvent event) {
    UndoRedoStacks.storeUndoCopy(currentUser);
    String selected = attributesList.getSelectionModel().getSelectedItem();
    attributesList.getItems().remove(selected);
    currentUser.removeMiscAttribute(selected);
    appController.update(currentUser);
  }

  /**
   * @param event passed in automatically by the gui
   */
  @FXML
  void goBack(ActionEvent event) {
    AppController appController = AppController.getInstance();
    DonorController donorController = appController.getDonorController();
    try {
      donorController.showUser(currentUser);
    }
    catch (NullPointerException ex) {
      //TODO causes npe if donor is new in this session
      //the text fields etc. are all null
    }
    stage.close();
  }

}
