package odms.controller.gui.window;


import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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

import java.io.IOException;
import java.time.LocalDate;

import static odms.commons.utils.AttributeValidation.checkString;


/**
 * controller class for creating a new donor profile.
 */
public class NewUserController {

    private AppController controller;
    private Stage stage;
    //<editor-fold desc="FXML declarations">
    @FXML
    private TextField newUserNhiInput;
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
    private TextField ecCellPhone;
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

    // error labels
    @FXML
    private Label nhiErrorLabel;
    @FXML
    private Label fNameErrorLabel;
    @FXML
    private Label pFNameErrorLabel;
    @FXML
    private Label mNameErrorLabel;
    @FXML
    private Label lNameErrorLabel;
    @FXML
    private Label dobErrorLabel;
    @FXML
    private Label heightErrorLabel;
    @FXML
    private Label weightErrorLabel;
    @FXML
    private Label homePhoneErrorLabel;
    @FXML
    private Label cellPhoneErrorLabel;
    @FXML
    private Label streetNumberErrorLabel;
    @FXML
    private Label streetNameErrorLabel;
    @FXML
    private Label neighborhoodErrorLabel;
    @FXML
    private Label cityErrorLabel;
    @FXML
    private Label regionErrorLabel;
    @FXML
    private Label zipCodeErrorLabel;
    @FXML
    private Label emailErrorLabel;
    @FXML
    private Label eNameErrorLabel;
    @FXML
    private Label eHomePhoneErrorLabel;
    @FXML
    private Label eCellPhoneErrorLabel;
    @FXML
    private Label eStreetNumberErrorLabel;
    @FXML
    private Label eStreetNameErrorLabel;
    @FXML
    private Label eNeighborhoodErrorLabel;
    @FXML
    private Label eCityErrorLabel;
    @FXML
    private Label eRegionErrorLabel;
    @FXML
    private Label eZipCodeErrorLabel;
    @FXML
    private Label eEmailErrorLabel;
    @FXML
    private Label eRelationshipErrorLabel;

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
     * Method to add a style class onto a node to make it appear invalid
     *
     * @param node node to add styleclass to
     */
    private void invalidateTextField(Node node) {
        node.getStyleClass().add("invalid");
    }

    /**
     * Checks if all the user details are valid
     *
     * @return true if all values are valid, false otherwise
     */
    private boolean validateUserDetails() {
        boolean valid = true;
        // User attributes
        String preferredFirstName = preferredFNameTextField.getText();
        if (!AttributeValidation.checkString(preferredFirstName)) {
            invalidateTextField(preferredFNameTextField);
            pFNameErrorLabel.setVisible(true);
            valid = false;
        }

        if (!checkString(mNameInput.getText())) {
            invalidateTextField(mNameInput);
            mNameErrorLabel.setVisible(true);
            valid = false;
        }

        if (!checkString(lNameInput.getText())) {
            invalidateTextField(lNameInput);
            lNameErrorLabel.setVisible(true);
            valid = false;
        }

        return valid;
    }

    /**
     * Checks if all the users health details are valid
     *
     * @return true if all the health details are valid, false otherwise
     */
    private boolean validateHealthDetails() {
        boolean valid = true;

        // validate doubles return -1 if the value is 0 or below, and 0 if the textfield is empty
        double height = AttributeValidation.validateDouble(heightInput.getText());
        double weight = AttributeValidation.validateDouble(weightInput.getText());
        if (height == -1) {
            invalidateTextField(heightInput);
            heightErrorLabel.setVisible(true);
            valid = false;
        }

        if (weight == -1) {
            invalidateTextField(weightInput);
            weightErrorLabel.setVisible(true);
            valid = false;
        }

        return valid;
    }

    /**
     * Checks if all the contact details are valid
     *
     * @return true if all contact detail fields are valid, false otherwise
     */
    private boolean validateContactDetails() {
        boolean valid = true;

        if (!AttributeValidation.checkString(streetNumber.getText())) {
            invalidateTextField(streetNumber);
            streetNumberErrorLabel.setVisible(true);
            valid = false;
        }

        if (!AttributeValidation.checkString(street.getText())) {
            invalidateTextField(street);
            streetNameErrorLabel.setVisible(true);
            valid = false;
        }

        if (!AttributeValidation.checkString(neighborhood.getText())) {
            invalidateTextField(neighborhood);
            neighborhoodErrorLabel.setVisible(true);
            valid = false;
        }

        String region;
        if (regionInput.isVisible()) {
            region = regionInput.getText();

        } else {
            region = this.regionSelector.getSelectionModel().getSelectedItem();
        }

        if (!AttributeValidation.checkString(region)) {
            invalidateTextField(regionInput);
            regionErrorLabel.setVisible(true);
            valid = false;
        }

        if (!AttributeValidation.checkString(city.getText())) {
            invalidateTextField(city);
            cityErrorLabel.setVisible(true);
            valid = false;
        }

        if (!AttributeValidation.checkString(zipCode.getText())) {
            invalidateTextField(zipCode);
            zipCodeErrorLabel.setVisible(true);
            valid = false;
        }

        if (!AttributeValidation.validateEmail(email.getText())) {
            invalidateTextField(email);
            emailErrorLabel.setVisible(true);
            valid = false;
        }

        if (!AttributeValidation.validatePhoneNumber(phone.getText().replaceAll(" ", ""))) {
            invalidateTextField(phone);
            homePhoneErrorLabel.setVisible(true);
            valid = false;
        }

        if (!AttributeValidation.validateCellNumber(cell.getText().replaceAll(" ", ""))) {
            invalidateTextField(cell);
            cellPhoneErrorLabel.setVisible(true);
            valid = false;
        }

        return valid;
    }

    private boolean validateEmergencyContactDetails() {
        boolean valid = true;

        String eName = ecName.getText();
        if (!AttributeValidation.checkRequiredStringName(eName)) {
            invalidateTextField(ecName);
            fNameErrorLabel.setVisible(true);
            valid = false;
        }

        if (!AttributeValidation.validateEmail(ecEmail.getText())) {
            invalidateTextField(ecEmail);
            eEmailErrorLabel.setVisible(true);
            valid = false;
        }

        if (!AttributeValidation.validatePhoneNumber(ecPhone.getText().replaceAll(" ", ""))) {
            invalidateTextField(ecPhone);
            eHomePhoneErrorLabel.setVisible(true);
            valid = false;
        }

        if (!AttributeValidation.validateCellNumber(ecCellPhone.getText().replaceAll(" ", ""))) {
            invalidateTextField(ecCellPhone);
            cellPhoneErrorLabel.setVisible(true);
            valid = false;
        }

        String eRegion;
        if (ecRegionInput.isVisible()) {
            eRegion = ecRegionInput.getText();

        } else {
            eRegion = ecRegionSelector.getSelectionModel().getSelectedItem();
        }
        valid &= (AttributeValidation.checkString(eRegion));

        if (!AttributeValidation.checkString(ecStreetNumber.getText())) {
            invalidateTextField(ecStreetNumber);
            eStreetNumberErrorLabel.setVisible(true);
            valid = false;
        }

        if (!AttributeValidation.checkString(ecStreet.getText())) {
            invalidateTextField(ecStreet);
            eStreetNameErrorLabel.setVisible(true);
            valid = false;
        }

        if (!AttributeValidation.checkString(ecNeighborhood.getText())) {
            invalidateTextField(ecNeighborhood);
            eNeighborhoodErrorLabel.setVisible(true);
            valid = false;
        }

        String region;
        if (ecRegionInput.isVisible()) {
            region = ecRegionInput.getText();

        } else {
            region = ecRegionSelector.getSelectionModel().getSelectedItem();
        }

        if (!AttributeValidation.checkString(region)) {
            invalidateTextField(ecRegionInput);
            eRegionErrorLabel.setVisible(true);
            valid = false;
        }

        if (!AttributeValidation.checkString(ecCity.getText())) {
            invalidateTextField(ecCity);
            eCityErrorLabel.setVisible(true);
            valid = false;
        }

        if (!AttributeValidation.checkString(ecZipCode.getText())) {
            invalidateTextField(ecZipCode);
            ecZipCode.setVisible(true);
            valid = false;
        }

        if (!AttributeValidation.validatePhoneNumber(phone.getText().replaceAll(" ", ""))) {
            invalidateTextField(phone);
            homePhoneErrorLabel.setVisible(true);
            valid = false;
        }

        if (!AttributeValidation.validateCellNumber(cell.getText().replaceAll(" ", ""))) {
            invalidateTextField(cell);
            cellPhoneErrorLabel.setVisible(true);
            valid = false;
        }

        if (!AttributeValidation.checkString(ecRelationship.getText())) {
            invalidateTextField(ecRelationship);
            eRelationshipErrorLabel.setVisible(true);
            valid = false;
        }

        return valid;
    }


    /**
     * Creates the new user with at least the required attributes.
     *
     * @param nhi   The national health index.
     * @param fName First Name.
     * @param dob   Date of birth.
     * @throws IOException if fxml cannot is read.
     */
    private void createUser(String nhi, String fName, LocalDate dob) throws IOException {
        boolean valid; // prevents the account being created if false
        valid = validateUserDetails();
        valid &= validateHealthDetails();
        valid &= validateContactDetails();
        valid &= validateEmergencyContactDetails();

        if (valid) {
            // create the new user
            User newUser = new User(fName, dob, nhi);

            try {
                newUser.setMiddleName(mNameInput.getText());
                newUser.setLastName(lNameInput.getText());
                if (preferredFNameTextField.getText().isEmpty()) {
                    newUser.setPreferredFirstName(fName);
                } else {
                    newUser.setPreferredFirstName(preferredFNameTextField.getText());
                }
                newUser.setHomePhone(phone.getText());
                newUser.setCellPhone(cell.getText());
                newUser.setEmail(email.getText());

                String region;
                if (regionInput.isVisible()) {
                    region = regionInput.getText();

                } else {
                    region = regionSelector.getSelectionModel().getSelectedItem();
                }
                newUser.setRegion(region);
                newUser.setNeighborhood(neighborhood.getText());
                newUser.setCity(city.getText());
                newUser.setCountry(countrySelector.getValue());
                newUser.setStreetNumber(streetNumber.getText());
                newUser.setStreetName(street.getText());
                newUser.setZipCode(zipCode.getText());
                newUser.setProfilePhotoFilePath("");
                HealthDetails healthDetails = collectHealthDetails();
                newUser.setHealthDetails(healthDetails);

                EmergencyContact contact = collectEmergencyContact(newUser, true);
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
     * @return the collated health details of the user
     */
    private HealthDetails collectHealthDetails() {
        HealthDetails healthDetails = new HealthDetails();

        healthDetails.setBirthGender(birthGenderComboBox.getValue());
        healthDetails.setGenderIdentity(genderIdComboBox.getValue());
        healthDetails.setHeight(Double.parseDouble(heightInput.getText()));
        healthDetails.setWeight(Double.parseDouble(weightInput.getText()));
        healthDetails.setBloodType(bloodComboBox.getValue());
        healthDetails.setAlcoholConsumption(alcoholComboBox.getValue());
        healthDetails.setSmoker(smokerCheckBox.isSelected());

        return healthDetails;
    }

    /**
     * Collects and returns an EmergencyContact based off the details entered
     */
    private EmergencyContact collectEmergencyContact(User user, boolean valid) throws InvalidFieldsException {

        // the name and cell number are required if any other attributes are filled out
        if ((ecName.getText().isEmpty() != ecCellPhone.getText().isEmpty()) && valid) {
            throw new InvalidFieldsException(); // Throws invalid field exception if inputs are found to be invalid
        } else {
            EmergencyContact contact = new EmergencyContact("", "", "");
            //need this until we update undo/redo
            contact.setAttachedUser(user);

            if (!ecName.getText().isEmpty() && !ecCellPhone.getText().isEmpty()) {
                // create the emergency contact
                contact.setName(ecName.getText());
                contact.setCellPhoneNumber(ecCellPhone.getText());
                contact.setHomePhoneNumber(ecPhone.getText());
                contact.setStreetNumber(ecStreetNumber.getText());
                contact.setStreetName(eStreetNameErrorLabel.getText());
                contact.setCity(ecCity.getText());
                contact.setCountry(ecCountrySelector.getValue());
                contact.setZipCode(ecZipCode.getText());
                String region;
                if (!ecRegionInput.getText().isEmpty()) {
                    region = ecRegionInput.getText();
                } else {
                    region = ecRegionSelector.getValue();
                }
                contact.setRegion(region);
                contact.setNeighborhood(ecNeighborhood.getText());
                contact.setEmail(ecEmail.getText());
                contact.setRelationship(ecRelationship.getText());
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
        boolean valid = true;
        String nhi = newUserNhiInput.getText();
        if (!AttributeValidation.validateNHI(nhi)) {
            invalidateTextField(newUserNhiInput);
            nhiErrorLabel.setText("Enter a valid NHI. e.g. ABC1234");
            nhiErrorLabel.setVisible(true);
            valid = false;
        } else if (controller.getUserBridge().getExists(nhi)) {
            invalidateTextField(newUserNhiInput);
            nhiErrorLabel.setText("This NHI is already in use");
            nhiErrorLabel.setVisible(true);
            valid = false;
        }

        String fName = fNameInput.getText();
        if (!AttributeValidation.checkRequiredStringName(fName)) {
            String error;
            if (fName.isEmpty()) {
                error = "The first name cannot be empty";
            } else {
                error = "Only alphanumeric characters are allowed";
            }
            invalidateTextField(fNameInput);
            fNameErrorLabel.setText(error);
            fNameErrorLabel.setVisible(true);
            valid = false;
        }

        LocalDate dob = dobInput.getValue();
        if (!AttributeValidation.validateDateOfBirth(dob)) {
            String error;
            if (dob == null) {
                error = "A date of birth must be selected";
            } else {
                error = "The date of birth must be before the current date";
            }
            invalidateTextField(dobInput);
            dobErrorLabel.setText(error);
            dobErrorLabel.setVisible(true);
            valid = false;
        }

        if (valid) {
            createUser(nhi, fName, dob);
        }
    }


}