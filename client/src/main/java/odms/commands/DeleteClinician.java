package odms.commands;

import odms.controller.AppController;
import odms.commons.model.Clinician;
import odms.commons.model._abstract.Blockable;
import odms.commons.utils.Log;
import odms.view.CLI;
import odms.view.IoHelper;
import picocli.CommandLine;

@CommandLine.Command(name = "clinician", description = "Allows a clinician to be deleted ")
public class DeleteClinician implements Runnable, Blockable {

    private AppController controller = AppController.getInstance();
    private Clinician toDelete;

    @CommandLine.Option(names = {"-h",
            "help"}, required = false, usageHelp = true, description = "display a help message")
    private Boolean helpRequested = false;


    @CommandLine.Parameters(index = "0")
    private String id;

    @Override
    public void run() {
        toDelete = controller.getClinician(id);
        if (toDelete == null) {
            IoHelper.display("Clinician with that ID not found");
            return;
        }
        IoHelper.display("This will delete the following clinician:");
        System.out.println(toDelete);
        IoHelper.display("Are you sure? y/n");
        CLI.setBlockage(this);
    }

    /**
     * this is set to AppController.getInstance by default. this setter exists for the purpose of mocking only.
     *
     * @param controller AppController instance to be used
     */
    public void setController(AppController controller) {
        this.controller = controller;
    }

    @Override
    public void confirm(String input) {
        if (input.equalsIgnoreCase("y")) {
            try {
                //old approach of using a scanner doesn't work in the new CLI
                controller.deleteClinician(toDelete);
                //TODO force listeners (Admin window) to update on deletion 22/6 - check if this is still an issue 3/8
                IoHelper.display("Clinician successfully deleted");
                CLI.clearBlockage();
            } catch (Exception e) {
                IoHelper.display("Could not delete clinician");
                Log.warning("failed to delete clinician " + id + "via cli", e);
            }
        } else if (input.equalsIgnoreCase("n")) {
            IoHelper.display("Cancelled");
            CLI.clearBlockage();
        } else {
            IoHelper.display("Invalid option, please enter y/n to confirm or cancel");
        }
    }
}
