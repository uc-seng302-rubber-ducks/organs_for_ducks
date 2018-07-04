package odms.commands;

import odms.controller.AppController;
import odms.commons.model.User;
import odms.commons.model._abstract.Blockable;
import odms.commons.utils.Log;
import odms.view.CLI;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.InputStream;
import java.util.Scanner;

@Command(name = "user", description = "first name, lastname, DOB. Required will locate user and prompt for deletion")
public class DeleteUser implements Runnable, Blockable {

    private InputStream inputStream = System.in;
    private Scanner sc = new Scanner(inputStream);
    private AppController controller = AppController.getInstance();
    private User toDelete;

    @Option(names = {"-h",
            "help"}, required = false, usageHelp = true, description = "display a help message")
    private Boolean helpRequested = false;

    @Parameters(index = "0")
    private String NHI;

    @Override
    public void run() {
        if (helpRequested) {
            System.out.println(
                    "Used to delete a Donor from the current Donor pool. Donor must be confirmed before deletion");
        }

        toDelete = controller.findUser(NHI);
        if (toDelete == null) {
            System.out.println("No Donor with those details was found");
            return;
        }
        System.out.println("This will delete the following user:");
        System.out.println(toDelete);
        System.out.println("Are you sure? y/n");
        CLI.setBlockage(this);
    }

    public void setScanner(Scanner sc) {
        this.sc = sc;
    }

    public void setController(AppController controller) {
        this.controller = controller;

    }

    @Override
    public void confirm(String input) {
        if (input.equalsIgnoreCase("y")) {
            try {
                controller.deleteUser(toDelete);
                System.out.println("User successfully deleted");
                CLI.clearBlockage();
            } catch (Exception e) {
                System.out.println("Failed to delete user");
                Log.warning("failed to delete user " + NHI, e);
            }
        } else if (input.equalsIgnoreCase("n")) {
            System.out.println("Cancelled");
            CLI.clearBlockage();
        } else {
            System.out.println("Invalid option, please enter y/n to confirm or cancel");
        }
    }
}
