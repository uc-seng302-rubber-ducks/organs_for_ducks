package seng302.Model.CliCommands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import seng302.Controller.AppController;
import seng302.Model.Donor;
import seng302.Model.Organs;

@Command(name = "addOrgans")
public class UpdateAddOrgans implements Runnable{

  @Parameters(index = "0")
  private int id = -1;

  @Parameters(index = "1..*")
  private String[] organs;

  @Override
  public void run() {
    if (id != -1) {
      AppController controller = AppController.getInstance();
      Donor donor = controller.getDonor(id);
      for(String item: organs) {
        try {
          Organs org = Organs.valueOf(item.toUpperCase());
          donor.addOrgan(org);
        }
        catch (IllegalArgumentException ex) {
          System.err.println("Could not parse organ:" + item);
          System.err.println("multi-word organs must be entered with underscores");
          System.err.println("e.g. bone_marrow");
        }
      }
    }
  }
}
