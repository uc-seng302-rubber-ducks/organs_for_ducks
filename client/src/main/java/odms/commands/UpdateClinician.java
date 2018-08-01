package odms.commands;

import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.commons.model.Clinician;
import odms.commons.utils.AttributeValidation;
import odms.view.IoHelper;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@CommandLine.Command(name = "clinician", description = "Allows the details for a clinician to be updated")
public class UpdateClinician implements Runnable {

    AppController controller = AppController.getInstance();
    private boolean changed;

    @CommandLine.Parameters(index = "0")
    private String originalId;

    @Option(names = {"-id", "-ID"})
    private String newId;

    @Option(names = {"-f", "-fname"})
    private String firstName;

    @Option(names = {"-m", "-mname"})
    private String middleName;

    @Option(names = {"-l", "-lname"})
    private String lastName;

    @Option(names = {"-p", "-password"})
    private String password;

    @Option(names = {"-n", "-streetNumber"})
    private String streetNumber;

    @Option(names = {"-s", "-streetName"})
    private String streetName;

    @Option(names = {"-ne", "-neighborhood"})
    private String neighborhood;

    @Option(names = {"-c", "-city"})
    private String city;

    @Option(names = {"-r", "-region"})
    private String region;

    @Option(names = {"-z", "-zipCode"})
    private String zipCode;

    @Option(names = {"-co", "-country"})
    private String country;

    @Override
    public void run() {
        boolean valid = true;
        Clinician clinician = controller.getClinician(originalId);
        if (clinician == null) {
            IoHelper.display("That clinician id does not exist");
            return;
        }
        List<String> failMessage = new ArrayList<>();

        if (newId != null) {
            if (controller.getClinician(newId) == null) {
                clinician.setStaffId(newId);
                valid = AttributeValidation.checkRequiredString(newId);
                if (!valid) failMessage.add("Invalid StaffID: " + newId);
            }
        }
        if (firstName != null) {
            clinician.setFirstName(firstName);
            valid &= AttributeValidation.checkRequiredString(firstName);
            if (!valid) failMessage.add("Invalid first name: " + firstName);
            changed = true;
        }

        if (middleName != null) {
            clinician.setMiddleName(middleName);
            valid &= AttributeValidation.checkString(middleName);
            if (!valid) failMessage.add("Invalid middle name: " + middleName);
            changed = true;
        }

        if (lastName != null) {
            clinician.setLastName(lastName);
            valid &= AttributeValidation.checkString(lastName);
            if (!valid) failMessage.add("Invalid last name: " + lastName);
            changed = true;
        }

        if (password != null) {
            clinician.setPassword(password);
            changed = true;
        }

        if (streetNumber != null) {
            clinician.setStreetNumber(streetNumber);
            valid &= AttributeValidation.checkString(streetNumber);
            if (!valid) failMessage.add("Invalid street number: " + streetNumber);
            changed = true;
        }

        if (streetName != null) {
            clinician.setStreetName(streetName);
            valid &= AttributeValidation.checkString(streetName);
            if (!valid) failMessage.add("Invalid street name: " + streetName);
            changed = true;
        }

        if (neighborhood != null) {
            clinician.setNeighborhood(neighborhood);
            valid &= AttributeValidation.checkString(neighborhood);
            if (!valid) failMessage.add("Invalid neighborhood: " + neighborhood);
            changed = true;
        }

        if (city != null) {
            clinician.setCity(city);
            valid &= AttributeValidation.checkString(city);
            if (!valid) failMessage.add("Invalid city: " + city);
            changed = true;
        }

        if (region != null) {
            clinician.setRegion(region);
            valid &= AttributeValidation.checkString(region);
            if (!valid) failMessage.add("Invalid region: " + region);
            changed = true;
        }

        if (zipCode != null) {
            clinician.setZipCode(zipCode);
            valid &= AttributeValidation.checkString(zipCode);
            if (!valid) failMessage.add("Invalid zipCode: " + zipCode);
            changed = true;
        }

        if (country != null) {
            clinician.setCountry(country);
            valid &= AttributeValidation.checkString(country);
            if (!valid) failMessage.add("Invalid country: " + country);
            changed = true;
        }

        if (changed && valid) {
            controller.updateClinicians(clinician);
            try {
                controller.saveClinician(clinician);
                IoHelper.display("Successfully updated clinician: " + clinician.getStaffId());
            } catch (IOException e) {
                Log.warning("File is wrong", e);
            }
        } else {
            IoHelper.display("Failed to update clinician: " + clinician.getStaffId());
            for (String message: failMessage) {
                IoHelper.display(message);
            }
        }

    }

    /**
     * For testing, do not use otherwise
     *
     * @param controller controller to use for tests
     */
    public void setController(AppController controller) {
        this.controller = controller;
    }
}
