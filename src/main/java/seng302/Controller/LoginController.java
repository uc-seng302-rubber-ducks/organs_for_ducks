package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seng302.Model.Clinician;
import seng302.Model.User;
import seng302.View.CLI;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Class for the login functionality of the application
 */
public class LoginController {

  @FXML
  private Button loginUButton;

  @FXML
  private Button signUpButton;

  @FXML
  private TextField userIDTextField;

  @FXML
  private Label userWarningLabel;

  @FXML
  private TextField staffIdTextField;

  @FXML
  private TextField staffPasswordField;

  @FXML
  private Button loginCButton;

  @FXML
  private Label clinicianWarningLabel;

  @FXML
  private TextField adminUsernameTextField;

  @FXML
  private TextField adminPasswordField;

  @FXML
  private Button loginAButton;

  @FXML
  private Label adminWarningLabel;

  //TODO delete this button once CLI is implemented in administrator
  @FXML
  private Button openCLIButton;

  private Stage helpStage = null;
  private AppController appController;
  private ArrayList<User> users;
  private Stage stage;

  /**
    * Initializes the Login controller.
    * @param appController The applications controller.
    * @param stage The applications stage.
    */
  public void init(AppController appController, Stage stage){
    openCLIButton.setVisible(false);
    userWarningLabel.setText("");
    clinicianWarningLabel.setText("");
    adminWarningLabel.setText("");
    this.appController = appController;
    users = appController.getUsers();
    this.stage = stage;
    stage.setTitle("Login");
    Scene scene = stage.getScene();
    scene.setOnKeyPressed(e -> {
//    if (e.getCode() == KeyCode.ENTER) {
//            login(new ActionEvent());
//        }
    });
  }


    /**
     * Logs in the user
     * @param event An action event.
     */@FXML
    void loginUser(ActionEvent event) {

            userWarningLabel.setText("");
            String wantedDonor = userIDTextField.getText();
            User donor = null;

            if (wantedDonor.isEmpty()) {
                userWarningLabel.setText("Please enter an NHI.");
                return;
            } else {
                donor = appController.findUser(wantedDonor);
            }
            if (donor == null) {
                userWarningLabel.setText("Donor was not found. \nTo register a new donor please click sign up.");
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
    }

    /**
     * Logs in the clinician
     * @param event An action event
     */
    @FXML
    void loginClinician(ActionEvent event) {
        //Checks if the Clinician login button is clicked and runs the clinician login code
            System.out.println("button clicked");
            clinicianWarningLabel.setText("");
            String wantedClinician;
            if (staffIdTextField.getText().isEmpty()) {
                clinicianWarningLabel.setText("Please enter your staff id number");
                return;
            } else {
                wantedClinician = staffIdTextField.getText();
            }
            String clinicianPassword = staffPasswordField.getText();
            Clinician clinician = appController.getClinician(wantedClinician);
            if (clinician == null) {
                clinicianWarningLabel.setText("The Clinician does not exist");
            } else if (!clinicianPassword.equals(clinician.getPassword())) {
                clinicianWarningLabel.setText("Your password is incorrect please try again");
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

    /**
     * Logs the administrator in
     * @param event An action event
     */
    @FXML
        void loginAdmin(ActionEvent event) {
        //Checks if the admin login button is clicked and runs the admin login code
//            adminWarningLabel.setText("");
//            String wantedAdmin;
//            if (adminUsernameTextField.getText().isEmpty()) {
//                adminWarningLabel.setText("Please enter your Administrator username.");
//                return;
//            } else {
//                wantedAdmin = adminUsernameTextField.getText();
//            }
//            String adminPassword = adminPasswordField.getText();
            //uncomment  the following when Administrator is set up - change code to fit the admin class as need be
//            Administrator administrator = appController.getAdministrator(wantedAdmin);
//            if (administrator == null) {
//                adminWarningLabel.setText("The administrator does not exist.");
//            } else if (!adminPassword.equals(administrator.getPassword())) {
//                adminWarningLabel.setText("Your password is incorrect. Please try again.");
//                return;
//            } else {
//                FXMLLoader administratorLoader = new FXMLLoader(getClass().getResource("/FXML/administratorView.fxml"));
//                Parent root = null;
//                try {
//                    root = administratorLoader.load();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                stage.setScene(new Scene(root));
//                AdministratorController administratorController = administratorLoader.getController();
//                AppController.getInstance().setAdministratorController(administratorController);
//                administratorController.init(stage, appController, administrator);
//            }
  }




    /**
     * Creates either a new user or clinician based on the login window
     * Opens the sign up view based on the login view
     * @param event An action event
     */
    @FXML
    void signUp(ActionEvent event) {

       FXMLLoader donorLoader = new FXMLLoader(getClass().getResource("/FXML/createNewUser.fxml"));
        Parent root = null;
        try {
            root = donorLoader.load();
        } catch (IOException e) {
            e.printStackTrace();}

        stage.setScene(new Scene(root));
        stage.setTitle("Create New User Profile");
        NewUserController donorController =  donorLoader.getController();
        donorController.init(AppController.getInstance(),  stage);

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
  }
}

