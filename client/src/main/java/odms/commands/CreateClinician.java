package odms.commands;

import odms.bridge.ClinicianBridge;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.commons.model.Clinician;
import odms.commons.utils.AttributeValidation;
import odms.view.IoHelper;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.util.List;

@Command(name = "clinician", description = "Allows the creation of a clinician")
public class CreateClinician implements Runnable {

    AppController controller = AppController.getInstance();

    @Parameters(index = "0")
    private String id = "";

    @Parameters(index = "1")
    private String firstName = "";

    @Parameters(index = "2")
    private String password = "";

    @Parameters(index = "3")
    private String region = "";

    @Option(names = {"-m", "-mname"})
    private String middleName;

    @Option(names = {"-l", "-lname"})
    private String lastName;

    @Option(names = {"-n", "-streetNumber"})
    private String streetNumber;

    @Option(names = {"-s", "-streetName"})
    private String streetName;

    @Option(names = {"-ne", "-neighborhood"})
    private String neighborhood;

    @Option(names = {"-c", "-city"})
    private String city;

    @Option(names = {"-z", "-zipCode"})
    private String zipCode;

    @Option(names = {"-co", "-country"})
    private String country;

    @Option(names = {"-h",
            "help"}, required = false, usageHelp = true, description = "display a help message")
    boolean helpRequested;

    @Override
    public void run() {
        if (controller.getClinician(id) != null) {
            IoHelper.display("Clinician with this id already exists");
            return;
        }

        boolean valid = AttributeValidation.checkRequiredString(id);
        valid &= AttributeValidation.checkRequiredString(firstName.replaceAll("_", " "));
        valid &= AttributeValidation.checkRequiredString(password);
        valid &= AttributeValidation.checkRequiredString(region.replaceAll("_", " "));

        Clinician clinician;
        if (valid) {

            clinician = new Clinician(id, password, firstName.replaceAll("_", " "), "", "");
            clinician.setRegion(region.replaceAll("_", " "));
        } else {
            return;
        }

        if (middleName != null) {
            clinician.setMiddleName(middleName.replaceAll("_", " "));
            valid = AttributeValidation.checkRequiredStringName(middleName.replaceAll("_", " "));
        }

        if (lastName != null) {
            clinician.setLastName(lastName.replaceAll("_", " "));
            valid &= AttributeValidation.checkRequiredStringName(lastName.replaceAll("_", " "));
        }

        if (streetNumber != null) {
            clinician.setStreetNumber(streetNumber);
            valid &= AttributeValidation.checkString(streetNumber);
        }

        if (streetName != null) {
            clinician.setStreetName(streetName.replaceAll("_", " "));
            valid &= AttributeValidation.checkString(streetName.replaceAll("_", " "));
        }

        if (neighborhood != null) {
            clinician.setNeighborhood(neighborhood.replaceAll("_", " "));
            valid &= AttributeValidation.checkString(neighborhood.replaceAll("_", " "));
        }

        if (city != null) {
            clinician.setCity(city.replaceAll("_", " "));
            valid &= AttributeValidation.checkString(city.replaceAll("_", " "));
        }

        if (zipCode != null) {
            clinician.setZipCode(zipCode);
            valid &= AttributeValidation.checkString(zipCode);
        }

        if (country != null) {
            List<String> allowedCountries = controller.getAllowedCountries();
            if (allowedCountries.contains(country.replaceAll("_", " "))) {
                clinician.setCountry(country.replaceAll("_", " "));
            } else {
                IoHelper.display(country + " is not one of the allowed countries\n" +
                        "For a list of the allowed countries use the command 'view countries'");
            }
        }

        if (region != null) {
            if (clinician.getCountry().equals("New Zealand") &&
                    controller.getAllNZRegion().contains(region.replaceAll("_", " "))) {
                clinician.setRegion(region.replaceAll("_", " "));
            } else if (!clinician.getCountry().equals("New Zealand")) {
                clinician.setRegion(region.replaceAll("_", " "));
                valid &= AttributeValidation.checkString(region.replaceAll("_", " "));
            }
        }

        if (valid) {
            controller.updateClinicians(clinician);
            try {
                controller.saveClinician(clinician);
            } catch (IOException e) {
                Log.warning("File is wrong", e);
            }
            IoHelper.display(clinician.toString());
            IoHelper.display("Created new clinician with id " + id);
        } else {
            IoHelper.display("Invalid fields entered");
        }
    }

    /**
     * sets the instance of app controller to use for testing
     * default .getInstance is used otherwise
     *
     * @param controller AppController (or mock) to be used
     */
    public void setController(AppController controller) {
        this.controller = controller;
    }
}
