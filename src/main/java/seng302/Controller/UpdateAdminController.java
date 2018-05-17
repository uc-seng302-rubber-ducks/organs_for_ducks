package seng302.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import seng302.Model.Administrator;
import seng302.Service.Log;

import java.util.Optional;

import static seng302.Service.UndoHelpers.removeFormChanges;

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
  private Button redoAdminUpdateButton;

  @FXML
  private Button undoAdminUpdateButton;

  @FXML
  private Label errorLabel;

  private Administrator admin;
  private Stage stage;
  private boolean valid;
  private boolean newAdmin;
  private Administrator oldAdmin;
  private int undoMarker;
  private AppController appController;
  private  AdministratorViewController adminViewController;




  /**
   *
   * @param administrator .
   * @param stage .
   */
  public void init(Administrator administrator, Stage stage, boolean newAdmin) {
    this.admin = administrator;
    this.newAdmin = newAdmin;
    this.stage = stage;

    appController = AppController.getInstance();
    adminViewController = appController.getAdministratorViewController();

    stage.getScene();
    errorLabel.setText("");

    if (!newAdmin) {
      oldAdmin = admin.clone();
      undoMarker = admin.getUndoStack().size();

      undoAdminUpdateButton.setDisable(true);
      redoAdminUpdateButton.setDisable(true);

      prefillFields();

      changesListener(usernameTextField);
      changesListener(firstNameTextField);
      changesListener(middleNameTextField);
      changesListener(lastNameTextField);
      changesListener(passwordTextField);
      changesListener(cPasswordTextField);

    } else {
      undoAdminUpdateButton.setVisible(false);
      redoAdminUpdateButton.setVisible(false);
    }
  }

  /**
   * Listens for changes on all of the text fields.
   *
   * @param field The current textfield/password field
   */
  private void changesListener(TextField field) {
    field.textProperty().addListener((observable, oldValue, newValue) -> updateAdminUndos());
  }

  /**
   * updates the undo stack
   */
  private void updateAdminUndos() {
    boolean changed;

    changed = updateAdminDetails(usernameTextField.getText(), firstNameTextField.getText(),
            middleNameTextField.getText(), lastNameTextField.getText());

    if (changed) {
      prefillFields();
//      admin.getRedoStack().clear();
    }

    undoAdminUpdateButton.setDisable(admin.getUndoStack().size() <= undoMarker);
    redoAdminUpdateButton.setDisable(admin.getRedoStack().isEmpty());
  }


  /**
   * Updates details of an admin that have been changed
   * @param username new username
   * @param firstName new first name
   * @param middleName new middle name
   * @param lastName new last name
   * @return true if any fields were changed
   */
  private boolean updateAdminDetails(String username, String firstName, String middleName, String lastName) {
    boolean changed = false;

    if (!admin.getUserName().equals(username)) {
      admin.setUserName(username);
      changed = true;
    }

    if (!admin.getFirstName().equals(firstName)) {
      admin.setFirstName(firstName);
      changed = true;
    }

    if (admin.getMiddleName() != null ) {
      if (!admin.getMiddleName().equals(middleName)) {
        admin.setMiddleName(middleName);
        changed = true;
      }
    } else {
      if (!middleName.isEmpty()) {
        admin.setMiddleName(middleName);
        changed = true;
      }
    }

    if (admin.getLastName() != null && !admin.getLastName().equals(username)) {
      admin.setLastName(lastName);
      changed = true;
    } else if (admin.getLastName() == null && !lastName.isEmpty()) {
      admin.setLastName(lastName);
      changed = true;
    }

    return changed;
  }


    /**
     * Prefills all the text fields as the attribute values.
     * If the attributes are null, then the fields are set as empty strings.
     */
  private void prefillFields(){
    usernameTextField.setText(admin.getUserName());
    firstNameTextField.setText(admin.getFirstName());

    if (admin.getMiddleName() != null) {
      middleNameTextField.setText(admin.getMiddleName());
    }

    if (admin.getLastName() != null) {
      lastNameTextField.setText(admin.getLastName());
    }
  }

  /**
   *
   */
  private void updateAdmin(){
    valid = true;
    // waiting for the string validation to be finished
    if (!usernameTextField.getText().isEmpty() && !usernameTextField.getText().equals(admin.getUserName())) {
      admin.setUserName(usernameTextField.getText());
    }
    if (!firstNameTextField.getText().isEmpty() && !firstNameTextField.getText().equals(admin.getFirstName())) {
      admin.setFirstName(firstNameTextField.getText());
    }
    if (!middleNameTextField.getText().isEmpty() && !middleNameTextField.getText().equals(admin.getMiddleName())) {
      admin.setMiddleName(middleNameTextField.getText());
    }

    if (!lastNameTextField.getText().isEmpty() && !lastNameTextField.getText().equals(admin.getLastName())) {
      admin.setLastName(lastNameTextField.getText());
    }

    if (!passwordTextField.getText().isEmpty() && !cPasswordTextField.getText().isEmpty()) {
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
    if (valid) {
    try {
      adminViewController.displayDetails(admin);
    } catch (NullPointerException ex) {
      Log.warning(ex.getMessage(), ex);
      //the text fields etc. are all null
    }
    stage.close();
    }
  }

  @FXML
  public void redoAdminUpdate() {
    admin.redo();
    redoAdminUpdateButton.setDisable(admin.getRedoStack().isEmpty());
    prefillFields();

  }

  @FXML
  public void undoAdminUpdate() {
    admin.undo();
    undoAdminUpdateButton.setDisable(admin.getUndoStack().isEmpty());
    prefillFields();
  }




  /**
   *If changes are present, a pop up alert is displayed.
   * Closes the window without making any changes.
   */
 @FXML
 private void cancelUpdate() {
   Alert alert = new Alert(Alert.AlertType.WARNING,
           "You have unsaved changes, are you sure you want to cancel?",
           ButtonType.YES, ButtonType.NO);

   Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
   yesButton.setId("yesButton");

   Optional<ButtonType> result = alert.showAndWait();
   if (result.get() == ButtonType.YES) {


     removeFormChanges(0, admin, undoMarker);
     admin.getRedoStack().clear();


     appController.updateAdmin(oldAdmin);
     adminViewController.displayDetails(oldAdmin);
     stage.close();
   }


 }


}
