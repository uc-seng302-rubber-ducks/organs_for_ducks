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
    private Label warningLabel;

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
    void login(ActionEvent event) {
        warningLabel.setText("");
        String wantedDonor = donorNameTextField.getText();
        Donor donor = appController.findDonor(wantedDonor);
        if (donor == null){
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
        DonorController donorController =  donorLoader.getController();
        donorController.init(AppController.getInstance(), donor);



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
        Stage primaryStage = new Stage();
        primaryStage.setScene(new Scene(root));
        DonorController donorController =  donorLoader.getController();
        donorController.init(AppController.getInstance(), new Donor());
        primaryStage.show();
    }

}
