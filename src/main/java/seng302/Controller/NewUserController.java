package seng302.Controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


//TODO: ADD INITIALIZE METHOD AND INSTANTIATE THINGS THROUGH THERE.

/**
 * Controller class for creating a new donor profile.
 * @author acb116, are66, eli26, jha236
 */
public class NewUserController {

    @FXML
    private Label errorLabel;

    @FXML
    private Label existingNHI;

    @FXML
    private Label invalidNHI;

    @FXML
    private Label invalidFirstName;

    @FXML
    private Label invalidDOB;

    @FXML
    private Label invalidDOD;

    @FXML
    private TextField nhiInput;

    @FXML
    private TextField fNameInput;

    @FXML
    private TextField mNameInput;

    @FXML
    private TextField lNameInput;

    @FXML
    private TextField heightInput;

    @FXML
    private TextField weightInput;

    @FXML
    private TextField phoneInput;

    @FXML
    private TextField cellInput;

    @FXML
    private TextField addressInput;

    @FXML
    private TextField regionInput;

    @FXML
    private TextField emailInput;

    @FXML
    private TextField ecNameInput;

    @FXML
    private TextField ecPhoneInput;

    @FXML
    private TextField ecCellInput;

    @FXML
    private TextField ecAddressInput;

    @FXML
    private TextField ecRegionInput;

    @FXML
    private TextField ecEmailInput;

    @FXML
    private TextField ecRelationshipInput;

    @FXML
    private ComboBox genderComboBox;

    @FXML
    private ComboBox bloodComboBox;

    @FXML
    private ComboBox smokerComboBox;

    @FXML
    private ComboBox alcoholComboBox;

    @FXML
    private ComboBox bloodPressureCB;

    @FXML
    private DatePicker dobInput;

    @FXML
    private DatePicker dodInput;

    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;

    AppController controller;
    Stage stage;


    public void init(AppController controller, Stage stage) {
        this.controller = controller;
        this.stage = stage;
    }


    public NewUserController() throws IOException {
    }


    /**
     * Returns the user to the login window.
     * @throws IOException Throws an exception if the fxml cannot be located.
     */
    @FXML
    private void cancelCreation() throws IOException {
//        Stage primaryStage = (Stage) cancelButton.getScene().getWindow();
//        Parent root = FXMLLoader.load(getClass().getResource("/FXML/login.fxml"));
//        primaryStage.setScene(new Scene(root));

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
     * Creates the new user with at least the required attributes.
     * @param nhi The national health index.
     * @param fName First Name.
     * @param dob Date of birth.
     * @param dod Date of death.
     * @param controller Allows variables to be passed between controllers.
     * @throws IOException
     */
//    private boolean createUser(String nhi, String fName, LocalDate dob, LocalDate dod, UserOverviewController controller) throws IOException {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        String birth = formatter.format(dobInput.getValue());
//
//
//        String profileValues = "-p -id " + nhi + " -firstName  " + fName + " -dateOfBirth " + birth;
//
//        if (dod != null) {
//            String death = formatter.format(dobInput.getValue());
//            profileValues += " -dateOfDeath " + death;
//        }
//
//        if (smokerComboBox.getValue() != null) {
//            if (AttributeValidation.validateSmoker(smokerComboBox.getValue().toString())) {
//                profileValues += " -smoker";
//            }
//        }
//
//        profileValues = AttributeValidation.addMultipleValues( profileValues, " -lastName ", lNameInput);
//        profileValues = AttributeValidation.addMultipleValues( profileValues, " -middleNames ", mNameInput);
//        profileValues = AttributeValidation.addMultipleValues( profileValues, " -region ", regionInput);
//        profileValues = AttributeValidation.addMultipleValues( profileValues, " -address ", addressInput);
//
//        profileValues = AttributeValidation.addValues( profileValues, " -weight ", weightInput);
//        profileValues = AttributeValidation.addValues( profileValues, " -height ", heightInput);
//        profileValues = AttributeValidation.addValues( profileValues, " -email ", emailInput);
//
//        profileValues = AttributeValidation.addPhoneNumber( profileValues, " -homeNum ", phoneInput);
//        profileValues = AttributeValidation.addPhoneNumber( profileValues, " -cellNum ", cellInput);
//
//        profileValues = AttributeValidation.addComboSelection(profileValues, " -bloodType ", bloodComboBox);
////        profileValues = AttributeValidation.addComboSelection(profileValues, " -gender ", genderComboBox);
//        profileValues = AttributeValidation.addComboSelection(profileValues, " -alcoholCons ", alcoholComboBox);
//        profileValues = AttributeValidation.addComboSelection(profileValues, " -bloodPressure ", bloodPressureCB);
//
//
//        if (genderComboBox.getValue() != null) {
//            profileValues += " -gender " + genderComboBox.getValue().toString().charAt(0);
//        }
//
//        CreateCommand create = new CreateCommand(profileValues, con);
//        create.execute();
//
//
//        // Form the update string for the emergency contact
//        String emergencyDetails = "-id " + nhi;
//
//        emergencyDetails = AttributeValidation.addMultipleValues(emergencyDetails, " -eConName ", ecNameInput);
//        emergencyDetails = AttributeValidation.addMultipleValues(emergencyDetails, " -eConAddress ", ecAddressInput);
//        emergencyDetails = AttributeValidation.addMultipleValues(emergencyDetails, " -eConRegion ", ecRegionInput);
//        emergencyDetails = AttributeValidation.addMultipleValues(emergencyDetails, " -eConRel ", ecRelationshipInput);
//
//        emergencyDetails = AttributeValidation.addValues(emergencyDetails, " -eConEmail ", ecEmailInput);
//
//        emergencyDetails = AttributeValidation.addPhoneNumber(emergencyDetails, " -eConHomeNum ", ecPhoneInput); // e c phone home
//        emergencyDetails = AttributeValidation.addPhoneNumber(emergencyDetails, " -eConCellNum ", ecCellInput);
//
//        UpdateEmergencyContact updateEC = new UpdateEmergencyContact(emergencyDetails, con, false, true);
//        updateEC.execute();
//
//        DonorProfile dp = con.search(nhi);
//        System.out.println(dp.getCellNum());
//
//        if (dp != null) {
//            saveUsers(con.getDonorList(), "src/main/resources/donors.json");
//            controller.initProfile(dp); // passes the donor to the userOverviewController class
//            return true;
//        }
//
//        return false;
//    }


    /**
     * Sends the user to the user overview window.
     * Validates the required attributes and sends messages if they are not valid.
     * @throws IOException Throws an exception if the fxml cannot be located.
     */
    @FXML
    private void confirmCreation() throws IOException {
//        hideErrorMessages();
//        boolean valid = true;
//
//        String nhi = AttributeValidation.validateNHI(nhiInput.getText());
//        String fName = AttributeValidation.checkString(fNameInput.getText());
//
//        LocalDate dob = dobInput.getValue();
//        LocalDate dod = dodInput.getValue();
//
//        if (nhi == null) {
//            invalidNHI.setVisible(true);
//            valid = false;
//        }
//
//        if (fName == null) {
//            invalidFirstName.setVisible(true);
//            valid = false;
//        }
//
//        if (dob == null) {
//            invalidDOB.setVisible(true);
//            valid = false;
//        }
//
//        if (dod != null) {
//            valid = AttributeValidation.validateDates(dob, dod);
//            if (!valid) invalidDOD.setVisible(true);
//        }
//
//        DonorProfile donor = con.search(nhi); // checks if the nhi already exists within the system
//
//        if (valid && donor == null){
//
//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(getClass().getResource("/userOverview.fxml"));
//            Parent root = loader.load();
//
//            UserOverviewController controller = loader.getController();
//            boolean created = createUser(nhi, fName, dob, dod, controller);
//
//
//            if (created) {
//                Stage primaryStage = (Stage) confirmButton.getScene().getWindow();
//                primaryStage.setScene(new Scene(root));
//            } else {
//                errorLabel.setVisible(true);
//            }
//
//        }
//
//        if (donor != null) existingNHI.setVisible(true);




//        FXMLLoader clinicianLoader = new FXMLLoader(getClass().getResource("/FXML/clinicianView.fxml"));
//        Parent root = null;
//        try {
//            root = clinicianLoader.load();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        stage.setScene(new Scene(root));
//        ClinicianController clinicianController = clinicianLoader.getController();
//        clinicianController.init(stage,appController,clinician);
    }


    /**
     * Makes all the error messages no longer visible.
     */
    private void hideErrorMessages() {
        errorLabel.setVisible(false);
        invalidNHI.setVisible(false);
        invalidDOB.setVisible(false);
        invalidDOD.setVisible(false);
        invalidFirstName.setVisible(false);
        existingNHI.setVisible(false);
    }


}