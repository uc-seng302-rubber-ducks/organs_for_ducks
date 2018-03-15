package seng302.Controller.CliCommands;

import java.io.IOException;
import java.util.Date;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import seng302.Controller.AppController;
import seng302.Model.Donor;
import seng302.Model.JsonWriter;
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
    Boolean changed;
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
    changed = IoHelper.updateName(donor, firstName, lastName);

    if (dobString != null) {
      Date newDate = IoHelper.readDate(dobString);
      if (newDate != null) {
        donor.setDateOfBirth(newDate);
        changed = true;
      }
    }

    if (dodString != null) {
      Date newDate = IoHelper.readDate(dobString);
      if (newDate != null) {
        donor.setDateOfDeath(newDate);
        changed = true;
      }
    }
    if (weight != -1) {
      donor.setWeight(weight);
      changed = true;
    }
    if (height != -1) {
      donor.setHeight(height);
      changed = true;
    }
    if (gender != null) {
      donor.setGender(gender);
      changed = true;
    }
    if (bloodType != null) {
      donor.setBloodType(bloodType);
      changed = true;
    }
    if (currentAddress != null) {
      donor.setCurrentAddress(currentAddress);
      changed = true;
    }
    if (region != null) {
      donor.setRegion(region);
      changed = true;
    }
    if (changed == true) {
      try {
        JsonWriter.saveCurrentDonorState(controller.getDonors());
      }
      catch (IOException ex) {
        System.err.println("Could not update details on file");
      }
    }
  }
}
