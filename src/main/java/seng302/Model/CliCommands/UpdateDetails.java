package seng302.Model.CliCommands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import seng302.Controller.AppController;
import seng302.Model.Donor;
import seng302.View.IoHelper;

@Command(name = "details", description = "Use -id to identify the the donor. All other tags will update values")
public class UpdateDetails implements Runnable {


  @Option(names = {"-id"}, required = true)
  private int id;
  @Option(names = {"-h",
      "help"}, required = false, usageHelp = true, description = "display a help message")
  private Boolean helpRequested = false;

  @Option(names = {"-f", "-fname"})
  private String firstName;

  @Option(names = {"-l", "-lname"})
  private String lastName;

  @Option(names = {"-dob"}, description = "format 'yyyy-mm-dd'")
  private String dobString;

  @Option(names = {"-dod"}, description = "Date of death. same formatting as dob")
  private String dodString;

  @Option(names = {"-w", "-weight"}, description = "weight in kg e.g. 87.3")
  private double weight = -1;

  @Option(names = {"-he", "-height"}, description = "height in m. e.g. 1.85")
  private double height = -1;

  @Option(names = {"-g", "-gender"}, description = "gender.")
  private String gender;

  @Option(names = {"-b", "-bloodType"}, description = "blood type")
  private String bloodType;

  @Option(names = {"-a", "-addr",
      "-currentAddress"}, description = "Current address (Address line 1)")
  private String currentAddress;

  @Option(names = {"-r", "-region"}, description = "Region (Address line 2)")
  private String region;

  @Override
  public void run() {
    if (helpRequested) {
      System.out.println("help goes here");
      return;
    }
    AppController controller = AppController.getInstance();
    Donor donor = controller.getDonor(id);
    if (donor == null) {
      System.err.println("Donor could not be found");
      return;
    }
    IoHelper.updateName(donor, firstName, lastName);

    if (dobString != null) {
      donor.setDateOfBirth(IoHelper.readDate(dobString));
    }

    if (dodString != null) {
      donor.setDateOfDeath(IoHelper.readDate(dodString));
    }
    if (weight != -1) {
      donor.setWeight(weight);
    }
    if (height != -1) {
      donor.setHeight(height);
    }
    if (gender != null) {
      donor.setGender(gender);
    }
    if (currentAddress != null) {
      donor.setCurrentAddress(currentAddress);
    }
    if (region != null) {
      donor.setRegion(region);
    }
  }
}
