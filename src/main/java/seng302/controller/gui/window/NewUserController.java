package seng302.controller.gui.window;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import seng302.controller.AppController;
import seng302.exception.InvalidFieldsException;
import seng302.model.EmergencyContact;
import seng302.model.HealthDetails;
import seng302.model.User;
import seng302.model.datamodel.Address;
import seng302.utils.AttributeValidation;
import seng302.utils.Log;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static seng302.utils.JsonHandler.saveUsers;


/**
 * controller class for creating a new donor profile.
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
    private TextField phone;
    @FXML
    private TextField cell;
    @FXML
    private TextField streetNumber;
    @FXML
    private TextField street;
    @FXML
    private TextField neighborhood;
    @FXML
    private TextField city;
    @FXML
    private ComboBox<String> region;
    @FXML
    private TextField zipCode;
    @FXML
    private TextField country;
    @FXML
    private TextField email;
    @FXML
    private TextField ecName;
    @FXML
    private TextField ecPhone;
    @FXML
    private TextField ecCell;
    @FXML
    private TextField ecStreetNumber;
    @FXML
    private TextField ecStreet;
    @FXML
    private TextField ecNeighborhood;
    @FXML
    private TextField ecCity;
    @FXML
    private ComboBox<String> ecRegion;
    @FXML
    private TextField ecZipCode;
    @FXML
    private TextField ecCounrty;
    @FXML
    private TextField ecEmail;
    @FXML
    private TextField ecRelationship;
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
     * @throws IOException if fxml cannot is read.
     */
    //TODO: Find a way to clean this up
    private void createUser(String nhi, String fName, LocalDate dob) throws IOException {
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
        String currentAddress = street.getText();
        valid &= (AttributeValidation.checkString(street.getText()));

        String region = this.region.getSelectionModel().getSelectedItem();
        valid &= (AttributeValidation.checkString(this.region.getSelectionModel().getSelectedItem()));

        String homePhone = phone.getText();
        valid &= (AttributeValidation.validatePhoneNumber(phone.getText()));

        String cellPhone = cell.getText();
        valid &= (AttributeValidation.validateCellNumber(cell.getText()));

        String email = this.email.getText();
        valid &= (AttributeValidation.validateEmail(this.email.getText()));

        if (valid) {
            // create the new user
            User newUser = new User(fName, dob, nhi);
            //User newUser = new User(nhi, dob, dod, currentAddress, region, homePhone, cellPhone, email, null,
            //      fName, fName, preferredFirstName, middleName,
//                    lastName); //todo: ewww gross can we please change this DELET THIS PLS

            try {
                newUser.setRegion(region);
                newUser.setHomePhone(homePhone);
                newUser.setCellPhone(cellPhone);
                newUser.setEmail(email);
                // newUser.setCurrentAddress();
                HealthDetails healthDetails = collectHealthDetails(birthGender, genderIdentity, height, weight, bloodType, alcoholConsumption, smoker);
                newUser.setHealthDetails(healthDetails);

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
                        Log.info("Successfully launched User Overview for User NHI: " + nhi);
                    } catch (IOException e) {
                        Log.severe("Failed to load User Overview for User NHI: " + nhi, e);
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

                        Log.info("Successfully launched User Overview for User NHI: " + nhi);
                    } catch (IOException e) {
                        Log.severe("Failed to load User Overview for User NHI: " + nhi, e);
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

    private Address collectAddress(String number, String street, String neighborhood, String city, String region, String zipcode, String country) {
        Address address = new Address(number, street, neighborhood, city, region, zipcode, country);

        return address;

    }

    /**
     * Sets all health detail variables based off the details entered
     * @param birthGender the birth gender entered by the user
     * @param genderIdentity the gender identity entered by the user
     * @param height the height entered by the user
     * @param weight the weight entered by the user
     * @param bloodType the blood type entered by the user
     * @param alcoholConsumption the alcohol consumption entered by the user
     * @param smoker the status of the user being a smoker or not
     * @return the collated health details of the user
     */
    private HealthDetails collectHealthDetails(String birthGender, String genderIdentity, double height, double weight,
                                               String bloodType, String alcoholConsumption, boolean smoker) {
        HealthDetails healthDetails = new HealthDetails();

        healthDetails.setBirthGender(birthGender);
        healthDetails.setGenderIdentity(genderIdentity);
        healthDetails.setHeight(height);
        healthDetails.setWeight(weight);
        healthDetails.setBloodType(bloodType);
        healthDetails.setAlcoholConsumption(alcoholConsumption);
        healthDetails.setSmoker(smoker);

        return healthDetails;
    }

    /**
     * Collects and returns an EmergencyContact based off the details entered
     */
    private EmergencyContact collectEmergencyContact(User user) throws InvalidFieldsException {
        boolean valid;
        // Emergency Contact attributes
        String eName = ecName.getText();
        valid = (AttributeValidation.checkString(ecName.getText()));

        String eCellPhone = ecCell.getText();
        valid &= (AttributeValidation.validateCellNumber(ecCell.getText()));

        String eHomePhone = ecPhone.getText();
        valid &= (AttributeValidation.validatePhoneNumber(ecPhone.getText()));

        String eAddress = ecStreet.getText();
        valid &= (AttributeValidation.checkString(ecStreet.getText()));

        String eRegion = ecRegion.getSelectionModel().getSelectedItem();
        valid &= (AttributeValidation.checkString(ecRegion.getSelectionModel().getSelectedItem()));

        String eEmail = ecEmail.getText();
        valid &= (AttributeValidation.validateEmail(ecEmail.getText()));

        String eRelationship = ecRelationship.getText();
        valid &= (AttributeValidation.checkString(ecRelationship.getText()));

        // the name and cell number are required if any other attributes are filled out
        if ((eName.isEmpty() != eCellPhone.isEmpty()) && valid) {
            throw new InvalidFieldsException(); // Throws invalid field exception if inputs are found to be invalid
        } else {
            EmergencyContact contact = new EmergencyContact(null, null, null);

            if (!eName.isEmpty() && !eCellPhone.isEmpty()) {
                // create the emergency contact
                contact = new EmergencyContact(eName, null, eCellPhone);

                contact.setHomePhoneNumber(eHomePhone);
                Address address = new Address(null, eAddress, null, null, eRegion, null, null);
                contact.setAddress(address);
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
            createUser(nhi, fName, dob);
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