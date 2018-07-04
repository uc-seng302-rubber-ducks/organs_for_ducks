package odms.commands;

import odms.controller.AppController;
import odms.commons.model.User;
import odms.view.IoHelper;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.time.LocalDate;

@Command(name = "update", description =
        "Update details of a single user or clinician. Use the help flag to view subcommands"
                + "to add or remove organs", subcommands = {UpdateUser.class, UpdateClinician.class})
public class Update implements Runnable {

    @Option(names = {"-h", "-help", "help"}, usageHelp = true)
    private Boolean helpRequested = false;

    @Override
    public void run() {
        CommandLine.usage(this, System.err);
    }

    protected static User searchForUser(String NHI, String fname, String lname, String dobString,
                                        AppController controller) {
        User user = null;
        if (!NHI.equals("")) {
            user = controller.findUser(NHI);
        } else {

            if (fname != null && dobString != null) {
                String name = fname;
                LocalDate dob = IoHelper.readDate(dobString);
                if (lname != null) {
                    name += " " + lname;
                }
                user = controller.findUser(name, dob);

            }
        }
        return user;
    }
}
