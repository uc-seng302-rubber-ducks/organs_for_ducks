package seng302.Controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import seng302.Exception.InvalidFieldsException;
import seng302.Model.EmergencyContact;
import seng302.Model.User;
import seng302.Service.AttributeValidation;
import seng302.Service.Log;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static seng302.Model.JsonHandler.saveUsers;


/**
 * Controller class for creating a new donor profile.
 */
public class NewUserController {

    AppController controller;
    Stage stage;
    //<editor-fold desc="FXML declarations">
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
    private ComboBox<String> birthGenderComboBox;
    @FXML
    private ComboBox<String> genderIdComboBox;
    @FXML
    private ComboBox<String> bloodComboBox;
    @FXML
    private CheckBox smokerCheckBox;
    @FXML
    private ComboBox<String> alcoholComboBox;
    @FXML
    private DatePicker dobInput;
    @FXML
    private DatePicker dodInput;
    //</editor-fold>
    private Stage ownStage;

    /**
     * Initializes the NewUserController
     *
     * @param controller The applications controller.
     * @param stage      The applications stage.
     */
    public void init(AppController controller, Stage stage, Stage ownStage) {
        this.controller = controller;
        this.stage = stage;
        this.ownStage = ownStage;
    }

    /**
     * Returns the user to the login window.
     */
    @FXML
    private void cancelCreation() {
        ownStage.close();
    }


    /**
     * Creates the new user with at least the required attributes.
     *
     * @param nhi   The national health index.
     * @param fName First Name.
     * @param dob   Date of birth.
     * @param dod   Date of death.
     * @throws IOException if fxml cannot is read.
     */
    //TODO: Find a way to clean this up
    private void createUser(String nhi, String fName, LocalDate dob, LocalDate dod) throws IOException {
        boolean valid; // prevents the account being created if false

        // User attributes
        String preferredFirstName = preferredFNameTextField.getText();
        valid = AttributeValidation.checkString(preferredFirstName);
        if (preferredFirstName.isEmpty()) {
            preferredFirstName = fName;
        }

        String middleName = mNameInput.getText();
        valid &= AttributeValidation.checkString(middleName);

        String lastName = lNameInput.getText();
        valid &= AttributeValidation.checkString(lastName);

        String birthGender = birthGenderComboBox.getValue();
        valid &= AttributeValidation.validateGender(birthGender);

        String genderIdentity = genderIdComboBox.getValue();
        valid &= AttributeValidation.validateGender(genderIdentity);
        if (genderIdentity.isEmpty() && !birthGender.isEmpty()) {
            genderIdentity = birthGender;
        }

        String bloodType = bloodComboBox.getValue();
        valid &= (AttributeValidation.validateBlood(bloodComboBox.getValue()));

        boolean smoker = smokerCheckBox.isSelected();

        String alcoholConsumption = alcoholComboBox.getValue();

        // validate doubles return -1 if the value is 0 or below, and 0 if the textfield is empty
        double height = AttributeValidation.validateDouble(heightInput.getText());
        double weight = AttributeValidation.validateDouble(weightInput.getText());
        if (height == -1 || weight == -1) {
            errorLabel.setVisible(true);
            valid = false;
        }

        // contact details
        String currentAddress = addressInput.getText();
        valid &= (AttributeValidation.checkString(addressInput.getText()));

        String region = regionInput.getText();
        valid &= (AttributeValidation.checkString(regionInput.getText()));

        String homePhone = phoneInput.getText();
        valid &= (AttributeValidation.validatePhoneNumber(phoneInput.getText()));

        String cellPhone = cellInput.getText();
        valid &= (AttributeValidation.validateCellNumber(cellInput.getText()));

        String email = emailInput.getText();
        valid &= (AttributeValidation.validateEmail(emailInput.getText()));

        if (valid) {
            // create the new user
            User newUser = new User(nhi, dob, dod, birthGender, genderIdentity, height, weight,
                    bloodType,
                    alcoholConsumption, smoker, currentAddress, region, homePhone, cellPhone, email, null,
                fName, fName, preferredFirstName, middleName,
                lastName); //todo: ewww gross can we please change this DELET THIS PLS
            try {
                EmergencyContact contact = collectEmergencyContact(newUser);
                newUser.setContact(contact);

                newUser.getUndoStack().clear();

                // add the new user to the list of users and save them
                List<User> users = controller.getUsers();
                users.add(newUser);
                saveUsers(users);

            // load to the overview page
            if (stage.getTitle().matches("Administrator*")) {
                ownStage.close();
                FXMLLoader userLoader = new FXMLLoader(
                    getClass().getResource("/FXML/userView.fxml"));
                Parent root;

                try {
                    root = userLoader.load();
                    Stage userStage = new Stage();
                    userStage.setScene(new Scene(root));
                    userStage.show();
                    UserController userController = userLoader.getController();
                    AppController.getInstance().setUserController(userController);
                    //TODO pass listeners from any preceding controllers 22/6
                    userController
                        .init(AppController.getInstance(), newUser, userStage, false, null);
                    userController.init(AppController.getInstance(), newUser, userStage, false, null);
                    userController.disableLogout();
                    Log.info("Successfully launched User Overview for User NHI: "+nhi);
                } catch (IOException e) {
                    Log.severe("Failed to load User Overview for User NHI: "+nhi, e);
                    e.printStackTrace();
                }
            } else {
                FXMLLoader userLoader = new FXMLLoader(
                    getClass().getResource("/FXML/userView.fxml"));
                Parent root;

                try {
                    root = userLoader.load();
                    stage.setScene(new Scene(root));
                    ownStage.close();
                    UserController userController = userLoader.getController();
                    //TODO pass listeners from any preceding controllers 22/6
                    userController.init(AppController.getInstance(), newUser, stage, false, null);

                    Log.info("Successfully launched User Overview for User NHI: "+nhi);
                    } catch (IOException e) {
                        Log.severe("Failed to load User Overview for User NHI: "+nhi, e);
                        e.printStackTrace();
                    }
                }
            } catch (InvalidFieldsException e) {
                errorLabel.setText("Name and cell phone number are required for an emergency contact.");
                errorLabel.setVisible(true);
            }
        } else {
            errorLabel.setVisible(true);
        }

    }

    /**
     * Collects and returns an EmergencyContact based off the details entered
     */
    private EmergencyContact collectEmergencyContact(User user) throws InvalidFieldsException {
        boolean valid;
        // Emergency Contact attributes
        String eName = ecNameInput.getText();
        valid = (AttributeValidation.checkString(ecNameInput.getText()));

        String eCellPhone = ecCellInput.getText();
        valid &= (AttributeValidation.validateCellNumber(ecCellInput.getText()));

        String eHomePhone = ecPhoneInput.getText();
        valid &= (AttributeValidation.validatePhoneNumber(ecPhoneInput.getText()));

        String eAddress = ecAddressInput.getText();
        valid &= (AttributeValidation.checkString(ecAddressInput.getText()));

        String eRegion = ecRegionInput.getText();
        valid &= (AttributeValidation.checkString(ecRegionInput.getText()));

        String eEmail = ecEmailInput.getText();
        valid &= (AttributeValidation.validateEmail(ecEmailInput.getText()));

        String eRelationship = ecRelationshipInput.getText();
        valid &= (AttributeValidation.checkString(ecRelationshipInput.getText()));

        // the name and cell number are required if any other attributes are filled out
        if ((eName.isEmpty() != eCellPhone.isEmpty()) && valid) {
            throw new InvalidFieldsException(); // Throws invalid field exception if inputs are found to be invalid
        } else {
            EmergencyContact contact = new EmergencyContact("", "", user);

            if (!eName.isEmpty() && !eCellPhone.isEmpty()) {
                // create the emergency contact
                contact = new EmergencyContact(eName, eCellPhone, user);

                contact.setHomePhoneNumber(eHomePhone);
                contact.setAddress(eAddress);
                contact.setRegion(eRegion);
                contact.setEmail(eEmail);
                contact.setRelationship(eRelationship);
            }
            return contact;
        }
    }


    /**
     * Sends the user to the user overview window.
     * Validates the required attributes and sends messages if they are not valid.
     *
     * @throws IOException Throws an exception if the fxml cannot be located.
     */
    @FXML
    private void confirmCreation() throws IOException {
        hideErrorMessages();
        errorLabel.setText("Error in creating profile.\n" +
                "Please make sure your details are correct.");
        boolean valid;

        String nhi = nhiInput.getText();
        valid = (AttributeValidation.validateNHI(nhiInput.getText()));
        if (!valid) {
            invalidNHI.setVisible(true);
        }

        String fName = fNameInput.getText();
        valid = (AttributeValidation.checkRequiredString(fNameInput.getText()));
        if (!valid) {
            invalidFirstName.setVisible(true);
        }

        LocalDate dob = dobInput.getValue();
        LocalDate dod = dodInput.getValue();

        valid &= AttributeValidation.validateDateOfBirth(dob);
        if (!valid) {
            invalidDOB.setVisible(true);
        }

        if (dob != null) {
            valid &= AttributeValidation.validateDateOfDeath(dob, dod); // checks if the dod is before tomorrow's date and that the dob is before the dod
            if (!valid) {
                invalidDOD.setVisible(true);
                valid = false;
            }
        }

        User user = controller.findUser(nhi); // checks if the nhi already exists within the system

        if (valid && user == null) {
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