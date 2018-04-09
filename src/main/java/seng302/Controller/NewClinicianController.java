package seng302.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seng302.Model.Clinician;


import java.io.IOException;


public class NewClinicianController {

    @FXML private TextField staffIDInput;
    @FXML private TextField firstNameInput;
    @FXML private TextField regionInput;
    @FXML private TextField passwordInput;
    @FXML private TextField middleNameInput;
    @FXML private TextField lastNameInput;
    @FXML private TextField workAddressInput;

    @FXML private Button createCButton;

    @FXML private Button cancelCButton;

    private Stage stage;
    private AppController appController;


    public void init(Stage stage, AppController appController) {
        this.stage = stage;
        this.appController = appController;
    }



    /**
     * Returns the clinician to the login window.
     * @throws IOException Throws an exception if the fxml cannot be located.
     */
    @FXML
    public void cancelCliCreation() throws IOException {
//        Stage primaryStage = (Stage) cancelCButton.getScene().getWindow();
//        Parent root = FXMLLoader.load(getClass().getResource("/FXML/loginView.fxml"));
//
//        primaryStage.setScene(new Scene(root));
        //primaryStage.setMinHeight(400);
        //primaryStage.setMinWidth(700);


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


    /**
     * Sends the clinician to the user overview window.
     * @throws IOException Throws an exception if the fxml cannot be located.
     */
    @FXML
    public void confirmCliCreation() throws IOException {
        Boolean requiredFieldIsPresent = true;


//        Stage primaryStage = (Stage) createCButton.getScene().getWindow();
//        Parent root = FXMLLoader.load(getClass().getResource("/clinicianWindow.fxml"));
//        primaryStage.setScene(new Scene(root));
//        primaryStage.setMinHeight(630);
//        primaryStage.setMinWidth(1000);


        Clinician clinician;
        String staffID = "";

        if (!staffIDInput.getText().isEmpty()) {
            staffID = staffIDInput.getText();
        } else {
            requiredFieldIsPresent = false;
        }



        String firstName = firstNameInput.getText();
        String password = passwordInput.getText();
        String region = regionInput.getText();
        String lastName = lastNameInput.getText();
        String workAddress = workAddressInput.getText();
        String middleName = middleNameInput.getText();

//        if (staffID.isEmpty())
//        {
//            //labelresponse.setText("The field cannot be left blank. You must enter in a staff ID");
//            requiredFieldIsPresent = false;
//        }

        if (firstName.isEmpty())
        {
            //labelresponse.setText("The field cannot be left blank. You must enter in a first name");
            requiredFieldIsPresent = false;
        }

        if (password.isEmpty())
        {
            //labelresponse.setText("The field cannot be left blank. You must enter in a last name");
            requiredFieldIsPresent = false;
        }

        if (region.isEmpty())
        {
            //labelresponse.setText("The field cannot be left blank. You must enter in a region");
            requiredFieldIsPresent = false;
        }

        if (requiredFieldIsPresent) {
            clinician = new Clinician(firstName, staffID, "", region, password);

            if (!lastName.isEmpty()) {
                clinician.setLastName(lastName);
            }

            if (!workAddress.isEmpty()) {
                clinician.setWorkAddress(workAddress);
            }

            if (!middleName.isEmpty()) {
                clinician.setMiddleName(middleName);
            }

            //console.getClinicianList().put(staffID, clinician);
        }
    }

}
