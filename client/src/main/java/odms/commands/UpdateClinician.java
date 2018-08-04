package odms.commands;

import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.commons.model.Clinician;
import odms.commons.utils.AttributeValidation;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.io.IOException;
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
            System.out.println("That clinician id does not exist");
            return;
        }

        if (newId != null) {
            if (controller.getClinician(newId) == null) {
                clinician.setStaffId(newId);
                valid = AttributeValidation.checkRequiredString(newId);
            }
        }
        if (firstName != null) {
            clinician.setFirstName(firstName);
            valid &= AttributeValidation.checkRequiredString(firstName);
            changed = true;
        }

        if (middleName != null) {
            clinician.setMiddleName(middleName);
            valid &= AttributeValidation.checkString(middleName);
            changed = true;
        }

        if (lastName != null) {
            clinician.setLastName(lastName);
            valid &= AttributeValidation.checkString(lastName);
            changed = true;
        }

        if (password != null) {
            clinician.setPassword(password);
            changed = true;
        }

        if (streetNumber != null) {
            clinician.setStreetNumber(streetNumber);
            changed = true;
        }

        if (streetName != null) {
            clinician.setStreetName(streetName);
            changed = true;
        }

        if (neighborhood != null) {
            clinician.setNeighborhood(neighborhood);
            changed = true;
        }

        if (city != null) {
            clinician.setCity(city);
            changed = true;
        }

        if (region != null) {
            clinician.setRegion(region);
            changed = true;
        }

        if (zipCode != null) {
            clinician.setZipCode(zipCode);
            changed = true;
        }

        if (country != null) {
            List<String> allowedCountries = controller.getAllowedCountries();
            if (allowedCountries.contains(country.replaceAll("_", " "))) {
                clinician.setCountry(country.replaceAll("_", " "));
                changed = true;
            } else {
                System.out.println(country + " is not one of the allowed countries\n" +
                        "For a list of the allowed countries use the command 'view countries'");
            }
        }

        if (changed) {
            controller.updateClinicians(clinician);
            try {
                controller.saveClinician(clinician);
            } catch (IOException e) {
                Log.warning("File is wrong", e);
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
