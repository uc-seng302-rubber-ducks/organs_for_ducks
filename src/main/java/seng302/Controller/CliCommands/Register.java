package seng302.Controller.CliCommands;

import java.io.IOException;
import java.util.Date;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import seng302.Controller.AppController;
import seng302.Model.Donor;
import seng302.Model.JsonWriter;
import seng302.View.IoHelper;


@Command(name = "register", description = "first name, last name, and dob are required. all other are optional and must be tagged")
public class Register implements Runnable {

  @Option(names = {"-h",
      "help"}, required = false, usageHelp = true, description = "display a help message")
  private Boolean helpRequested = false;

  @Parameters(index = "0")
  private String firstName;

  @Parameters(index = "1")
  private String lastName;

  @Parameters(index = "2", description = "format 'yyyy-mm-dd'")
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

  public void run() {
    //meat goes here
    AppController controller = AppController.getInstance();
    if (helpRequested) {
      System.out.println("help goes here");
      return;
    }

    Date dob = IoHelper.readDate(dobString);
    if (dob == null) {
      return;
    }
    int id = controller.Register(firstName + " " + lastName, dob);
    Donor donor = controller.getDonor(id);

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

    System.out.println("Donor " + donor.toString() + " has been registered with ID number");
    System.out.println(donor.hashCode());
    try {
      JsonWriter.saveCurrentDonorState(controller.getDonors());
    } catch (IOException ex) {
      System.err.println("Error saving data to file\n" + ex.getMessage());
    }
  }

}
