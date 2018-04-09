package seng302.Controller.CliCommands;

import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;
import seng302.Controller.AppController;
import seng302.Model.Donor;
import seng302.Model.JsonWriter;
import seng302.Model.User;
import seng302.View.IoHelper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Scanner;

@Command(name="delete", description ="first name, lastname, DOB. Required will locate donor and prompt for deletion")
public class DeleteDonor implements Runnable {

    @Option(names = {"-h", "help"}, required = false, usageHelp = true, description = "display a help message")
    private Boolean helpRequested = false;



    @Parameters(index = "0")
    private String firstName;

    @Parameters(index = "1")
    private String lastName;

    @Parameters(index = "2", description = "format 'yyyy-mm-dd'")
    private String dobString;

    @Override
    public void run() {
        AppController controller = AppController.getInstance();

        if(helpRequested){
            System.out.println("Used to delete a Donor from the current Donor pool. Donor must be confirmed before deletion");
        }
        LocalDate dob = IoHelper.readDate(dobString);
        if(dob == null) {
            return;
        }
        User toDelete = controller.findUser(firstName+" "+lastName, dob);
        if(toDelete == null){
            System.out.println("No Donor with those details was found");
            return;
        }
        System.out.println("This will delete the following donor: " + toDelete.toString());
        System.out.println("Please enter Y/n to confirm deletion");
        Scanner sc = new Scanner(System.in);

        while(true) {
            String confirmString = sc.next();
            if (confirmString.equalsIgnoreCase("y")) {
                controller.deleteDonor(toDelete);
                System.out.println("Donor successfully deleted");
                break;
            } else if (confirmString.equalsIgnoreCase("n")) {
                System.out.println("Donor has not been deleted");
                break;
            } else {
                System.out.println("Input was not understood please try again");
            }
        }
        //sc.close();
        //TODO fix json writer
//        try {
//            JsonWriter.saveCurrentDonorState(controller.getUsers());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
