package seng302.Controller.CliCommands;

import picocli.CommandLine;
import seng302.Controller.AppController;
import seng302.Model.Clinician;
import seng302.Service.Log;

@CommandLine.Command(name = "clinician", description = "Allows a clinician to be deleted ")
public class DeleteClinician implements Runnable{

    private AppController controller = AppController.getInstance();

    @CommandLine.Option(names = {"-h",
            "help"}, required = false, usageHelp = true, description = "display a help message")
    private Boolean helpRequested = false;


    @CommandLine.Parameters(index = "0")
    private String id;

    @Override
    public void run() {
        Clinician toDelete = controller.getClinician(id);
        if (toDelete == null) {
            System.out.println("Clinician with that ID not found");
            return;
        }
        try {
            //TODO should confirm with user before deleting 22/6
            //old approach of using a scanner doesn't work in the new CLI
            controller.deleteClinician(toDelete);
            //TODO force listeners (Admin window) to update on deletion 22/6
            System.out.println("Clinician successfully deleted");
        } catch (Exception e) {
            System.out.println("Could not delete clinician");
            Log.warning("failed to delete clinician " + id + "via cli", e);
        }
    }

    /**
     * this is set to AppController.getInstance by default. this setter exists for the purpose of mocking only.
     *
     * @param controller AppController instance to be used
     */
    public void setController(AppController controller) {
        this.controller = controller;
    }
}
