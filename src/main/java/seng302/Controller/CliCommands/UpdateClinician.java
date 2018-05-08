package seng302.Controller.CliCommands;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import seng302.Controller.AppController;
import seng302.Model.Clinician;

@CommandLine.Command(name = "clinician", description = "TODO better description")
public class UpdateClinician implements Runnable{

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
        }

        //TODO input validators go here





    }
}
