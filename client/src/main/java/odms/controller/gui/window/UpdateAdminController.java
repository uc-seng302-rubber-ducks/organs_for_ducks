package odms.controller.gui.window;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import odms.commons.model.Administrator;
import odms.commons.utils.Log;
import odms.controller.AppController;

import java.util.Optional;

import static odms.commons.utils.AttributeValidation.*;
import static odms.commons.utils.UndoHelpers.removeFormChanges;

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
    private Label invalidMName;

    @FXML
    private Label invalidLName;

    @FXML
    private Label passwordErrorLabel;

    @FXML
    private Label confirmPasswordErrorLabel;

    @FXML
    private Label adminGenericErrorLabel;

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

            if (admin.getUserName().equals("default")) {
                usernameTextField.setDisable(true); // default admin cannot change their staff ID or password
                passwordTextField.setDisable(true);
                cPasswordTextField.setDisable(true);
            }

        } else {
            adminDetailInputTitle.setText("Create Admin");
            undoAdminUpdateButton.setVisible(false);
            redoAdminUpdateButton.setVisible(false);

            styleListener(usernameTextField);
            styleListener(firstNameTextField);
            styleListener(middleNameTextField);
            styleListener(lastNameTextField);
            styleListener(passwordTextField);
            styleListener(cPasswordTextField);
        }
    }

    /**
     * Listens for user input on all of the fields to remove invalid css for that field.
     * Used only when creating a new administrator.
     *
     * @param field The current textfield/password field
     */
    private void styleListener(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            field.getStyleClass().remove("invalid");
        });
    }

    /**
     * Listens for changes on all of the text fields.
     *
     * @param field The current textfield/password field
     */
    private void changesListener(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            field.getStyleClass().remove("invalid");
            detectChanges();
        });
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
        invalidMName.setVisible(false);
        invalidLName.setVisible(false);
        passwordErrorLabel.setVisible(false);
        confirmPasswordErrorLabel.setVisible(false);
        adminGenericErrorLabel.setVisible(false);
        // waiting for the string validation to be finished
        if (!usernameTextField.getText().isEmpty() && !usernameTextField.getText().equals(admin.getUserName())) {
            Administrator foundAdministrator = appController.getAdministrator(usernameTextField.getText());

            if (!(foundAdministrator == null || (!newAdmin && usernameTextField.getText().equals(admin.getUserName())))) { // no clinician exists with the updated staff ID
                invalidateNode(usernameTextField);
                invalidUsername.setText("Staff username already in use");
                invalidUsername.setVisible(true);
                valid = false;
            } else {
                admin.setUserName(usernameTextField.getText());
            }
        } else if (usernameTextField.getText().isEmpty()) {
            invalidateNode(usernameTextField);
            invalidUsername.setText("Staff username cannot be blank");
            invalidUsername.setVisible(true);
            valid = false;
        }

        if (!firstNameTextField.getText().equals(admin.getFirstName()) || newAdmin) {
            if (checkRequiredStringName(firstNameTextField.getText())) {
                admin.setFirstName(firstNameTextField.getText());
            } else {
                invalidateNode(firstNameTextField);
                invalidFName.setText("That is not a valid first name");
                invalidFName.setVisible(true);
                valid = false;
            }
        }
        if (!middleNameTextField.getText().isEmpty() && (!middleNameTextField.getText().equals(admin.getMiddleName()) || newAdmin)) {
            if (checkString(middleNameTextField.getText())) {
                admin.setMiddleName(middleNameTextField.getText());
            } else {
                invalidateNode(middleNameTextField);
                invalidMName.setVisible(true);
                valid = false;
            }
        }

        if (!lastNameTextField.getText().isEmpty() && (!lastNameTextField.getText().equals(admin.getLastName()) || newAdmin)) {
            if (checkString(lastNameTextField.getText())) {
                admin.setLastName(lastNameTextField.getText());
            } else {
                invalidateNode(lastNameTextField);
                invalidLName.setVisible(true);
                valid = false;
            }
        }

        if (!passwordTextField.getText().isEmpty() || newAdmin) {
            String password = cPasswordTextField.getText();
            if (passwordTextField.getText().equals(password)) {
                if (checkRequiredString(passwordTextField.getText())) {
                    admin.setPassword(password);
                } else {
                    displayPasswordError("Only alphanumeric characters are allowed");
                }
            } else {
                displayPasswordError("Your passwords don't match");
            }

        } else if (!cPasswordTextField.getText().isEmpty()) {
            displayPasswordError("Your passwords don't match");
        }

        admin.getRedoStack().clear();
    }

    /**
     * Invalidates the given node by applying red highlighting to the node background.
     *
     * @param node The node to be invalidated
     */
    private void invalidateNode(Node node) {
        node.getStyleClass().add("invalid");
    }

    /**
     * Invalidates both password text fields and displays an error message for each.
     */
    private void displayPasswordError(String errorMessage) {
        invalidateNode(passwordTextField);
        invalidateNode(cPasswordTextField);
        passwordErrorLabel.setText(errorMessage);
        passwordErrorLabel.setVisible(true);
        confirmPasswordErrorLabel.setText(errorMessage);
        confirmPasswordErrorLabel.setVisible(true);
        valid = false;
    }

    /**
     * checks that all input is valid then updates the Admin
     */
    @FXML
    private void confirmUpdate() {
        updateAdmin();
        if (valid) {
            if (newAdmin) {
                appController.saveAdmin(admin);
            }
            try {
                adminClone.getRedoStack().clear();
                adminViewController.displayDetails();
            } catch (NullPointerException ex) {
                Log.warning(ex.getMessage(), ex);
                //the text fields etc. are all null
            }
            adminViewController.refreshTables();
            stage.close();
        } else {
            adminGenericErrorLabel.setVisible(true);
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

                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
                yesButton.setId("yesButton");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
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
