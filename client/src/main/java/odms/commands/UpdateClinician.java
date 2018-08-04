package odms.commands;

import odms.commons.exception.ApiException;
import odms.commons.model.Clinician;
import odms.commons.utils.AttributeValidation;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.view.IoHelper;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.io.IOException;

@CommandLine.Command(name = "clinician", description = "Allows the details for a clinician to be updated using the current staffID as the identifier")
public class UpdateClinician implements Runnable {

    AppController controller = AppController.getInstance();
    private boolean changed;

    @CommandLine.Parameters(index = "0")
    private String originalId;

    @Option(names = {"-id", "-ID", "-newID", "-newId"}, description = "The new staffID to replace the existing one")
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
        Clinician clinician;
        try {
            clinician = controller.getClinicianBridge().getClinician(originalId,controller.getToken());
        } catch (ApiException e) {
            IoHelper.display("That clinician id does not exist");
            return;
        }

        if (newId != null) {
            valid = AttributeValidation.checkString(newId);
            if (!controller.getClinicianBridge().getExists(newId) && valid) {
                clinician.setStaffId(newId);
                changed = true;
            } else {
                IoHelper.display("Could not update staff ID from " + originalId + " to " + newId);
            }
        }

        if (firstName != null) {
            clinician.setFirstName(firstName);
            valid &= AttributeValidation.checkRequiredString(firstName);
            changed = true;
        }

        if (middleName != null) {
            clinician.setMiddleName(middleName);
            valid &= AttributeValidation.checkRequiredString(middleName);
            changed = true;
        }

        if (lastName != null) {
            clinician.setLastName(lastName);
            valid &= AttributeValidation.checkRequiredString(lastName);
            changed = true;
        }

        if (password != null) {
            clinician.setPassword(password);
            changed = true;
        }

        if (streetNumber != null) {
            clinician.setStreetNumber(streetNumber);
            valid &= AttributeValidation.checkString(streetNumber);
            changed = true;
        }

        if (streetName != null) {
            clinician.setStreetName(streetName);
            valid &= AttributeValidation.checkString(streetName);
            changed = true;
        }

        if (neighborhood != null) {
            clinician.setNeighborhood(neighborhood);
            valid &= AttributeValidation.checkString(neighborhood);
            changed = true;
        }

        if (city != null) {
            clinician.setCity(city);
            valid &= AttributeValidation.checkString(city);
            changed = true;
        }

        if (region != null) {
            clinician.setRegion(region);
            valid &= AttributeValidation.checkString(region);
            changed = true;
        }

        if (zipCode != null) {
            clinician.setZipCode(zipCode);
            valid &= AttributeValidation.checkString(zipCode);
            changed = true;
        }

        if (country != null) {
            clinician.setCountry(country);
            valid &= AttributeValidation.checkString(country);
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
            IoHelper.display("Failed to update clinician: " + originalId);
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
