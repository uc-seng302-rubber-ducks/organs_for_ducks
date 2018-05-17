package seng302.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
  private Label errorLabel;

  private Administrator admin;
  private Stage stage;
  private boolean valid;




  /**
   *
   * @param administrator .
   * @param stage .
   */
  public void init(Administrator administrator, Stage stage) {
    admin = administrator;
    this.stage = stage;
    prefillFields();
    stage.getScene();
    errorLabel.setText("");

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
    valid = true;
    // waiting for the string validation to be finished
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
        valid = false;
      }
    }

  }


  /**
   *checks that all input is valid then updates the Admin
   */
  @FXML
  private void confirmUpdate(){
    updateAdmin();
    if (valid){
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
  }


  /**
   *If changes are present, a pop up alert is displayed.
   * Closes the window without making any changes.
   */
 @FXML
 private void cancelUpdate() {
   stage.close();
 }


}
