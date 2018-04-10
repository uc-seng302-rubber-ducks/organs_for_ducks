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
import java.util.ArrayList;
import java.util.Date;

import static seng302.Model.JsonHandler.saveUsers;


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
        //stage.setMinWidth(620);
        //stage.setMaxWidth(620);
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
        boolean valid = true;

        // User attributes
        // check string returns null if the textfield is empty
        String preferredFirstName = AttributeValidation.checkString(preferredFNameTextField.getText());
        String middleName = AttributeValidation.checkString(mNameInput.getText());
        String lastName = AttributeValidation.checkString(lNameInput.getText());

        String birthGender = AttributeValidation.validateGender(birthGenderComboBox);
        String genderIdentity = AttributeValidation.validateGender(genderIdComboBox);
        String bloodType = AttributeValidation.validateBlood(bloodComboBox);

        boolean smoker = smokerCheckBox.isSelected();

        String alcoholConsumption = null;
        if (alcoholComboBox.getValue() != null) {
            alcoholConsumption = alcoholComboBox.getValue().toString();
        }

        // validate doubles return -1 if the value is 0 or below, and 0 if the textfield is empty
        double height = AttributeValidation.validateDouble(heightInput.getText());
        double weight = AttributeValidation.validateDouble(weightInput.getText());
        if (height == -1 || weight == -1) {
            errorLabel.setVisible(true);
            valid = false;
        }
        // todo: add in validation for phone numbers and email
        // contact details
        String currentAddress = AttributeValidation.checkString(addressInput.getText());
        String region = AttributeValidation.checkString(regionInput.getText());
        String homePhone = phoneInput.getText();
        String cellPhone = cellInput.getText();
        String email = emailInput.getText();

        // Emergency Contact attributes
        String eName = AttributeValidation.checkString(ecNameInput.getText());
        String eCellPhone = ecCellInput.getText();
        String eHomePhone = ecPhoneInput.getText();
        String eAddress = AttributeValidation.checkString(ecAddressInput.getText());
        String eRegion = AttributeValidation.checkString(ecRegionInput.getText());
        String eEmail = ecEmailInput.getText();
        String eRelationship = AttributeValidation.checkString(ecRelationshipInput.getText());

        // the name and cell number are required if any other attributes are filled out
        if ((eName == null || eCellPhone.isEmpty()) && (!eHomePhone.isEmpty() || eAddress != null || eRegion != null ||
                !eEmail.isEmpty() || eRelationship != null)) {
            valid = false;
            errorLabel.setText("Name and cell phone number are required for an emergency contact.");
            errorLabel.setVisible(true);
        }

        if (valid) {
            EmergencyContact contact = null;

            if (!(eName == null) && !eCellPhone.isEmpty()) {
                // create the emergency contact
                contact = new EmergencyContact(eName, eCellPhone);
                contact.setHomePhoneNumber(eHomePhone);
                contact.setAddress(eAddress);
                contact.setRegion(eRegion);
                contact.setEmail(eEmail);
                contact.setRelationship(eRelationship);
            }

            // create the new user
            User newUser = new User(nhi, dob, dod, birthGender, genderIdentity, height, weight, bloodType,
                    alcoholConsumption, smoker, currentAddress, region, homePhone, cellPhone, email, contact,
                    fName, fName, preferredFirstName, middleName, lastName);

            // add the new user to the list of users and save them
            ArrayList<User> users = controller.getUsers();
            users.add(newUser);
            saveUsers(users);

            // load to the overview page
            FXMLLoader donorLoader = new FXMLLoader(getClass().getResource("/FXML/donorView.fxml"));
            Parent root = null;

            try {
                root = donorLoader.load();
                stage.setScene(new Scene(root));
                DonorController donorController = donorLoader.getController();
                AppController.getInstance().setDonorController(donorController);
                donorController.init(AppController.getInstance(), newUser, stage,false);

            } catch (IOException e) {
                e.printStackTrace();
            }
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

        LocalDate dob = dobInput.getValue();
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
        } else if (!dob.isBefore(LocalDate.now().plusDays(1))) { // checks that the date of birth is before tomorrow's date
            invalidDOB.setVisible(true);
        }

        if (dod != null) {
            boolean datesValid = AttributeValidation.validateDates(dob, dod); // checks if the dod is before tomorrow's date and that the dob is before the dod
            if (!datesValid) {
                invalidDOD.setVisible(true);
                valid = false;
            }
        }

        //Donor donor = controller.findDonor(nhi); // checks if the nhi already exists within the system
        User user = controller.findUser(fName); // TODO: change this to check for the nhi

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