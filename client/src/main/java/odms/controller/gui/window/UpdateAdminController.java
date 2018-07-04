package odms.controller.gui.window;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import odms.controller.AppController;
import odms.model.Administrator;
import odms.utils.Log;

import java.util.Optional;

import static odms.utils.AttributeValidation.checkRequiredStringName;
import static odms.utils.UndoHelpers.removeFormChanges;

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
    private Label invalidUsername;

    @FXML
    private Label invalidFName;

    @FXML
    private Label errorLabel;

    @FXML
    private Label adminDetailInputTitle;

    private Administrator admin;
    private Stage stage;
    private boolean valid;
    private boolean newAdmin;
    private Administrator adminClone;
    private int undoMarker;
    private AppController appController;
    private AdministratorViewController adminViewController;


    /**
     * @param administrator .
     * @param stage         .
     */
    public void init(Administrator administrator, Stage stage, boolean newAdmin) {
        this.admin = administrator;
        this.newAdmin = newAdmin;
        this.stage = stage;

        appController = AppController.getInstance();
        adminViewController = appController.getAdministratorViewController();

        stage.getScene();
        invalidUsername.setText("");
        invalidFName.setText("");
        errorLabel.setText("");

        if (!newAdmin) {
            adminClone = Administrator.clone(admin);
            undoMarker = adminClone.getUndoStack().size();

            undoAdminUpdateButton.setDisable(true);
            redoAdminUpdateButton.setDisable(true);

            stage.setTitle("Update Administrator: " + admin.getFirstName());
            prefillFields();

            changesListener(usernameTextField);
            changesListener(firstNameTextField);
            changesListener(middleNameTextField);
            changesListener(lastNameTextField);
            changesListener(passwordTextField);
            changesListener(cPasswordTextField);

        } else {
            adminDetailInputTitle.setText("Create Admin");
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
        field.textProperty().addListener((observable, oldValue, newValue) -> detectChanges());
    }

    /**
     * Called by the textfield listeners to update the title bar depending on changes made to the undo stack.
     */
    private void detectChanges() {
        updateAdminUndos();

        if (undoAdminUpdateButton.isDisabled() && passwordTextField.getText().isEmpty() && cPasswordTextField.getText().isEmpty()) {
            stage.setTitle("Update Administrator: " + admin.getFirstName());
        } else if (!stage.getTitle().endsWith("*")) {
            stage.setTitle(stage.getTitle() + " *");
        }
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
            //adminClone.getRedoStack().clear(); //TODO
        }

        undoAdminUpdateButton.setDisable(adminClone.getUndoStack().size() <= undoMarker);
        redoAdminUpdateButton.setDisable(adminClone.getRedoStack().isEmpty());
    }


    /**
     * Updates all attributes that have been changed for the cloned admin.
     *
     * @param username   new username
     * @param firstName  new first name
     * @param middleName new middle name
     * @param lastName   new last name
     * @return true if any fields were changed
     */
    private boolean updateAdminDetails(String username, String firstName, String middleName, String lastName) {
        boolean changed = false;

        if (!adminClone.getUserName().equals(username)) {
            adminClone.setUserName(username);
            changed = true;
        }

        if (!adminClone.getFirstName().equals(firstName)) {
            adminClone.setFirstName(firstName);
            changed = true;
        }

        if (adminClone.getMiddleName() != null) {
            if (!adminClone.getMiddleName().equals(middleName)) {
                adminClone.setMiddleName(middleName);
                changed = true;
            }
        } else {
            if (!middleName.isEmpty()) {
                adminClone.setMiddleName(middleName);
                changed = true;
            }
        }
        //Why is this different to the middle name one?
        if (adminClone.getLastName() != null && !adminClone.getLastName().equals(username)) {
            adminClone.setLastName(lastName);
            changed = true;
        } else if (adminClone.getLastName() == null && !lastName.isEmpty()) {
            adminClone.setLastName(lastName);
            changed = true;
        }

        return changed;
    }


    /**
     * Pre-fills all the text fields as the attribute values.
     * If the attributes are null, then the fields are set as empty strings.
     */
    private void prefillFields() {
        usernameTextField.setText(adminClone.getUserName());
        firstNameTextField.setText(adminClone.getFirstName());

        if (adminClone.getMiddleName() != null) {
            middleNameTextField.setText(adminClone.getMiddleName());
        } else {
            middleNameTextField.setText("");
        }

        if (adminClone.getLastName() != null) {
            lastNameTextField.setText(adminClone.getLastName());
        } else {
            lastNameTextField.setText("");
        }
    }

    /**
     * Updates the original administrator.
     * This is run on-exit when the confirm button is clicked.
     */
    private void updateAdmin() {
        valid = true;
        invalidUsername.setVisible(false);
        invalidFName.setVisible(false);
        errorLabel.setVisible(false);
        // waiting for the string validation to be finished
        if (!usernameTextField.getText().isEmpty() && !usernameTextField.getText().equals(admin.getUserName())) {
            Administrator foundAdministrator = appController.getAdministrator(usernameTextField.getText());

            if (!(foundAdministrator == null || (!newAdmin && usernameTextField.getText().equals(admin.getUserName())))) { // no clinician exists with the updated staff ID
                invalidUsername.setText("Staff username already in use");
                invalidUsername.setVisible(true);
                valid = false;
            } else {
                admin.setUserName(usernameTextField.getText());
            }
        }
        if (!firstNameTextField.getText().equals(admin.getFirstName())) {
            if (checkRequiredStringName(firstNameTextField.getText())) {
                admin.setFirstName(firstNameTextField.getText());
            } else {
                invalidFName.setText("That is not a valid first name");
                invalidFName.setVisible(true);
                valid = false;
            }
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
                errorLabel.setText("Your passwords don't match");
                errorLabel.setVisible(true);
                valid = false;
            }
        }
        admin.getRedoStack().clear();
    }


    /**
     * checks that all input is valid then updates the Admin
     */
    @FXML
    private void confirmUpdate() {
        updateAdmin();
        if (valid) {
            if (newAdmin) {
                appController.updateAdmin(admin);
            }
            try {
                adminClone.getRedoStack().clear();
                adminViewController.displayDetails();
            } catch (NullPointerException ex) {
                Log.warning(ex.getMessage(), ex);
                //the text fields etc. are all null
            }

            stage.close();
        }
    }

    @FXML
    public void redoAdminUpdate() {
        adminClone.redo();
        redoAdminUpdateButton.setDisable(adminClone.getRedoStack().isEmpty());
        prefillFields();

    }

    @FXML
    public void undoAdminUpdate() {
        adminClone.undo();
        undoAdminUpdateButton.setDisable(adminClone.getUndoStack().isEmpty());
        prefillFields();
    }

    /**
     * If changes are present, a pop up alert is displayed.
     * Closes the window without making any changes.
     */
    @FXML
    private void cancelUpdate() {
        if (!newAdmin) {
            if (!undoAdminUpdateButton.isDisabled() || !passwordTextField.getText().isEmpty() || !cPasswordTextField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                        "You have unsaved changes, are you sure you want to cancel?",
                        ButtonType.YES, ButtonType.NO);

                Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
                yesButton.setId("yesButton");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.YES) {
                    removeFormChanges(0, adminClone, undoMarker);
                    adminClone.getRedoStack().clear();
                    adminViewController.displayDetails();
                    stage.close();
                }

            } else {
                admin.getRedoStack().clear();
                stage.close();
            }
        } else {
            stage.close();
        }
    }


}
