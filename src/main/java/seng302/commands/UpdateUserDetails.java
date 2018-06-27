package seng302.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import seng302.controller.AppController;
import seng302.model.User;
import seng302.view.IoHelper;

import java.time.LocalDate;

@Command(name = "details", description = "Use -id to identify the the user. All other tags will update values")
public class UpdateUserDetails implements Runnable {


    @Option(names = {"-id", "-nhi", "-NHI"}, required = true)
    private String NHI;

    @Option(names = {"-h",
            "help"}, required = false, usageHelp = true, description = "display a help message")
    private Boolean helpRequested = false;

    @Option(names = {"-f", "-fname"})
    private String firstName;

    @Option(names = {"-l", "-lname"})
    private String lastName;

    @Option(names = {"-newNHI", "-newnhi"})
    private String newNHI;

    @Option(names = {"-dob"}, description = "format 'yyyy-mm-dd'")
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

    @Override
    public void run() {
        Boolean changed;
        if (helpRequested) {
            System.out.println("help goes here");
            return;
        }
        AppController controller = AppController.getInstance();
        User user = controller.getUser(NHI);
        if (user == null) {
            System.err.println("Donor could not be found");
            return;
        }
        changed = IoHelper.updateName(user, firstName, lastName);

        if (dobString != null) {
            LocalDate newDate = IoHelper.readDate(dobString);
            if (newDate != null) {
                user.setDateOfBirth(newDate);
                changed = true;
            }
        }

        if (dodString != null) {
            LocalDate newDate = IoHelper.readDate(dobString);
            if (newDate != null) {
                user.setDateOfDeath(newDate);
                changed = true;
            }
        }
        if (weight != -1) {
            user.setWeight(weight);
            changed = true;
        }
        if (height != -1) {
            user.setHeight(height);
            changed = true;
        }
        if (gender != null) {
            user.setBirthGender(gender);
            changed = true;
        }
        if (bloodType != null) {
            user.setBloodType(bloodType);
            changed = true;
        }
        if (city != null) {
            user.setCity(city);
            changed = true;
        }
        if (country != null) {
            user.setCountry(country);
            changed = true;
        }
        if (streetName != null) {
            user.setStreetName(streetName);
            changed = true;
        }
        if (number != null) {
            user.setStreetNumber(number);
            changed = true;
        }
        if (neighborhood != null) {
            user.setNeighbourhood(neighborhood);
            changed = true;
        }

        if (region != null) {
            user.setRegion(region);
            changed = true;
        }
        if (newNHI != null) {
            User exists = controller.getUser(newNHI);
            if (exists != null) {
                System.out.println("User with this NHI already exists");
                return;
            }
            user.setNhi(newNHI);
            changed = true;
        }
        if (changed) {
            controller.update(user);
        }
    }
}
