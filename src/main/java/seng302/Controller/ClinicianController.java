package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.joda.time.DateTime;
import seng302.Controller.AppController;
import seng302.Model.Clinician;

import java.io.IOException;

public class ClinicianController {

    @FXML
    private TextField regionTextField;

    @FXML
    private TextField addressTextField;

    @FXML
    private PasswordField conformPasswordField;

    @FXML
    private TextField nameTextField;

    @FXML
    private Button logoutButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button confirmButton;

    @FXML
    private Button undoButton;

    @FXML
    private Label staffIdLabel;

    @FXML
    private Label warningLabel;

    private Stage stage;
    private AppController appController;
    private Clinician clinician;


    public void init(Stage stage, AppController appController, Clinician clinician){
        warningLabel.setText("");
        this.stage = stage;
        this.appController = appController;
        this.clinician = clinician;
        nameTextField.setText(clinician.getName());
        staffIdLabel.setText(String.valueOf(clinician.getStaffId()));
        addressTextField.setText(clinician.getWorkAddress());
        regionTextField.setText(clinician.getRegion());

    }

    @FXML
    void undo(ActionEvent event) {

    }

    @FXML
    void logout(ActionEvent event) {
        confirm(new ActionEvent());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/loginView.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LoginController loginController = loader.getController();
        loginController.init(AppController.getInstance(), stage);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void confirm(ActionEvent event) {
        clinician.setName(nameTextField.getText());
        clinician.setWorkAddress(addressTextField.getText());
        clinician.setRegion(regionTextField.getText());
        if(passwordField.getText().equals(conformPasswordField.getText()) && !passwordField.getText().equals("")){
            clinician.setPassword(passwordField.getText());
        } else {
            warningLabel.setText("Passwords did not match.\n Password was not updated.");
        }
        clinician.setDateLastModified(DateTime.now());
        appController.updateClinicians(clinician);

    }

}
