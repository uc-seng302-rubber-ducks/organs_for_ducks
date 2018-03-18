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
    private ArrayList<Donor> donors;
    private Stage stage;

    public void init(AppController appController, Stage stage){
        warningLabel.setText("");
        this.appController = appController;
        donors = appController.getDonors();
        this.stage = stage;
        accountTypeComboBox.getItems().add("Donor");
        accountTypeComboBox.getItems().add("Clinician");
        accountTypeComboBox.getSelectionModel().select("Donor");

    }

    @FXML
    void login(ActionEvent event) {
        if(accountTypeComboBox.getValue().equals("Donor")) {
            warningLabel.setText("");
            String wantedDonor = donorNameTextField.getText();
            Donor donor = appController.findDonor(wantedDonor);
            if (donor == null) {
                warningLabel.setText("Donor was not found. To register a new donor please click sign up");
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
            donorController.init(AppController.getInstance(), donor, stage);
        } else if (accountTypeComboBox.getValue().equals("Clinician")) {
            warningLabel.setText("");
            int wantedClinician = -1;
            try {
                wantedClinician = Integer.parseInt(donorNameTextField.getText());
            } catch (NumberFormatException e){
                warningLabel.setText("Please enter your staff id number");
                return;
            }
            String password = passwordField.getText();
            Clinician clinician = appController.getClinician(wantedClinician);
            if (!password.equals(clinician.getPassword())){
                warningLabel.setText("Either the Clinician does not exist\n or the password is incorrect please try again");
                return;
            }
            FXMLLoader clincianLoader = new FXMLLoader(getClass().getResource("/FXML/clinicianView.fxml"));
            Parent root = null;
            try {
                root = clincianLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(root));
            ClinicianController clinicianController = clincianLoader.getController();
            clinicianController.init(stage,appController,clinician);

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
        donorController.init(AppController.getInstance(), new Donor(), stage);

    }



}

