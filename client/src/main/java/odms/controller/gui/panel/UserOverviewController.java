package odms.controller.gui.panel;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model._enum.Directory;
import odms.controller.AppController;
import odms.controller.gui.FileSelectorController;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

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

    @FXML
    private ImageView profilePicture;
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
        } else {
            Clinician = false;
        }
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
     * uploads an image using file picker. includes validation.
     * @throws IOException if there are issues with handling files.
     */
    @FXML
    private void uploadImage() throws IOException {
        boolean isValid = true;
        String filename;
        String filePath;
        List<String> extensions = new ArrayList<>();
        extensions.add("*.jpg");
        extensions.add("*.png");
        filename = FileSelectorController.getFileSelector(stage, extensions);

        if(filename != null) {
            File inFile = new File(filename);

            if (inFile.length() > 2000000) { //if more than 2MB
                System.out.println("image exceeded 2MB!"); //TODO: Replace with javafx label
                isValid = false;
            }

            if (isValid) {
                filePath = setUpImageFile(inFile);
                currentUser.setProfilePhotoFilePath(filePath);
                Image image = new Image("file:" + filePath, 200, 200, false, true);
                profilePicture.setImage(image);
            }
        }
    }

    /**
     * sets up a temp folder and image folder (within temp folder).
     * then make a copy of the desired image and store it in the image folder.
     * @param inFile desired image file
     * @return filepath to the image file
     * @throws IOException if there are issues with handling files.
     */
    public String setUpImageFile(File inFile) throws IOException {
        BufferedImage bImage;
        if(currentUser.getProfilePhotoFilePath() != null) { //Prevent duplicated image files.
            File oldFile = new File(currentUser.getProfilePhotoFilePath());
            oldFile.delete();
        }

        String fileType = inFile.getName();
        fileType = fileType.substring(fileType.lastIndexOf(".") + 1);

        Files.createDirectories(Paths.get(Directory.TEMP.directory()+Directory.IMAGES));
        File outFile = new File(Directory.TEMP.directory()+Directory.IMAGES +"/"+currentUser.getNhi()+"."+fileType);

        bImage = ImageIO.read(inFile);
        ImageIO.write(bImage, fileType, outFile);

        return outFile.getPath();
    }

    //TODO move this to update user 18/7/2018
//    /**
//     * Creates a alert pop up to confirm that the user wants to delete the profile
//     */
//    @FXML
//    public void delete() {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setContentText("Are you sure you want to delete this user?");
//        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
//        Optional<ButtonType> result = alert.showAndWait();
//
//        if (result.get() == ButtonType.OK) {
//            currentUser.setDeleted(true);
//            Log.info("Successfully deleted user profile for User NHI: " + currentUser.getNhi());
//            if (!Clinician) {
//                application.deleteUser(currentUser);
//                logout();
//            } else {
//                stage.close();
//            }
//        }
//    }






    /**
     * Disables logout buttons
     */
    public void disableLogout() {
        logOutButton.setVisible(false);
        backButton.setVisible(true);
    }
}
