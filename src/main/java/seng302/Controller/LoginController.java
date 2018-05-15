package seng302.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng302.Model.Administrator;
import seng302.Model.Clinician;
import seng302.Model.User;
import seng302.Service.Log;
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
  private Button loginButton;

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
  private Label idLabel;

  @FXML
  private TextField adminPasswordField;

  @FXML
  private Label passwordLabel;

  @FXML
  private Button loginAButton;

  @FXML
  private Label adminWarningLabel;

  @FXML
  private TabPane loginTabPane;

  //TODO delete this button once CLI is implemented in administrator
  @FXML
  private Button openCLIButton;

  private Stage helpStage = null;
  private boolean isUser = true;
  private AppController appController;
  private ArrayList<User> users;
  private Stage stage;

  /**
   * Initializes the Login controller.
   *
   * @param appController The applications controller.
   * @param stage The applications stage.
   */
  public void init(AppController appController, Stage stage) {
    Log.info("starting loginController");
    userWarningLabel.setText("");
    clinicianWarningLabel.setText("");
    adminWarningLabel.setText("");
    this.appController = appController;
    users = appController.getUsers();
    this.stage = stage;
    stage.setTitle("Login");
    Scene scene = stage.getScene();

    scene.setOnKeyPressed(e -> {
      int currentPane = loginTabPane.getSelectionModel().getSelectedIndex();
      if (e.getCode() == KeyCode.ENTER) {
        if (currentPane == 0) {
          loginUser(new ActionEvent());
        } else if (currentPane == 1) {
          loginClinician(new ActionEvent());
        } else if (currentPane == 2) {
          loginAdmin(new ActionEvent());
        }
      }
    });
  }


  /**
   * Logs in the user
   *
   * @param event An action event.
   */
  @FXML
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
              Log.info("Logging in as a user");
            } catch (IOException e) {
              Log.severe("failed to load user window", e);
                e.printStackTrace();
            }
            stage.setScene(new Scene(root));
            DonorController donorController = donorLoader.getController();
            AppController.getInstance().setDonorController(donorController);
            donorController.init(AppController.getInstance(), donor, stage, false);
  }

  /**
   * Logs in the clinician
   *
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
    } else if (!clinician.isPasswordCorrect(clinicianPassword)) {
      clinicianWarningLabel.setText("Your password is incorrect please try again");
      return;
    } else {
      FXMLLoader clinicianLoader = new FXMLLoader(
          getClass().getResource("/FXML/clinicianView.fxml"));
      Parent root = null;
      try {
        root = clinicianLoader.load();
      } catch (IOException e) {
                    Log.severe("failed to load clinician window", e);
        e.printStackTrace();
      }

      stage.setScene(new Scene(root));
      ClinicianController clinicianController = clinicianLoader.getController();
      AppController.getInstance().setClinicianController(clinicianController);
                Log.info("Logging in as a clinician");
      clinicianController.init(stage, appController, clinician);
    }
  }

  /**
   * Logs the administrator in
   *
   * @param event An action event
   */
  @FXML
  void loginAdmin(ActionEvent event) {
    //Checks if the admin login button is clicked and runs the admin login code
    adminWarningLabel.setText("");
    String wantedAdmin;
    if (adminUsernameTextField.getText().isEmpty()) {
      adminWarningLabel.setText("Please enter your Administrator username.");
      return;
    } else {
      wantedAdmin = adminUsernameTextField.getText();
    }
    String adminPassword = adminPasswordField.getText();
    Administrator administrator = appController.getAdministrator(wantedAdmin);
    if (administrator == null) {
      adminWarningLabel.setText("The administrator does not exist.");
    } else if (!administrator.isPasswordCorrect(adminPassword)) {
      adminWarningLabel.setText("Your password is incorrect. Please try again.");
      return;
    } else {
      FXMLLoader administratorLoader = new FXMLLoader(
          getClass().getResource("/FXML/adminView.fxml"));
      Parent root = null;
      try {
        root = administratorLoader.load();
      } catch (IOException e) {
                    Log.severe("failed to load administrator window", e);
        e.printStackTrace();
      }

      stage.setScene(new Scene(root));
      stage.setTitle("Administrator");
      AdministratorViewController administratorController = administratorLoader.getController();
      AppController.getInstance().setAdministratorViewController(administratorController);
                Log.info("Logging in as an administrator");
      administratorController.init(administrator, appController, stage);
    }
  }


  /**
   * Creates either a new user or clinician based on the login window Opens the sign up view based
   * on the login view
   *
   * @param event An action event
   */
  @FXML
  void signUp(ActionEvent event) {

    FXMLLoader donorLoader = new FXMLLoader(getClass().getResource("/FXML/createNewUser.fxml"));
        Parent root = null;
        try {
            root = donorLoader.load();
        } catch (IOException e) {
            Log.severe("failed to load new user window", e);
          e.printStackTrace();
        }
        Stage newStage = new Stage();
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setScene(new Scene(root));
        newStage.setTitle("Create New User Profile");
        newStage.show();
        NewUserController donorController = donorLoader.getController();
        Log.info("Opening new user window");
        donorController.init(AppController.getInstance(), stage, newStage);

  }

/**
 * Displays a pop up window with instructions to help the user on the login page.
 */
@FXML
private void helpButton(){

    if(helpStage==null){
    try{
    FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("/FXML/loginHelp.fxml"));
    Parent root=fxmlLoader.load();
    helpStage=new Stage();
    helpStage.setTitle("Login Help");
    helpStage.setScene(new Scene(root));
    helpStage.setResizable(false);
    helpStage.setOnCloseRequest(event->helpStage=null);
    helpStage.show();

    }catch(Exception e){
    Log.severe("could not load help window",e);
    e.printStackTrace();
    }
    }
    }



    /**
     * Opens the Command Line version of the application

     */
    @FXML
    void openCLI(ActionEvent event) {
    stage.hide();

    CLI.main(new String[]{"gui"});
    stage.show();
        FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("/FXML/adminView.fxml"));
        Parent root = null;
        try {
            root = adminLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

      stage.setScene(new Scene(root));
      stage.setTitle("Administrator");
      AdministratorViewController administratorViewController=adminLoader.getController();
      administratorViewController.init(new Administrator(),appController,stage);
      }
      }

