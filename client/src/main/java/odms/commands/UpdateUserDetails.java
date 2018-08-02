package odms.commands;

import odms.commons.model.User;
import odms.controller.AppController;
import odms.view.IoHelper;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.time.LocalDate;

@Command(name = "details", description = "Use -id to identify the the user. All other tags will update values")
public class UpdateUserDetails implements Runnable {


    @CommandLine.Parameters(index = "0")
    private String originalNhi;

    @Option(names = {"-h",
            "help"}, required = false, usageHelp = true, description = "display a help message")
    private Boolean helpRequested = false;

    @Option(names = {"-f", "-fname"})
    private String firstName;

    @Option(names = {"-m", "-mname"})
    private String middleName;

    @Option(names = {"-l", "-lname"})
    private String lastName;

    @Option(names = {"-id", "-nhi", "-NHI", "-newNHI", "-newnhi"})
    private String newNHI;

    @Option(names = {"-dob"}, description = "format 'yyyy-mm-dd'")
    private String dobString;

    @Option(names = {"-dod"}, description = "Date of death. same formatting as dob")
    private String dodString;

    @Option(names = {"-w", "-weight"}, description = "weight in kg e.g. 87.3")
    private double weight = -1;

    @Option(names = {"-he", "-height"}, description = "height in m. e.g. 1.85")
    private double height = -1;

    @Option(names = {"-g", "-birthgender"}, description = "Users birth gender")
    private String gender;

    @Option(names = {"-gi", "-genderIdentity"}, description = "Gender that the user identifies by")
    private String genderIdentity;

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

    private AppController controller = AppController.getInstance();

    @Override
    public void run() {
        Boolean changed;
        if (helpRequested) {
            IoHelper.display("help goes here");
            return;
        }

        User user;
        try {
            user = controller.getUserBridge().getUser(originalNhi);
        } catch (IOException e) {
            IoHelper.display("Donor could not be found");
            return;
        }
        changed = IoHelper.updateName(user, firstName, lastName);

        if (middleName != null) {
            user.setMiddleName(middleName);
            changed = true;
        }

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
        if (genderIdentity != null) {
            user.setGenderIdentity(genderIdentity);
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
            user.setNeighborhood(neighborhood);
            changed = true;
        }

        if (region != null) {
            user.setRegion(region);
            changed = true;
        }
        if (newNHI != null) {
            boolean exists = controller.getUserBridge().getExists(newNHI);
            if (exists) {
                IoHelper.display("User with this nhi already exists");
                return;
            }
            user.setNhi(newNHI);
            changed = true;
        }
        if (changed) {
            controller.update(user);
            controller.saveUser(user);
            IoHelper.display("Successfully updated user:"+ originalNhi);
        }
    }

    public void setAppController(AppController appController){
        this.controller = appController;
    }
}
