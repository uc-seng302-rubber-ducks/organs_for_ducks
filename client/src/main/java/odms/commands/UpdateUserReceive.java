package odms.commands;

import odms.commons.model.User;
import odms.commons.model._enum.OrganDeregisterReason;
import odms.commons.model._enum.Organs;
import odms.controller.AppController;
import odms.view.IoHelper;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;

@Command(name = "receive", description = "updates a user's organs to be received")
public class UpdateUserReceive implements Runnable {

    private AppController controller = AppController.getInstance();

    @Parameters(index = "0", description = "The NHI of the user to be updated")
    private String nhi;

    @Parameters(index = "1..*", description =
            "A list of the organs to be updated separated by spaces prefixed by + or / \n"
                    + "e.g. +liver /bone_marrow would add a liver and remove bone marrow")
    private String[] rawOrgans;

    @Option(names = {"-h", "help"}, usageHelp = true)
    boolean helpRequested;

    @Override
    public void run() {
        User user;
        try {
            user = controller.getUserBridge().getUser(nhi);
        } catch (IOException e) {
            IoHelper.display("No users with this NHI could be found");
            return;
        }

        boolean changed = false;
        if (rawOrgans == null) {
            IoHelper.display("Please enter some organs to update");
            return;
        }
        for (String rawOrgan : rawOrgans) {
            String prefix = rawOrgan.substring(0, 1);
            Organs organ;
            try {
                String org = rawOrgan.substring(1);
                organ = Organs.valueOf(org.toUpperCase());
            } catch (IllegalArgumentException ex) {
                IoHelper.display("Organ " + rawOrgan + " not recognised");
                //skip this organ and try the next one
                continue;
            }
            switch (prefix) {
                case "+":
                    changed = user.getReceiverDetails().startWaitingForOrgan(organ);
                    break;
                case "/":
                    changed = user.getReceiverDetails().stopWaitingForOrgan(organ, OrganDeregisterReason.TRANSPLANT_RECEIVED);
                    break;
                default:
                    IoHelper.display("could not recognise argument" + rawOrgan);
                    break;
            }
        }
        if (changed) {
            IoHelper.display("User " + user.getNhi() + " updated");
            IoHelper.display(user.getReceiverDetails().stringIsWaitingFor());
            controller.update(user);
            controller.saveUser(user);
        }
    }

    /**
     * sets the preferred AppController to be used. This is set by default and should only be
     * overridden for testing/mocking
     *
     * @param controller AppController to be used
     */
    public void setController(AppController controller) {
        this.controller = controller;
    }
}
