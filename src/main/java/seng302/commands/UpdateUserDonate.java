package seng302.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import seng302.controller.AppController;
import seng302.model.User;
import seng302.model._enum.Organs;

@Command(name = "donate", description = "Updates a user's organs to donate.")
public class UpdateUserDonate implements Runnable {

    private AppController controller = AppController.getInstance();

    @Parameters(index = "0", description = "The NHI of the user to be updated")
    private String nhi;

    @Parameters(index = "1..*", description =
            "A list of the organs to be updated separated by spaces prefixed by + or / \n"
                    + "e.g. +liver /bone_marrow would add liver and remove bone marrow")
    private String[] rawOrgans;

    @Option(names = {"-h", "help"}, usageHelp = true)
    boolean helpRequested;

    @Override
    public void run() {
        User user = controller.findUser(nhi);
        if (user == null) {
            System.out.println("No users with this NHI could be found");
            return;
        }


        boolean changed = false;
        if (rawOrgans == null) {
            System.out.println("Please enter some organs to update");
            return;
        }
        for (String rawOrgan : rawOrgans) {
            String prefix = rawOrgan.substring(0, 1);
            Organs organ;
            try {
                String org = rawOrgan.substring(1);
                organ = Organs.valueOf(org.toUpperCase());
            } catch (IllegalArgumentException ex) {
                System.out.println("Organ " + rawOrgan + " not recognised");
                //skip this organ and try the next one
                continue;
            }
            switch (prefix) {
                case "+":
                    user.getDonorDetails().addOrgan(organ);
                    changed = true;
                    break;
                case "/":
                    user.getDonorDetails().removeOrgan(organ);
                    changed = true;
                    break;
                default:
                    System.out.println("could not recognise argument" + rawOrgan);
                    break;
            }
        }
        if (changed) {
            System.out.println("User " + user.getNhi() + " updated");
            System.out.println(user.getDonorDetails().getOrgans().toString());
            controller.update(user);
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
