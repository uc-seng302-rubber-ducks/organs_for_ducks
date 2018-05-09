package seng302.Controller;

import java.util.Optional;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import seng302.Model.Administrator;
import seng302.Model.User;

public class UpdateAdminController {
  @FXML
  private TextField usernameTextField;

  @FXML
  private TextField firstNameTextField;

  @FXML
  private TextField middleNameTextField;

  @FXML
  private TextField lastNameTextField;

  @FXML
  private TextField passwordTextField;

  @FXML
  private TextField cPasswordTextField;

  @FXML
  private Button cancelButton;

  @FXML
  private Button confirmButton;

  @FXML
  private Label invalidUsername;

  @FXML
  private  Label invaildFName;

  @FXML
  private Label errorLabel;

  private Administrator admin;
  private Stage stage;
  private AppController appController;




  /**
   *
   * @param administrator
   * @param controller
   * @param stage
   */
  public void init(Administrator administrator, AppController controller, Stage stage) {
    admin = administrator;
    this.stage = stage;
    this.appController = controller;
    prefillFields(admin);
    Scene scene = stage.getScene();

  }

    /**
     * Prefills all the text fields as the attribute values.
     * If the attributes are null, then the fields are set as empty strings.
     */
  private void prefillFields(Administrator admin){
    usernameTextField.setText(admin.getUserName());
    firstNameTextField.setText(admin.getFirstName());
    if (!admin.getMiddleName().isEmpty()){
      middleNameTextField.setText(admin.getMiddleName());
    }
    if (!admin.getLastName().isEmpty()) {
      lastNameTextField.setText(admin.getLastName());
    }
  }

  private void updateAdmin(){


  }


  /**
   *checks that all input is valid then updates the Admin
   */
  @FXML
  private void confirmUpdate(){
    updateAdmin();
  }


  /**
   *If changes are present, a pop up alert is displayed.
   * Closes the window without making any changes.
   */
 @FXML
  private void cancelUpdate(){
   if (stage.getTitle().equals("Update Administrator: " + admin.getFirstName() + " *")){
     Alert alert = new Alert(AlertType.WARNING,"You have unsaved changes, are you sure you want to cancel?",
         ButtonType.YES,ButtonType.NO);
     Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
     yesButton.setId("yesButton");

     Optional<ButtonType> result = alert.showAndWait();
     if (result.get() == ButtonType.YES) {
       AppController appController = AppController.getInstance();
     }


   }

 }


}
