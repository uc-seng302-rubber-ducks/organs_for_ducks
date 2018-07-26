package odms.commands;


import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.commons.model.Clinician;
import odms.commons.utils.AttributeValidation;
import picocli.CommandLine;

import java.io.IOException;

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
    private String region = "";

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
            Clinician clinician = new Clinician(id, password, firstName, "", "");
            clinician.setRegion(region);
            controller.updateClinicians(clinician);
            try {
                controller.saveClinician(clinician);
            } catch (IOException e) {
                Log.warning("File is wrong", e);
            }
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
