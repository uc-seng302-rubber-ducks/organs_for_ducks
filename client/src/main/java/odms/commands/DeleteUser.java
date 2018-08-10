package odms.commands;

import odms.commons.model.User;
import odms.commons.model._abstract.Blockable;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.view.CLI;
import odms.view.IoHelper;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;

@Command(name = "user", description = "Requires nhi to locate user and prompt for deletion")
public class DeleteUser implements Runnable, Blockable {

    private AppController controller = AppController.getInstance();
    private User toDelete;

    @Option(names = {"-h",
            "help"}, usageHelp = true, description = "display a help message")
    private Boolean helpRequested = false;

    @Parameters(index = "0")
    private String nhi;

    @Override
    public void run() {
        if (helpRequested) {
            IoHelper.display(
                    "Used to delete a Donor from the current Donor pool. Donor must be confirmed before deletion");
        }

        try {
            toDelete = controller.getUserBridge().getUser(nhi);
        } catch (IOException e) {
            Log.warning("Failed to get user " + nhi + " on the CLI to delete", e);
        }

        if (toDelete == null) {
            IoHelper.display("No Donor with those details was found");
            return;
        }
        IoHelper.display("This will delete the following user:");
        IoHelper.display(toDelete.toString());
        IoHelper.display("Are you sure? y/n");
        CLI.setBlockage(this);
    }

    public void setController(AppController controller) {
        this.controller = controller;

    }

    @Override
    public void confirm(String input) {
        if (input.equalsIgnoreCase("y")) {
            try {
                controller.deleteUser(toDelete);
                IoHelper.display("User successfully deleted");
                CLI.clearBlockage();
            } catch (Exception e) {
                IoHelper.display("Failed to delete user");
                Log.warning("failed to delete user " + nhi, e);
            }
        } else if (input.equalsIgnoreCase("n")) {
            IoHelper.display("Cancelled");
            CLI.clearBlockage();
        } else {
            IoHelper.display("Invalid option, please enter y/n to confirm or cancel");
        }
    }
}
