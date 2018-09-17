package odms.controller.gui.window;


import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import odms.commons.exception.InvalidFieldsException;
import odms.commons.model.EmergencyContact;
import odms.commons.model.HealthDetails;
import odms.commons.model.User;
import odms.commons.model._enum.Regions;
import odms.commons.utils.AttributeValidation;
import odms.commons.utils.Log;
import odms.controller.AppController;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;


/**
 * controller class for creating a new donor profile.
 */
public class NewUserController {

    private AppController controller;
    private Stage stage;
    //<editor-fold desc="FXML declarations">
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
    private TextField regionInput;
    @FXML
    private ComboBox<String> regionSelector;
    @FXML
    private TextField zipCode;
    @FXML
    private ComboBox<String> countrySelector;
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
    private TextField ecRegionInput;
    @FXML
    private ComboBox<String> ecRegionSelector;
    @FXML
    private TextField ecZipCode;
    @FXML
    private ComboBox<String> ecCountrySelector;
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

    //</editor-fold>
    private Stage ownStage;
    private String defaultCountry = "New Zealand";

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

        countrySelector.setItems(FXCollections.observableList(controller.getAllowedCountries()));
        ecCountrySelector.setItems(FXCollections.observableList(controller.getAllowedCountries()));
        if (!controller.getAllowedCountries().isEmpty() && !controller.getAllowedCountries().contains(defaultCountry)) {
            defaultCountry = controller.getAllowedCountries().get(0);
        }
        countrySelector.setValue(defaultCountry);
        ecCountrySelector.setValue(defaultCountry);
        for (Regions regions : Regions.values()) {
            regionSelector.getItems().add(regions.toString());
            ecRegionSelector.getItems().add(regions.toString());
        }
        regionSelector.setValue("");
        ecRegionSelector.setValue("");
        bloodComboBox.setValue("");

    }

    /**
     * If New Zealand is selected at the country combo box, the region combo box will appear.
     * If country other than New Zealand is selected at the country combo box, the region combo box will
     * be replaced with a text field.
     * region text field is cleared by default when it appears.
     * region combo box selects the first item by default when it appears.
     *
     * @param event from GUI
     */
    @FXML
    private void countrySelectorListener(ActionEvent event) {
        controller.countrySelectorEventHandler(countrySelector, regionSelector, regionInput, null, null);
    }

    /**
     * If New Zealand is selected at the country combo box, the region combo box will appear.
     * If country other than New Zealand is selected at the country combo box, the region combo box will
     * be replaced with a text field.
     * region text field is cleared by default when it appears.
     * region combo box selects the first item by default when it appears.
     *
     * @param event from GUI
     */
    @FXML
    private void ecCountrySelectorListener(ActionEvent event){
        controller.countrySelectorEventHandler(ecCountrySelector, ecRegionSelector, ecRegionInput, null, null);
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
            valid = false;
        }

        // contact details
        String streetName = street.getText();
        valid &= (AttributeValidation.checkString(streetName));

        String region;
        if (regionInput.isVisible()) {
            region = regionInput.getText();

        } else {
            region = this.regionSelector.getSelectionModel().getSelectedItem();
        }

        valid &= (AttributeValidation.checkString(region));

        String homePhone = phone.getText();
        valid &= (AttributeValidation.validatePhoneNumber(homePhone.replaceAll(" ", "")));

        String cellPhone = cell.getText();
        valid &= (AttributeValidation.validateCellNumber(cellPhone.replaceAll(" ", "")));

        String email = this.email.getText();
        valid &= (AttributeValidation.validateEmail(email));


        String neighborhood = this.neighborhood.getText();
        valid &= (AttributeValidation.checkString(neighborhood));

        String city = this.city.getText();
        valid &= (AttributeValidation.checkString(city));

        String country = this.countrySelector.getSelectionModel().getSelectedItem();
        valid &= (AttributeValidation.checkString(country));

        String streetnum = this.streetNumber.getText();
        valid &= (AttributeValidation.checkString(streetnum));

        String zipcode = this.zipCode.getText();
        valid &= (AttributeValidation.checkString(zipcode));


        if (valid) {
            // create the new user
            User newUser = new User(fName, dob, nhi);

            try {
                newUser.setMiddleName(middleName);
                newUser.setLastName(lastName);
                newUser.setPreferredFirstName(preferredFirstName);
                newUser.setHomePhone(homePhone);
                newUser.setCellPhone(cellPhone);
                newUser.setEmail(email);
                newUser.setRegion(region);
                newUser.setNeighborhood(neighborhood);
                newUser.setCity(city);
                newUser.setCountry(country);
                newUser.setStreetNumber(streetnum);
                newUser.setStreetName(streetName);
                newUser.setZipCode(zipcode);
                newUser.setProfilePhotoFilePath("");
                HealthDetails healthDetails = collectHealthDetails(birthGender, genderIdentity, height, weight, bloodType, alcoholConsumption, smoker);
                newUser.setHealthDetails(healthDetails);

                EmergencyContact contact = collectEmergencyContact(newUser);
                newUser.setContact(contact);

                newUser.getUndoStack().clear();

                // add the new user to the list of users and save them
                controller.saveUser(newUser);

                // load to the overview page
                if (stage.getTitle().matches("Administrator*")) {
                    ownStage.close();
                    FXMLLoader userLoader = new FXMLLoader(
                            getClass().getResource("/FXML/userView.fxml"));

                    try {
                        launchUserScene(nhi, newUser, userLoader);
                    } catch (IOException e) {
                        Log.severe("Failed to load User Overview for User NHI: " + nhi, e);
                    }
                } else {
                    loadUserScene(nhi, newUser);
                }
            } catch (InvalidFieldsException e) {
            }
        } else {
        }

    }

    /**
     * Helper function to Load a user onto a new scene
     *
     * @param nhi Users NHI
     * @param newUser User to be displayed
     */
    private void loadUserScene(String nhi, User newUser) {
        FXMLLoader userLoader = new FXMLLoader(
                getClass().getResource("/FXML/userView.fxml"));
        Parent root;

        try {
            root = userLoader.load();
            stage.setScene(new Scene(root));
            ownStage.close();
            UserController userController = userLoader.getController();
            controller.setUserController(userController);
            //TODO pass listeners from any preceding controllers 22/6
            userController.init(AppController.getInstance(), newUser, stage, false, null);

            Log.info("Successfully launched User Overview for User NHI: " + nhi);
        } catch (IOException e) {
            Log.severe("Failed to load User Overview for User NHI: " + nhi, e);
        }
    }

    /**
     * Launches a given FXML user scene
     *
     * @param nhi nuhi of User
     * @param newUser User to launch
     * @param userLoader loader to be launched
     * @throws IOException when user loader is incorrectly passed and controller can not be found
     */
    private void launchUserScene(String nhi, User newUser, FXMLLoader userLoader) throws IOException {
        Parent root;
        root = userLoader.load();
        Stage userStage = new Stage();
        userStage.setScene(new Scene(root));
        userStage.show();
        UserController userController = userLoader.getController();
        AppController.getInstance().setUserController(userController);
        //TODO pass listeners from any preceding controllers 22/6
        userController.init(AppController.getInstance(), newUser, userStage, false, null);
        userController.disableLogout();
        Log.info("Successfully launched User Overview for User NHI: " + nhi);
    }


    /**
     * Sets all health detail variables based off the details entered
     *
     * @param birthGender        the birth gender entered by the user
     * @param genderIdentity     the gender identity entered by the user
     * @param height             the height entered by the user
     * @param weight             the weight entered by the user
     * @param bloodType          the blood type entered by the user
     * @param alcoholConsumption the alcohol consumption entered by the user
     * @param smoker             the status of the user being a smoker or not
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
        valid &= (AttributeValidation.validateCellNumber(ecCell.getText().replaceAll(" ", "")));

        String eHomePhone = ecPhone.getText();
        valid &= (AttributeValidation.validatePhoneNumber(ecPhone.getText().replaceAll(" ", "")));

        String eStreet = ecStreet.getText();
        valid &= (AttributeValidation.checkString(ecStreet.getText()));

        String eRegion;
        if (ecRegionInput.isVisible()) {
            eRegion = ecRegionInput.getText();

        } else {
            eRegion = ecRegionSelector.getSelectionModel().getSelectedItem();
        }
        valid &= (AttributeValidation.checkString(eRegion));

        String eEmail = ecEmail.getText();
        valid &= (AttributeValidation.validateEmail(ecEmail.getText()));

        String eRelationship = ecRelationship.getText();
        valid &= (AttributeValidation.checkString(ecRelationship.getText()));


        String eneighborhood = ecNeighborhood.getText();
        valid &= (AttributeValidation.checkString(eneighborhood));

        String ecity = ecCity.getText();
        valid &= (AttributeValidation.checkString(ecity));

        String ecountry = ecCountrySelector.getSelectionModel().getSelectedItem();
        valid &= (AttributeValidation.checkString(ecountry));

        String estreetnum = ecStreetNumber.getText();
        valid &= (AttributeValidation.checkString(estreetnum));

        String ezipcode = ecZipCode.getText();
        valid &= (AttributeValidation.checkString(ezipcode));

        // the name and cell number are required if any other attributes are filled out
        if ((eName.isEmpty() != eCellPhone.isEmpty()) && valid) {
            throw new InvalidFieldsException(); // Throws invalid field exception if inputs are found to be invalid
        } else {
            EmergencyContact contact = new EmergencyContact("", "", "");
            //need this until we update undo/redo
            contact.setAttachedUser(user);

            if (!eName.isEmpty() && !eCellPhone.isEmpty()) {
                // create the emergency contact
                contact.setName(eName);
                contact.setCellPhoneNumber(eCellPhone);
                contact.setHomePhoneNumber(eHomePhone);
                contact.setStreetNumber(estreetnum);
                contact.setStreetName(eStreet);
                contact.setCity(ecity);
                contact.setCountry(ecountry);
                contact.setZipCode(ezipcode);
                contact.setRegion(eRegion);
                contact.setNeighborhood(eneighborhood);
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

        boolean valid;

        String nhi = nhiInput.getText();
        valid = (AttributeValidation.validateNHI(nhiInput.getText()));
        if (!valid) {

        }

        String fName = fNameInput.getText();
        valid &= (AttributeValidation.checkRequiredString(fNameInput.getText()));
        if (!valid) {

        }

        LocalDate dob = dobInput.getValue();

        valid &= AttributeValidation.validateDateOfBirth(dob);
        if (!valid) {

        }

        if (dob != null) {
            valid &= AttributeValidation.validateDateOfBirth(dob); // checks if the dod is before tomorrow's date and that the dob is before the dod
            if (!valid) {
                valid = false;
            }
        }

        User user = controller.findUser(nhi); // checks if the nhi already exists within the system
        if (valid && user == null) {
            createUser(nhi, fName, dob);
        } else if (valid) { // user is not null
        }
    }


    /**
     * Makes all the error messages no longer visible.
     */
}