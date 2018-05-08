package seng302.Controller.CliCommands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import seng302.Controller.AppController;
import seng302.Model.Organs;
import seng302.Model.User;

@Command(name = "donate", description = "Updates a user's rawString to donate.")
public class UpdateUserDonate implements Runnable {

  private AppController controller;
  @Parameters(description = "The NHI of the user to be updated")
  private String nhi;

  @Parameters(description =
      "A list of the rawString to be updated separated by spaces prefixed by +/- \n"
          + "e.g. +liver -bone_marrow")
  private String rawString;

  @Override
  public void run() {
    controller = AppController.getInstance();
    User user = controller.getUser(nhi);
    if (user == null) {
      System.out.println("No users with this NHI could be found");
      return;
    }

    String[] rawOrgans = rawString.split(" ");

    boolean changed = false;
    for (String rawOrgan : rawOrgans) {
      String prefix = rawOrgan.substring(0, 0);
      Organs organ;
      try {
        organ = Organs.valueOf(rawOrgan.substring(1));
      } catch (IllegalArgumentException ex) {
        System.out.println("Organ " + rawOrgan + " not recognised");
        return;
      }
      switch (prefix) {
        case "+":
          user.getDonorDetails().addOrgan(organ);
          break;
        case "-":
          user.getDonorDetails().removeOrgan(organ);
          break;
        default:
          System.out.println("could not recognise argument" + rawOrgan);
          break;
      }
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
