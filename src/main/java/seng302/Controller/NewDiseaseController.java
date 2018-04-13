package seng302.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

/**
 * Controller class for creating new disease.
 * @author acb116
 */
public class NewDiseaseController {
    @FXML
    public TextField diseaseNameInput;

    @FXML
    public DatePicker  diagnosisDateInput;

    @FXML
    public Checkbox chronicCheckBox;

    @FXML
    public Checkbox curedCheckBox;

    AppController controller;
    Stage stage;

    /**
     * Initializes the NewDiseaseController
     * @param controller The applications controller.
     * @param stage The applications stage.
     */
    public void init(AppController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
        //stage.setMinWidth(620);
        //stage.setMaxWidth(620);
    }

    /**
     * Returns the user to the DonorView.
     * @throws IOException Throws an exception if the fxml cannot be located.
     */
    @FXML
    private void cancelCreation() throws IOException {
//        Stage primaryStage = (Stage) cancelButton.getScene().getWindow();
//        Parent root = FXMLLoader.load(getClass().getResource("/FXML/login.fxml"));
//        primaryStage.setScene(new Scene(root));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/donorView.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        stage.setScene(new Scene(root));
//        LoginController loginController = loader.getController();
//        loginController.init(AppController.getInstance(), stage);
//        stage.show();
    }
}
