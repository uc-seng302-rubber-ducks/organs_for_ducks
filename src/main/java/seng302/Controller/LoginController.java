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
    private ArrayList<Donor> donors;
    private Stage stage;

    public void init(AppController appController, Stage stage){
        warningLabel.setText("");
        this.appController = appController;
        donors = appController.getDonors();
        this.stage = stage;

    }

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

    @FXML
    void login(ActionEvent event) {
        if(isUser) {
            warningLabel.setText("");
            String wantedDonor = userIDTextField.getText();
            Donor donor = appController.findDonor(wantedDonor);

            if (donor == null) {
                warningLabel.setText("Donor was not found.\nTo register a new donor please click sign up.");
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
            donorController.init(AppController.getInstance(), donor, stage,false);

        } else {
            warningLabel.setText("");
            int wantedClinician = -1;

            try {
                wantedClinician = Integer.parseInt(userIDTextField.getText());
            } catch (NumberFormatException e){
                warningLabel.setText("Please enter your staff id number");
                return;
            }

            String password = passwordField.getText();
            Clinician clinician = appController.getClinician(wantedClinician);
            System.out.println(clinician);

            if (clinician == null){
                warningLabel.setText("The Clinician does not exist");
            } else if (!password.equals(clinician.getPassword())){
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
            clinicianController.init(stage,appController,clinician);
            }
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
        DonorController donorController =  donorLoader.getController();
        donorController.init(AppController.getInstance(), new Donor(), stage, false);

    }


    /**
     * Displays a pop up window with instructions to help the user on the login page.
     */
    @FXML
    public void helpButton() {

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


}

