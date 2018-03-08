package seng302.Controller.CliCommands;

import java.io.IOException;
import java.util.Date;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import seng302.Controller.AppController;
import seng302.Model.Donor;
import seng302.Model.JsonWriter;
import seng302.Model.Organs;
import seng302.View.IoHelper;

@Command(name = "remove")
public class UpdateRemoveOrgans implements Runnable {

  @Option(names = {
      "-id"}, description = "ID number of the donor to be updated")
  private int id = -1;

  @Option(names = {"-f", "-n", "-fname", "-name"}, description = "Donor's first name. If their name is a single word, it can be entered here")
  private String fname;

  @Option(names = {"-l", "-lname"}, description = "Donor's surname")
  private String lname;

  @Option(names = {"-d", "-dob"}, description = "Donor's date of birth in format yyyy-MM-dd")
  private String dobString;

  @Parameters(description = "List of organs to be added. Use underscores for multi-word organs (e.g. bone_marrow")
  private String[] organs;

  @Option(names = {"-h", "help"}, required = false, usageHelp = true, description = "display a help message")
  private Boolean helpRequested = false;


  @Override
  public void run() {
    AppController controller = AppController.getInstance();
    Donor donor = null;

    if (id != -1) {
      donor = controller.getDonor(id);
    } else {
      if (fname != null && dobString != null) {
        String name = fname;
        Date dob = IoHelper.readDate(dobString);
        if (lname != null) {
          name += " " + lname;
        }
        donor = controller.findDonor(name, dob);

      }
    }
    if (donor != null && organs != null) {
      for (String item : organs) {
        try {
          Organs org = Organs.valueOf(item.toUpperCase());
          donor.removeOrgan(org);
        }
        catch (IllegalArgumentException ex) {
          System.err.println("Could not parse organ:" + item);
          System.err.println("multi-word organs must be entered with underscores");
          System.err.println("e.g. bone_marrow");
        } catch (NullPointerException ex) {
          System.err.println("Could not find donor");
        }
      }
      try {
        JsonWriter.saveCurrentDonorState(controller.getDonors());
        return;
      } catch (IOException ex) {
        System.err.println("Could not update file");
      }
    }
    System.err.println("Please use either the -id tag or -f, -l, and -dob to identify a donor. Organs to be removed should be specified after these arguments");
  }
}
