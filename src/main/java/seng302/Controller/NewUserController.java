package seng302.Controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import seng302.Model.BloodTypes;
import seng302.Model.Donor;
import seng302.Model.EmergencyContact;
import seng302.Model.User;
import seng302.Service.AttributeValidation;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;


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
    private TextField preferredFNameTextField;

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
    private ComboBox birthGenderComboBox;

    @FXML
    private ComboBox genderIdComboBox;

    @FXML
    private ComboBox bloodComboBox;

    @FXML
    private CheckBox smokerCheckBox;

    @FXML
    private ComboBox alcoholComboBox;

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
        stage.setMinWidth(620);
        stage.setMaxWidth(620);
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
     * @throws IOException
     */
    private void createUser(String nhi, String fName, LocalDate dob, LocalDate dod) throws IOException {

        // TODO: Add in more validation and do not allow the user to be created if any of the fields are wrong.



        // User attributes
        String preferredFirstName = AttributeValidation.checkString(preferredFNameTextField.getText());
        String middleName = AttributeValidation.checkString(mNameInput.getText());
        String lastName = AttributeValidation.checkString(lNameInput.getText());

        String birthGender = AttributeValidation.validateGender(birthGenderComboBox.getValue().toString());
        String genderIdentity = AttributeValidation.validateGender(genderIdComboBox.getValue().toString());

        double height = AttributeValidation.validateHeight(heightInput.getText());
        double weight = AttributeValidation.validateWeight(weightInput.getText());

        String bloodType = AttributeValidation.validateBlood(bloodComboBox.getValue().toString()).toString(); // TODO: Change the data type of the value stored in Donor to be BloodTypes
        String alcoholConsumption = alcoholComboBox.getValue().toString();

        String currentAddress = addressInput.getText();
        String region = regionInput.getText();
        String homePhone = phoneInput.getText();
        String cellPhone = cellInput.getText();
        String email = emailInput.getText();


        boolean smoker;
        if (smokerCheckBox.isSelected()) {
            smoker = true;

        } else {
            smoker = false;
        }


        // Emergency Contact attributes
        String eName = ecNameInput.getText();
        String eCellPhone = ecCellInput.getText();


        EmergencyContact contact = new EmergencyContact(eName, eCellPhone);


        User dp = new User(nhi, dob, dod, birthGender, genderIdentity, height, weight, bloodType,
                alcoholConsumption, smoker, currentAddress, region, homePhone, cellPhone, email, contact,
                fName, fName, preferredFirstName, middleName, lastName);


        if (dp != null) {
//            saveUsers(con.getDonorList(), "src/main/resources/donors.json");

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
            donorController.init(AppController.getInstance(), dp, stage,false);
        }
    }


    /**
     * Sends the user to the user overview window.
     * Validates the required attributes and sends messages if they are not valid.
     * @throws IOException Throws an exception if the fxml cannot be located.
     */
    @FXML
    private void confirmCreation() throws IOException {
        hideErrorMessages();
        boolean valid = true;

        String nhi = AttributeValidation.validateNHI(nhiInput.getText());
        String fName = AttributeValidation.checkString(fNameInput.getText());

        LocalDate dob = dobInput.getValue(); // TODO: use LocalDate objects?
        LocalDate dod = dodInput.getValue();


        if (nhi == null) {
            invalidNHI.setVisible(true);
            valid = false;
        }

        if (fName == null) {
            invalidFirstName.setVisible(true);
            valid = false;
        }

        if (dob == null) {
            invalidDOB.setVisible(true);
            valid = false;
        }

        if (dod != null) {
            valid = AttributeValidation.validateDates(dob, dod);
            if (!valid) invalidDOD.setVisible(true);
        }

        //Donor donor = controller.findDonor(nhi); // checks if the nhi already exists within the system
        User user = controller.findUser(nhi, dob);
        //User user = null;

        if (valid && user == null){

            createUser(nhi, fName, dob, dod);


        } else if (user != null) {
            existingNHI.setVisible(true);
        }

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