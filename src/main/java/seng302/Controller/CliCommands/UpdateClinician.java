package seng302.Controller.CliCommands;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import seng302.Controller.AppController;
import seng302.Model.Clinician;
import seng302.Model.JsonHandler;

import java.io.IOException;

@CommandLine.Command(name = "clinician", description = "TODO better description")
public class UpdateClinician implements Runnable{

    private boolean changed = false;

    AppController controller = AppController.getInstance();

    @Option(names = {"-id", "-ID"}, required = true)
    private String id = "";

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
        Clinician clinician = controller.getClinician(id);
        if (clinician == null) {
            System.out.println("That clinician id does not exist");
            return;
        }

        if (id != null) {
            clinician.setStaffId(id);
            changed = true;
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
            try {
                JsonHandler.saveUsers(controller.getUsers());
                //JsonWriter.saveCurrentDonorState(controller.getUsers());
            } catch (IOException ex) {
                System.err.println("Could not update details on file");
            }
        }

    }
}
