package seng302.Controller.CliCommands;

import picocli.CommandLine;
import seng302.Controller.AppController;
import seng302.Model.Clinician;

import java.io.InputStream;
import java.util.Scanner;

@CommandLine.Command(name = "clinician", description = "Allows a clinician to be deleted ")
public class DeleteClinician implements Runnable{

    private InputStream inputStream = System.in;
    private Scanner sc = new Scanner(inputStream);

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

        System.out.println("This will delete the following clinician: " + toDelete.toString());
        System.out.println("Please enter Y/n to confirm deletion");

        while (true) {
            String confirmString = sc.next();
            if (confirmString.equalsIgnoreCase("y")) {
                //controller.deleteClinician(id); //TODO implement this when functionality exists
                System.out.println("Clinician successfully deleted");
                break;
            } else if (confirmString.equalsIgnoreCase("n")) {
                System.out.println("Clinician has not been deleted");
                break;
            } else {
                System.out.println("Input was not understood please try again");
            }
        }
    }
}
