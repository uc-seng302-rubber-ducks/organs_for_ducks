package seng302.commands;


import picocli.CommandLine;
import seng302.controller.AppController;
import seng302.model.Clinician;
import seng302.utils.AttributeValidation;

@CommandLine.Command(name = "clinician", description = "Allows the creation of a clinician. ot update use update clinician")
public class CreateClinician implements Runnable {

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

        boolean valid = AttributeValidation.checkRequiredString(id);
        valid &= AttributeValidation.checkRequiredString(firstName);
        valid &= AttributeValidation.checkRequiredString(password);
        valid &= AttributeValidation.checkRequiredString(region);

        if (valid) {
            Clinician clinician = new Clinician(firstName, id, null, region, password);
            controller.updateClinicians(clinician);
            System.out.println(clinician.toString());
            System.out.println("Created new clinician with id " + id);
        } else {
            System.out.println("Invalid fields entered");
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
