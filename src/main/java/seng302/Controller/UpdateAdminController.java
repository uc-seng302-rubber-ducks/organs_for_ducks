package seng302.Controller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
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


  /**
   * Prefills all the text fields as the attribute values.
   * If the attributes are null, then the fields are set as empty strings.
   */
  private void prefillFields(Administrator admin){

  }


  /**
   *
   */
  @FXML
  private void confirmUpdate(){

  }


  /**
   *
   */
 @FXML
  private void cancelUpdate(){

 }


}
