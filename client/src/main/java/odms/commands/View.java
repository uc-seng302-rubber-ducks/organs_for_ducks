package odms.commands;

import odms.model.User;
import odms.view.IoHelper;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import odms.controller.AppController;

import java.time.LocalDate;

@Command(name = "view", description = "view all currently registered users based on set parameters.")
public class View implements Runnable {

    @Option(names = {"-h", "help",
            ""}, required = false, usageHelp = true, description = "display a help message")
    private Boolean helpRequested = false;

    @Option(names = {"-a", "all", "-all"})
    private Boolean viewAll = false;

    @Option(names = {"-f", "-fname", "-n", "-name"})
    private String firstName;

    @Option(names = {"-l", "-lname"})
    private String lastName;

    @Option(names = {"-dob"})
    private String dobString;

    @Option(names = {"-NHI", "-nhi", "-NHI"})
    private String NHI = "";

    @Override
    public void run() {
        if (helpRequested) {
            System.out.println("help goes here");
        }

        AppController controller = AppController.getInstance();
        if (viewAll) {
            System.out.println(IoHelper.prettyStringUsers(controller.getUsers()));
            return;
        }
        if (!NHI.equals("")) {
            System.out.println(controller.findUser(NHI));
            return;
        }

        if (firstName != null) {
            String name;
            if (lastName != null) {
                name = firstName + " " + lastName;
            } else {
                name = firstName;
            }
            if (dobString != null) {
                LocalDate dob = IoHelper.readDate(dobString);
                if (dob != null) {
                    User user = controller.findUser(name, dob);
                    if (user == null) {
                        System.out.println("No donors found");
                    } else {
                        System.out.println(user);
                    }
                }
            } else {
                System.out.println(IoHelper
                        .prettyStringUsers(controller.findUsers(name)));
            }
        }
    }
}

