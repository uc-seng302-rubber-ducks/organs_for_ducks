package seng302.commands;

import picocli.CommandLine;
import seng302.controller.AppController;
import seng302.model.Clinician;
import seng302.model._abstract.Blockable;
import seng302.service.Log;
import seng302.view.CLI;

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
            System.out.println("Clinician with that ID not found");
            return;
        }
        System.out.println("This will delete the following clinician:");
        System.out.println(toDelete);
        System.out.println("Are you sure? y/n");
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
                //TODO force listeners (Admin window) to update on deletion 22/6
                System.out.println("Clinician successfully deleted");
                CLI.clearBlockage();
            } catch (Exception e) {
                System.out.println("Could not delete clinician");
                Log.warning("failed to delete clinician " + id + "via cli", e);
            }
        } else if (input.equalsIgnoreCase("n")) {
            System.out.println("Cancelled");
            CLI.clearBlockage();
        } else {
            System.out.println("Invalid option, please enter y/n to confirm or cancel");
        }
    }
}
