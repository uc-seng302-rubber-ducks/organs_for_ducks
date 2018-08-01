package odms.controller.gui.panel;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import odms.controller.AppController;
import odms.controller.gui.popup.UpdateDeathDetailsController;
import odms.commons.model.User;
import odms.commons.utils.Log;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;

import static odms.commons.utils.PhotoHelper.displayImage;

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
    private Label cityOfDeathValue;

    @FXML
    private Label regionOfDeathValue;

    @FXML
    private Label countryOfDeathValue;

    @FXML
    private Button deleteUser;

    @FXML
    private Button logOutButton;

    @FXML
    private ImageView profilePicture;
    //</editor-fold>

    private AppController application;
    private User currentUser;
    private Stage stage;
    private boolean clinician;

    @FXML
    public void init(AppController controller, User user, Stage stage, boolean fromClinician) {
        this.stage = stage;
        this.application = controller;
        this.currentUser = user;
        clinician = fromClinician;
        showUser(user);
    }

    /**
     * Shows the user profile for the logged in user
     *
     * @param user The current user.
     */
    public void showUser(User user) {
        currentUser = user;
        NHIValue.setText(user.getNhi());
        fNameValue.setText(user.getFirstName());
        DOBValue.setText(user.getDateOfBirth().toString());

        displayImage(profilePicture, user.getProfilePhotoFilePath());


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

        if (user.getDeathCity() != null) {
            cityOfDeathValue.setText(user.getDeathCity());
        } else {
            cityOfDeathValue.setText("");
        }

        if (user.getDeathRegion() != null) {
            regionOfDeathValue.setText(user.getDeathRegion());
        } else {
            regionOfDeathValue.setText("");
        }

        if (user.getDeathCountry() != null) {
            countryOfDeathValue.setText(user.getDeathCountry());
        } else {
            countryOfDeathValue.setText("");
        }


        if (user.getLastModified() != null) {
            lastModifiedValue.setText(user.getLastModified().toString());
        }
        createdValue.setText(user.getTimeCreated().toString());
        alcoholValue.setText(user.getAlcoholConsumption());

    }


    @FXML
    private void updateDeathDetails() {
        openUpdateDeathDetailsPopup();
    }

    private void openUpdateDeathDetailsPopup() {
        FXMLLoader updateDeathDetailsLoader = new FXMLLoader(getClass().getResource("/FXML/updateDeathDetails.fxml"));
        Parent root;
        try {
            root = updateDeathDetailsLoader.load();
            UpdateDeathDetailsController updateDeathDetailsController = updateDeathDetailsLoader.getController();
            Stage updateStage = new Stage();
            updateStage.initModality(Modality.APPLICATION_MODAL);
            updateStage.setScene(new Scene(root));
            updateDeathDetailsController.init(application, updateStage, currentUser);
            updateStage.show();
            Log.info("Successfully launched update user window for User NHI: " + currentUser.getNhi());

        } catch (IOException e) {
            Log.severe("Failed to load edit death details window for User NHI: " + currentUser.getNhi(), e);
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
