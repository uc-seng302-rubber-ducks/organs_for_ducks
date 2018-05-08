package seng302.Controller.CliCommands;


import picocli.CommandLine;
import seng302.Controller.AppController;
import seng302.Controller.ClinicianController;
import seng302.Model.Clinician;

@CommandLine.Command(name = "clinician", description = "TODO describe this")
public class CreateClinician implements Runnable{

    AppController controller = AppController.getInstance();

    @CommandLine.Parameters(index = "0")
    private String id = "";

    @CommandLine.Parameters(index = "1")
    private String firstName = "";

    @CommandLine.Parameters(index = "2")
    private String password = "";

    @CommandLine.Parameters(index = "3..*")
    private String region;

    @CommandLine.Option(names = {"-h",
            "help"}, required = false, usageHelp = true, description = "display a help message")
    boolean helpRequested;

    @Override
    public void run() {
        if (controller.getClinician(id) != null) {
            System.out.println("Clinician with this id already exists");
            return;
        }

        Clinician clinician = new Clinician(firstName, id, null, region, password);
        controller.updateClinicians(clinician);
        //TODO set first name
        System.out.println(controller.getClinician(id).toString());
        System.out.println("Created new clinician with id " + id);
    }

    /**
     * sets the instance of app controller to use for testing
     * default .getInstance is used otherwise
     * @param controller AppController (or mock) to be used
     */
    public void setController(AppController controller) {
        this.controller = controller;
    }
}
