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
   * @param administrator .
   * @param controller .
   * @param stage .
   */
  public void init(Administrator administrator, AppController controller, Stage stage) {
    admin = administrator;
    this.stage = stage;
    this.appController = controller;
    prefillFields();
    Scene scene = stage.getScene();

  }

    /**
     * Prefills all the text fields as the attribute values.
     * If the attributes are null, then the fields are set as empty strings.
     */
  private void prefillFields(){
    usernameTextField.setText(admin.getUserName());
    firstNameTextField.setText(admin.getFirstName());
    if (!admin.getMiddleName().isEmpty()){
      middleNameTextField.setText(admin.getMiddleName());
    }
    if (!admin.getLastName().isEmpty()) {
      lastNameTextField.setText(admin.getLastName());
    }
    passwordTextField.clear();
    cPasswordTextField.clear();
  }

  /**
   *
   */
  private void updateAdmin(){
    if (!usernameTextField.getText().isEmpty() && !usernameTextField.getText().equals(admin.getUserName())){
      admin.setUserName(usernameTextField.getText());
    }
    if (!firstNameTextField.getText().isEmpty() && !firstNameTextField.getText().equals(admin.getFirstName())){
      admin.setFirstName(firstNameTextField.getText());
    }
    if (!middleNameTextField.getText().isEmpty() && !middleNameTextField.getText().equals(admin.getMiddleName())){
      admin.setMiddleName(middleNameTextField.getText());
    }

    if (!lastNameTextField.getText().isEmpty() && !lastNameTextField.getText().equals(admin.getLastName())){
      admin.setLastName(lastNameTextField.getText());
    }

    if (!passwordTextField.getText().isEmpty() && !cPasswordTextField.getText().isEmpty()){
      if (passwordTextField.getText().equals(cPasswordTextField.getText())) {
        admin.setPassword(passwordTextField.getText());
      } else {
        errorLabel.setText("your password don't match");
      }
    }

  }


  /**
   *checks that all input is valid then updates the Admin
   */
  @FXML
  private void confirmUpdate(){
    updateAdmin();
    AppController appController = AppController.getInstance();
    AdministratorViewController administratorViewController = appController.getAdministratorViewControlloer();
    try {
      administratorViewController.displayDetails();
    } catch (NullPointerException ex) {
      //TODO causes npe if donor is new in this session
      //the text fields etc. are all null
    }
    stage.close();
  }


  /**
   *If changes are present, a pop up alert is displayed.
   * Closes the window without making any changes.
   */
 @FXML
  private void cancelUpdate(){
   stage.close();
//   if (stage.getTitle().equals("Update Administrator: " + admin.getFirstName() + " *")){
//     Alert alert = new Alert(AlertType.WARNING,"You have unsaved changes, are you sure you want to cancel?",
//         ButtonType.YES,ButtonType.NO);
//     Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
//     yesButton.setId("yesButton");
//
//     Optional<ButtonType> result = alert.showAndWait();
//     if (result.get() == ButtonType.YES) {
//       AppController appController = AppController.getInstance();
//     }
//
//
//   }




 }


}
