package odms.commands;

import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.controller.AppController;
import odms.view.IoHelper;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;

@Command(name = "donate", description = "Updates a user's organs to donate.")
public class UpdateUserDonate implements Runnable {

    @Option(names = {"-h", "help"}, usageHelp = true)
    boolean helpRequested;
    private AppController controller = AppController.getInstance();
    @Parameters(index = "0", description = "The NHI of the user to be updated")
    private String nhi;
    @Parameters(index = "1..*", description =
            "A list of the organs to be updated separated by spaces prefixed by + or / \n"
                    + "e.g. +liver /bone_marrow would add liver and remove bone marrow")
    private String[] rawOrgans;

    @Override
    public void run() {
        User user = null;
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
                    user.getDonorDetails().addOrgan(organ, null);
                    changed = true;
                    break;
                case "/":
                    user.getDonorDetails().removeOrgan(organ);
                    changed = true;
                    break;
                default:
                    IoHelper.display("could not recognise argument" + rawOrgan);
                    break;
            }
        }
        if (changed) {
            IoHelper.display("User " + user.getNhi() + " updated");
            IoHelper.display(user.getDonorDetails().getOrgans().toString());
            controller.update(user);
            controller.saveUser(user);
        }
    }

    /**
     * overrides the AppController to be used. The default one will be used unless this setter is used
     * Use for testing/mocking only
     *
     * @param controller AppController to be used
     */
    public void setController(AppController controller) {
        this.controller = controller;
    }

}
