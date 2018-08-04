package odms.commands;

import odms.commons.model.User;
import odms.commons.utils.DataHandler;
import odms.commons.utils.JsonHandler;
import odms.controller.AppController;
import odms.view.IoHelper;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


@Command(name = "user", description = "first name, last name, and dob are required. all other are optional and must be tagged")
public class CreateUser implements Runnable {

    @Option(names = {"-h",
            "help"}, required = false, usageHelp = true, description = "display a help message")
    private Boolean helpRequested = false;

    @Parameters(index = "0")
    private String firstName;

    @Parameters(index = "1")
    private String lastName;

    @Parameters(index = "2", description = "NHI 'ABC1234'")
    private String NHI;

    @Parameters(index = "3", description = "format 'yyyy-mm-dd'")
    private String dobString;

    @Option(names = {"-dod"}, description = "Date of death. same formatting as dob")
    private String dodString;

    @Option(names = {"-w", "-weight"}, description = "weight in kg e.g. 87.3")
    private double weight = -1;

    @Option(names = {"-he", "-height"}, description = "height in m. e.g. 1.85")
    private double height = -1;

    @Option(names = {"-g", "-gender"}, description = "gender.")
    private String gender;

    @Option(names = {"-b", "-bloodType"}, description = "blood type")
    private String bloodType;

    @Option(names = {"-c", "-city"}, description = "Current address city")
    private String city;

    @Option(names = {"-n", "-number"}, description = "Current address number")
    private String number;

    @Option(names = {"-s", "-streetName"}, description = "Current address street name")
    private String streetName;

    @Option(names = {"-z", "-zipCode"}, description = "Current address zipCode")
    private String zipCode;

    @Option(names = {"-co", "-country"}, description = "Current address country")
    private String country;

    @Option(names = {"-ne", "-neighborhood"}, description = "Current address neighborhood")
    private String neighborhood;

    @Option(names = {"-r", "-region"}, description = "Region (Address line 2)")
    private String region;

    private DataHandler dataHandler = new JsonHandler();

    public void run() {
        AppController controller = AppController.getInstance();
        if (helpRequested) {
            System.out.println("help goes here");
            return;
        }

        LocalDate dob = IoHelper.readDate(dobString);
        if (dob == null) {
            return;
        }
        User user = new User(firstName + " " + lastName, dob, NHI);
        boolean success = controller.addUser(new User(firstName + " " + lastName, dob, NHI));
        if (!success) {
            System.out.println("An error occurred when creating registering the new user\n"
                    + "maybe a user with that NHI already exists?");
            return;
        }

        if (user == null) {
            System.out.println("User with this NHI already exists");
            return;
        }
        if (dodString != null) {
            user.setDateOfDeath(IoHelper.readDate(dodString));
        }
        if (weight != -1) {
            user.setWeight(weight);
        }
        if (height != -1) {
            user.setHeight(height);
        }
        if (gender != null) {
            user.setBirthGender(gender);
        }
        if (bloodType != null) {
            user.setBloodType(bloodType);
        }
        if (city != null) {
            user.setCity(city);
        }
        if (country != null) {
            List<String> allowedCountries = controller.getAllowedCountries();
            if (allowedCountries.contains(country.replaceAll("_", " ")) || allowedCountries.isEmpty()) {
                user.setCountry(country.replaceAll("_", " "));
            } else {
                System.out.println(country + " is not one of the allowed countries\n" +
                        "For a list of the allowed countries use the command 'view countries'");
                return;
            }
        }
        if (streetName != null) {
            user.setStreetName(streetName);
        }
        if (number != null) {
            user.setStreetNumber(number);
        }
        if (neighborhood != null) {
            user.setNeighborhood(neighborhood);
        }
        if (region != null) {
            user.setRegion(region);
        }

        System.out.println("User " + user.toString() + " has been registered with ID number");
        System.out.println(user.hashCode());
        controller.update(user);
        controller.saveUser(user);
    }

}
