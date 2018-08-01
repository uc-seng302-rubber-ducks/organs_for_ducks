package odms.commands;

import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.view.IoHelper;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;

@Command(name = "view", description = "view a set donor based on NHI.",subcommands = {ViewAll.class})
public class View implements Runnable {

    private AppController appController = AppController.getInstance();

    @Option(names = {"-h", "help",
            ""}, required = false, usageHelp = true, description = "display a help message")
    private Boolean helpRequested = false;

    @Parameters(description = "ID of the user to be viewed")
    String[] params;

    @Option(names ={"-u","-user"}, description = "Sets the user flag to specify that a user is the query target.\n" +
            "This is the default option")
    private boolean afterUser = true;

    @Option(names = {"-c","-clinician"}, description = "Sets the user flag to specify that a Clinician is the query target.")
    private boolean afterClinician = false;

    @Option(names = {"-a","-admin"}, description = "Sets the user flag to specify that an Admin is the query target.")
    private boolean afterAdmin = false;

    @Override
    public void run() {

        if (helpRequested) {
            IoHelper.display("help goes here");
        }
        try {
            String id = params[0];
            if (afterClinician) {
               IoHelper.display(appController.getClinicianBridge().getClinician(id, appController.getToken()).toString());
            } else if (afterAdmin) {
                IoHelper.display(appController.getAdministratorBridge().getAdmin(id, appController.getToken()).toString());
            } else {
                IoHelper.display(appController.getUserBridge().getUser(id).toString());
            }
        } catch (IOException e){
            IoHelper.display("No User by that name was found");
            Log.severe("Io exception somehow occurred", e);
        }
    }

    public void setAppController(AppController appController){
        this.appController = appController;
    }
}

