package seng302.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng302.model.User;
import seng302.service.Log;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class UserOverviewController {

    //the Home page attributes
    //<editor-fold desc="FMXL declarations">
    @FXML
    private Button backButton;

    @FXML
    private Label ageValue;

    @FXML
    private Label ageDeathValue;

    @FXML
    private Label NHIValue;

    @FXML
    private Label fNameValue;

    @FXML
    private Label lNameValue;

    @FXML
    private Label pNameValue;

    @FXML
    private Label mNameValue;

    @FXML
    private Label DOBValue;

    @FXML
    private Label DODValue;

    @FXML
    private Label genderIdentityValue;

    @FXML
    private Label birthGenderValue;

    @FXML
    private Label lastModifiedValue;

    @FXML
    private Label createdValue;

    @FXML
    private Label bloodTypeValue;

    @FXML
    private Label heightValue;

    @FXML
    private Label weightValue;

    @FXML
    private Label smokerValue;

    @FXML
    private Label alcoholValue;

    @FXML
    private Label bmiValue;

    @FXML
    private Button deleteUser;

    @FXML
    private Button logOutButton;
    //</editor-fold>

    private AppController application;
    private User currentUser;
    private Stage stage;
    private boolean Clinician;

    @FXML
    public void init(AppController controller, User user, Stage stage, boolean fromClinician) {
        this.stage = stage;
        this.application = controller;
        this.currentUser = user;
        if (fromClinician) {
            Clinician = true;
            logOutButton.setVisible(false);
        } else {
            Clinician = false;
//      deleteUser.setVisible(false);
            backButton.setVisible(false);
        }
    }

    /**
     * Shows the user profile for the logged in user
     *
     * @param user The current user.
     */
    public void showUser(User user) {
        NHIValue.setText(user.getNhi());
        fNameValue.setText(user.getFirstName());
        DOBValue.setText(user.getDateOfBirth().toString());
        if (user.getMiddleName() != null) {
            mNameValue.setText(user.getMiddleName());
        } else {
            mNameValue.setText("");
        }

        if (user.getPreferredFirstName() != null) {
            pNameValue.setText(user.getPreferredFirstName());
        } else {
            pNameValue.setText("");
        }

        if (user.getLastName() != null) {
            lNameValue.setText(user.getLastName());
        } else {
            lNameValue.setText("");
        }

        if (user.getGenderIdentity() != null) {
            genderIdentityValue.setText(user.getGenderIdentity());
        } else {
            genderIdentityValue.setText("");
        }
        if (user.getBirthGender() != null) {
            birthGenderValue.setText(user.getBirthGender());
            if (user.getGenderIdentity() == null || user.getGenderIdentity()
                    .equals(user.getBirthGender())) {
                genderIdentityValue.setText(user.getBirthGender());
            }
        } else {
            birthGenderValue.setText("");
        }

        ageValue.setText(user.getStringAge().replace("P", "").replace("Y", "") + " Years");
        if (user.getDateOfDeath() != null) {
            DODValue.setText(user.getDateOfDeath().toString());
            ageDeathValue.setText(Long.toString(
                    ChronoUnit.YEARS.between(user.getDateOfBirth(), user.getDateOfDeath())) + " Years");
        } else {
            DODValue.setText("");
            ageDeathValue.setText("");
        }
        if (user.getBloodType() != null) {
            bloodTypeValue.setText(user.getBloodType());
        } else {
            bloodTypeValue.setText("");
        }

        if (user.isSmoker()) {
            smokerValue.setText("Yes");
        } else {
            smokerValue.setText("No");
        }

        String weight;
        if (user.getWeight() > 0) {
            weight = java.lang.Double.toString(user.getWeight());
            weightValue.setText(weight);
        } else {
            weightValue.setText("");
        }

        String height;
        if (user.getHeight() > 0) {
            height = java.lang.Double.toString(user.getHeight());
            heightValue.setText(height);
        } else {
            heightValue.setText("");
        }

        if (user.getHeight() > 0 && user.getWeight() > 0) {
            //TODO fix BMI kg/m^
            DecimalFormat df = new DecimalFormat("#.00");
            double bmi = user.getWeight() / (user.getHeight() * user.getHeight());
            String formattedBmi = df.format(bmi);
            bmiValue.setText(formattedBmi);
        } else {
            bmiValue.setText("");
        }

        if (user.getLastModified() != null) {
            lastModifiedValue.setText(user.getLastModified().toString());
        }
        createdValue.setText(user.getTimeCreated().toString());
        alcoholValue.setText(user.getAlcoholConsumption());

    }

    /**
     * Opens the update user details window
     */
    @FXML
    private void updateDetails() {
        FXMLLoader updateLoader = new FXMLLoader(getClass().getResource("/FXML/updateUser.fxml"));
        Parent root;
        try {
            root = updateLoader.load();
            UpdateUserController updateUserController = updateLoader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            updateUserController.init(currentUser, application, stage);
            stage.show();
            Log.info("Successfully launched update user window for User NHI: " + currentUser.getNhi());

        } catch (IOException e) {
            Log.severe("Failed to load update user window for User NHI: " + currentUser.getNhi(), e);
            e.printStackTrace();
        }
    }

    /**
     * Closes current window.
     */
    @FXML
    private void closeWindow() {
        application.update(currentUser);
        stage.close();
        Log.info("Successfully closed update user window for User NHI: " + currentUser.getNhi());
    }

    /**
     * Creates a alert pop up to confirm that the user wants to delete the profile
     */
    @FXML
    public void delete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to delete this user?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            application.deleteUser(currentUser);
            Log.info("Successfully deleted user profile for User NHI: " + currentUser.getNhi());
            if (!Clinician) {
                logout();
            } else {
                stage.close();
            }
        }
    }

    /**
     * Fires when the logout button is clicked Ends the users session, and takes back to the login
     * window
     */
    @FXML
    private void logout() {
        currentUser.getUndoStack().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/loginView.fxml"));
        Parent root;
        try {
            root = loader.load();
            LoginController loginController = loader.getController();
            loginController.init(AppController.getInstance(), stage);
            stage.setScene(new Scene(root));
            stage.hide();
            stage.show();
            Log.info("successfully launched login window after logged out for User NHI: " + currentUser.getNhi());
        } catch (IOException e) {
            Log.severe("failed to launch login window after logged out for User NHI: " + currentUser.getNhi(), e);
            e.printStackTrace();
        }


    }

    /**
     * Disables logout buttons
     */
    public void disableLogout() {
        logOutButton.setVisible(false);
        backButton.setVisible(true);
    }
}
