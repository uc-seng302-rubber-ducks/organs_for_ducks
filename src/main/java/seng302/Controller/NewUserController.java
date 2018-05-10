package seng302.Controller;


import static seng302.Model.JsonHandler.saveUsers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import seng302.Model.EmergencyContact;
import seng302.Model.User;
import seng302.Service.AttributeValidation;


/**
 * Controller class for creating a new donor profile.
 *
 * @author acb116, are66, eli26, jha236
 */
public class NewUserController {

    AppController controller;
    Stage stage;
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
    @FXML
    private Label headerLabel;
    private Stage ownStage;


    public NewUserController() throws IOException {
    }

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
        //stage.setMinWidth(620);
        //stage.setMaxWidth(620);
    }

    /**
     * Returns the user to the login window.
     *
     * @throws IOException Throws an exception if the fxml cannot be located.
     */
    @FXML
    private void cancelCreation() throws IOException {
//        Stage primaryStage = (Stage) cancelButton.getScene().getWindow();
//        Parent root = FXMLLoader.load(getClass().getResource("/FXML/login.fxml"));
//        primaryStage.setScene(new Scene(root));

      /*  FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/loginView.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.setScene(new Scene(root));
        LoginController loginController = loader.getController();
        loginController.init(AppController.getInstance(), stage);
        stage.show();
        stage.hide();
        stage.show();*/
        ownStage.close();
    }


    /**
     * Creates the new user with at least the required attributes.
     *
     * @param nhi   The national health index.
     * @param fName First Name.
     * @param dob   Date of birth.
     * @param dod   Date of death.
     * @throws IOException
     */
    private void createUser(String nhi, String fName, LocalDate dob, LocalDate dod) throws IOException {
        boolean valid = true; // prevents the account being created if false

        // User attributes
        String preferredFirstName;
        if (preferredFNameTextField.getText().isEmpty()) {
            preferredFirstName = fName;
        } else {
            preferredFirstName = preferredFNameTextField.getText();
        }

        String middleName = AttributeValidation.checkString(mNameInput.getText()) == null ? ""
                : AttributeValidation.checkString(
                mNameInput.getText()); // checkString returns null if the textfield is empty
        String lastName = AttributeValidation.checkString(lNameInput.getText()) == null ? ""
                : AttributeValidation.checkString(lNameInput.getText());

        String birthGender = AttributeValidation.validateGender(birthGenderComboBox);
        String genderIdentity;
        if (birthGender != null && AttributeValidation.validateGender(genderIdComboBox) == null) {
            genderIdentity = birthGender;
        } else {
            genderIdentity = AttributeValidation.validateGender(genderIdComboBox);
        }
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

        // contact details
        String currentAddress = AttributeValidation.checkString(addressInput.getText()) == null ? ""
                : AttributeValidation.checkString(addressInput.getText());
        String region = AttributeValidation.checkString(regionInput.getText()) == null ? ""
                : AttributeValidation.checkString(regionInput.getText());
        String homePhone = AttributeValidation.validatePhoneNumber(phoneInput.getText());
        String cellPhone = AttributeValidation.validateCellNumber(cellInput.getText());
        String email = AttributeValidation.validateEmail(emailInput.getText());

        // validate email and phone numbers
        valid = emailCheck(email, valid);
        valid = homePhoneCheck(homePhone, valid);
        valid = cellPhoneCheck(cellPhone, valid);

        // Emergency Contact attributes
        String eName = AttributeValidation.checkString(ecNameInput.getText());
        String eCellPhone = AttributeValidation.validateCellNumber(ecCellInput.getText());
        String eHomePhone = AttributeValidation.validatePhoneNumber(ecPhoneInput.getText());
        String eAddress = AttributeValidation.checkString(ecAddressInput.getText());
        String eRegion = AttributeValidation.checkString(ecRegionInput.getText());
        String eEmail = AttributeValidation.validateEmail(ecEmailInput.getText());
        String eRelationship = AttributeValidation.checkString(ecRelationshipInput.getText());

        // validate emergency contact email and phone numbers
        valid = emailCheck(eEmail, valid);
        valid = homePhoneCheck(eHomePhone, valid);
        valid = cellPhoneCheck(eCellPhone, valid);


        // the name and cell number are required if any other attributes are filled out
        if ((eName == null || eCellPhone == null) && (eHomePhone != null || eAddress != null || eRegion != null ||
                eEmail != null || eRelationship != null || eName != null || eCellPhone != null)) {
            valid = false;
            errorLabel.setText("Name and cell phone number are required for an emergency contact.");
            errorLabel.setVisible(true);
        }

        if (valid) {
            // create the new user
            User newUser = new User(nhi, dob, dod, birthGender, genderIdentity, height, weight,
                    bloodType,
                    alcoholConsumption, smoker, currentAddress, region, homePhone, cellPhone, email, null,
                    fName, fName, preferredFirstName, middleName, lastName); //todo: ewww gross can we please change this

            EmergencyContact contact = new EmergencyContact("", "", newUser);

            if (eName != null && eCellPhone != null) {
                // create the emergency contact
                contact = new EmergencyContact(eName, eCellPhone, newUser);

                contact.setHomePhoneNumber(eHomePhone == null ? "" : eHomePhone);
                contact.setAddress(eAddress == null ? "" : eAddress);
                contact.setRegion(eRegion == null ? "" : eRegion);
                contact.setEmail(eEmail == null ? "" : eEmail);
                contact.setRelationship(eRelationship == null ? "" : eRelationship);
            }

            newUser.setContact(contact);

            newUser.getUndoStack().clear();

            // add the new user to the list of users and save them
            ArrayList<User> users = controller.getUsers();
            users.add(newUser);
            saveUsers(users);

            // load to the overview page
            if (stage.getTitle().matches("Administrator*")) {
                ownStage.close();
                FXMLLoader donorLoader = new FXMLLoader(getClass().getResource("/FXML/userView.fxml"));
                Parent root = null;

                try {
                    root = donorLoader.load();
                    Stage userStage = new Stage();
                    userStage.setScene(new Scene(root));
                    userStage.show();
                    DonorController donorController = donorLoader.getController();
                    AppController.getInstance().setDonorController(donorController);
                    donorController.init(AppController.getInstance(), newUser, userStage, false);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                FXMLLoader donorLoader = new FXMLLoader(getClass().getResource("/FXML/userView.fxml"));
                Parent root = null;

                try {
                    root = donorLoader.load();
                    stage.setScene(new Scene(root));
                    ownStage.close();
                    DonorController donorController = donorLoader.getController();
                    AppController.getInstance().setDonorController(donorController);
                    donorController.init(AppController.getInstance(), newUser, stage, false);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    /**
     * Checks that if the given email is not null, then it must be in the correct format.
     *
     * @param email The email address to be validated.
     * @param valid A boolean indicating if there is an invalid user input.
     * @return false if the email is not valid, otherwise the original value of the boolean.
     */
    private boolean emailCheck(String email, boolean valid) {
        if (email != null) {
            email = AttributeValidation.validateEmail(email);

            if (email == null) {
                valid = false;
                errorLabel.setVisible(true);
            }
        }

        return valid;
    }


    /**
     * Checks that if the given home phone number is not null, then it must be in the correct format.
     *
     * @param homeNum The home phone number to be validated.
     * @param valid   A boolean indicating if there is an invalid user input.
     * @return false if the home phone number is not valid, otherwise the original value of the boolean.
     */
    private boolean homePhoneCheck(String homeNum, boolean valid) {
        if (homeNum != null) {
            homeNum = AttributeValidation.validatePhoneNumber(homeNum);

            if (homeNum == null) {
                valid = false;
                errorLabel.setVisible(true);
            }
        }

        return valid;
    }


    /**
     * Checks that if the given cell phone number is not null, then it must be in the correct format.
     *
     * @param cellNum The cell phone number to be validated.
     * @param valid   A boolean indicating if there is an invalid user input.
     * @return false if the cell phone number is not valid, otherwise the original value of the boolean.
     */
    private boolean cellPhoneCheck(String cellNum, boolean valid) {
        if (cellNum != null) {
            cellNum = AttributeValidation.validateCellNumber(cellNum);

            if (cellNum == null) {
                valid = false;
                errorLabel.setVisible(true);
            }
        }

        return valid;
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
            valid = false;
        }

        if (dob != null && dod != null) {
            boolean datesValid = AttributeValidation.validateDates(dob, dod); // checks if the dod is before tomorrow's date and that the dob is before the dod
            if (!datesValid) {
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