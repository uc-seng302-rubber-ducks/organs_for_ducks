package seng302.Controller.CliCommands;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import seng302.Controller.AppController;
import seng302.Model.Clinician;
import seng302.Model.JsonHandler;

import java.io.IOException;

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

    @Option(names = {"-a", "-address"})
    private String address;

    @Option(names = {"-r", "-region"})
    private String region;

    @Override
    public void run() {
        Clinician clinician = controller.getClinician(originalId);
        if (clinician == null) {
            System.out.println("That clinician id does not exist");
            return;
        }

        if (newId != null) {
            if(controller.getClinician(newId) == null) {
                clinician.setStaffId(newId);
            }
        }
        if (firstName != null) {
            clinician.setFirstName(firstName);
            changed = true;
        }

        if (middleName != null) {
            clinician.setMiddleName(middleName);
            changed = true;
        }

        if (lastName != null) {
            clinician.setLastName(middleName);
            changed = true;
        }

        if (password != null) {
            clinician.setPassword(password);
            changed = true;
        }

        if (address != null) {
            clinician.setWorkAddress(address);
            changed = true;
        }

        if (region != null) {
            clinician.setRegion(region);
            changed = true;
        }

        //TODO input validators go here

        if (changed) {
            controller.updateClinicians(clinician);
        }

    }

    /**
     * For testing, do not use otherwise
     * @param controller controller to use for tests
     */
    public void setController(AppController controller) {
        this.controller = controller;
    }
}
