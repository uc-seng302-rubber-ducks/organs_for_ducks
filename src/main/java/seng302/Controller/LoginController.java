package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import seng302.Model.Clinician;
import seng302.Model.Donor;

import java.io.IOException;
import java.util.ArrayList;
import seng302.Model.User;
import seng302.View.CLI;

public class LoginController {

  @FXML
  private Button loginButton;

  @FXML
  private Button signUpButton;

  @FXML
  private TextField donorNameTextField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private ComboBox<String> accountTypeComboBox;

  @FXML
  private Label warningLabel;

  @FXML
  private Label passwordLabel;

  private AppController appController;
  private ArrayList<User> users;
  private Stage stage;

  public void init(AppController appController, Stage stage) {
    warningLabel.setText("");
    this.appController = appController;
    users = appController.getUsers();
    this.stage = stage;
    accountTypeComboBox.getItems().add("Donor");
    accountTypeComboBox.getItems().add("Clinician");
    accountTypeComboBox.getSelectionModel().select("Donor");

  }

  @FXML
  void login(ActionEvent event) {
    if (accountTypeComboBox.getValue().equals("Donor")) {
      warningLabel.setText("");
      String wantedDonor = donorNameTextField.getText();
      User user = appController.findUser(wantedDonor);
      if (user == null) {
        warningLabel.setText("Donor was not found. To register a new user please click sign up");
        return;
      }
      FXMLLoader donorLoader = new FXMLLoader(getClass().getResource("/FXML/donorView.fxml"));
      Parent root = null;
      try {
        root = donorLoader.load();
      } catch (IOException e) {
        e.printStackTrace();
      }
      stage.setScene(new Scene(root));
      DonorController donorController = donorLoader.getController();
      AppController.getInstance().setDonorController(donorController);
      donorController.init(AppController.getInstance(), user, stage, false);
    } else if (accountTypeComboBox.getValue().equals("Clinician")) {
      warningLabel.setText("");
      String wantedClinician = "";
      wantedClinician = donorNameTextField.getText();
      String password = passwordField.getText();
      Clinician clinician = appController.getClinician(wantedClinician);
      if (!password.equals(clinician.getPassword())) {
        warningLabel.setText(
            "Either the Clinician does not exist\n or the password is incorrect please try again");
        return;
      }
      FXMLLoader clinicianLoader = new FXMLLoader(
          getClass().getResource("/FXML/clinicianView.fxml"));
      Parent root = null;
      try {
        root = clinicianLoader.load();
      } catch (IOException e) {
        e.printStackTrace();
      }
      stage.setScene(new Scene(root));
      ClinicianController clinicianController = clinicianLoader.getController();
      clinicianController.init(stage, appController, clinician);

    }


  }

  @FXML
  void signUp(ActionEvent event) {

    FXMLLoader donorLoader = new FXMLLoader(getClass().getResource("/FXML/donorView.fxml"));
    Parent root = null;
    try {
      root = donorLoader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
    stage.setScene(new Scene(root));
    DonorController donorController = donorLoader.getController();
    donorController.init(AppController.getInstance(), new User(), stage, false);

  }

  @FXML
  void openCLI(ActionEvent event) {
    CLI.main(new String[]{});
  }
}

