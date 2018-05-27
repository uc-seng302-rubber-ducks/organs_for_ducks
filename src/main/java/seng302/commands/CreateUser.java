package seng302.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import seng302.controller.AppController;
import seng302.model.JsonHandler;
import seng302.model.User;
import seng302.view.IoHelper;

import java.io.IOException;
import java.time.LocalDate;


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

    @Option(names = {"-a", "-addr",
            "-currentAddress"}, description = "Current address (Address line 1)")
    private String currentAddress;

    @Option(names = {"-r", "-region"}, description = "Region (Address line 2)")
    private String region;

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
        boolean success = controller.Register(firstName + " " + lastName, dob, NHI);
        if (!success) {
            System.out.println("An error occurred when creating registering the new user\n"
                    + "maybe a user with that NHI already exists?");
            return;
        }
        User user = controller.getUser(NHI);
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
            user.setGender(gender);
        }
        if (bloodType != null) {
            user.setBloodType(bloodType);
        }
        if (currentAddress != null) {
            user.setCurrentAddress(currentAddress);
        }
        if (region != null) {
            user.setRegion(region);
        }

        System.out.println("User " + user.toString() + " has been registered with ID number");
        System.out.println(user.hashCode());
        try {
            JsonHandler.saveUsers(controller.getUsers());
        } catch (IOException ex) {
            System.err.println("Error saving data to file\n" + ex.getMessage());
        }
    }

}
