package seng302.Controller;

import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng302.Model.Administrator;
import seng302.Model.Clinician;
import seng302.Model.User;
import seng302.View.CLI;

/**
 * Class for the login functionality of the application
 */
public class LoginController {

    @FXML
    private Button changeLogin;

    @FXML
    private Button loginButton;

    @FXML
    private Button signUpButton;

    @FXML
    private TextField userIDTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> accountTypeComboBox;

    @FXML
    private Label warningLabel;

    @FXML
    private Label idLabel;

    @FXML
    private Label passwordLabel;

    private Stage helpStage = null;
    private boolean isUser = true;
    private AppController appController;
    private ArrayList<User> users;
    private Stage stage;

    /**
     * Initializes the Login controller.
     *
     * @param appController The applications controller.
     * @param stage         The applications stage.
     */
    public void init(AppController appController, Stage stage) {
        warningLabel.setText("");
        this.appController = appController;
        users = appController.getUsers();
        this.stage = stage;
        stage.setTitle("Login");
        Scene scene = stage.getScene();
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                login(new ActionEvent());
            }
        });
    }

    /**
     * Changes the login window view between Clinician login and User login
     */
    @FXML
    void changeUserButtonClicked() {
        if (isUser) {
            warningLabel.setText("");
            idLabel.setText("Staff ID:");
            userIDTextField.setText("");
            passwordField.setVisible(true);
            passwordLabel.setVisible(true);
            isUser = false;
            changeLogin.setText("Login as a Public User");

        } else {
            warningLabel.setText("");
            idLabel.setText("NHI:");
            userIDTextField.setText("");
            passwordLabel.setVisible(false);
            passwordField.setVisible(false);
            isUser = true;
            changeLogin.setText("Login as a Clinician");
        }

    }

    /**
     * Logs in the person based on if they are a user or Clinician
     *
     * @param event An action event.
     */
    @FXML
    void login(ActionEvent event) {
        if (isUser) {
            warningLabel.setText("");
            String wantedDonor = userIDTextField.getText();
            User donor = null;

            if (wantedDonor.isEmpty()) {
                warningLabel.setText("Please enter an NHI.");
                return;
            } else {
                donor = appController.findUser(wantedDonor);
            }
            if (donor == null) {
                warningLabel.setText("Donor was not found. \nTo register a new donor please click sign up.");
                return;
            }

            FXMLLoader donorLoader = new FXMLLoader(getClass().getResource("/FXML/userView.fxml"));
            Parent root = null;
            try {
                root = donorLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(root));
            DonorController donorController = donorLoader.getController();
            AppController.getInstance().setDonorController(donorController);
            donorController.init(AppController.getInstance(), donor, stage, false);
        } else {
            warningLabel.setText("");
            String wantedClinician;
            if (userIDTextField.getText().isEmpty()) {
                warningLabel.setText("Please enter your staff id number");
                return;
            } else {
                wantedClinician = userIDTextField.getText();
            }
            String password = passwordField.getText();
            Clinician clinician = appController.getClinician(wantedClinician);
            if (clinician == null) {
                warningLabel.setText("The Clinician does not exist");
            } else if (!clinician.isPasswordCorrect(password)) {
                warningLabel.setText("Your password is incorrect please try again");
                return;
            } else {
                FXMLLoader clinicianLoader = new FXMLLoader(getClass().getResource("/FXML/clinicianView.fxml"));
                Parent root = null;
                try {
                    root = clinicianLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                stage.setScene(new Scene(root));
                ClinicianController clinicianController = clinicianLoader.getController();
                AppController.getInstance().setClinicianController(clinicianController);
                clinicianController.init(stage, appController, clinician);
            }
        }
    }


    /**
     * Creates either a new user or clinician based on the login window
     * Opens the sign up view based on the login view
     *
     * @param event An action event
     */
    @FXML
    void signUp(ActionEvent event) {


        if (isUser) {
            FXMLLoader donorLoader = new FXMLLoader(getClass().getResource("/FXML/createNewUser.fxml"));
            Parent root = null;
            try {
                root = donorLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage newStage  = new Stage();
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setScene(new Scene(root));
            newStage.setTitle("Create New User Profile");
            newStage.show();
            NewUserController donorController = donorLoader.getController();
            donorController.init(AppController.getInstance(), stage);

        } else {
            FXMLLoader clinicianLoader = new FXMLLoader(getClass().getResource("/FXML/updateClinician.fxml"));
            Parent root = null;

            try {
                root = clinicianLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage newStage  = new Stage();
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setScene(new Scene(root));
            newStage.show();
            UpdateClinicianController newClinician = clinicianLoader.getController();
            newClinician.init(null, appController, stage, true, newStage);
        }
    }


    /**
     * Displays a pop up window with instructions to help the user on the login page.
     */
    @FXML
    private void helpButton() {

        if (helpStage == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/loginHelp.fxml"));
                Parent root = fxmlLoader.load();
                helpStage = new Stage();
                helpStage.setTitle("Login Help");
                helpStage.setScene(new Scene(root));
                helpStage.setResizable(false);
                helpStage.setOnCloseRequest(event -> helpStage = null);
                helpStage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Opens the Command Line version of the application
     *
     * @param event
     */
    @FXML
    void openCLI(ActionEvent event) {
/*    stage.hide();
    CLI.main(new String[]{"gui"});
    stage.show();*/
        FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("/FXML/adminView.fxml"));
        Parent root = null;
        try {
            root = adminLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.setScene(new Scene(root));
        stage.setTitle("Administrator");
        AdministratorViewController administratorViewController = adminLoader.getController();
        administratorViewController.init(stage, appController, new Administrator());
    }
}

