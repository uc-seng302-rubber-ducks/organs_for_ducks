package seng302.Controller.CliCommands;

import java.io.IOException;
import java.time.LocalDate;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import seng302.Controller.AppController;
import seng302.Model.JsonHandler;
import seng302.Model.User;
import seng302.View.IoHelper;


@Command(name = "user", description = "first name, last name, and dob are required. all other are optional and must be tagged")
public class CreateUser implements Runnable {

  @Option(names = {"-h",
      "help"}, required = false, usageHelp = true, description = "display a help message")
  private Boolean helpRequested = false;

  @Parameters(index = "0")
  private String firstName;

  @Parameters(index = "1")
  private String lastName;

  @Parameters(index = "2", description = "NHI 'ABC1234'")
  private String NHI;

  @Parameters(index = "3", description = "format 'yyyy-mm-dd'")
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
    AppController controller = AppController.getInstance();
    if (helpRequested) {
      System.out.println("help goes here");
      return;
    }

    LocalDate dob = IoHelper.readDate(dobString);
    if (dob == null) {
      return;
    }
    boolean success = controller.Register(firstName + " " + lastName, dob, NHI);
    if (!success) {
      System.out.println("An error occurred when creating registering the new user\n"
          + "maybe a user with that NHI already exists?");
      return;
    }
    User donor = controller.getUser(NHI);
    if (donor != null) {
      System.out.println("User with this NHI already exists");
      return;
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
    if (bloodType != null) {
      donor.setBloodType(bloodType);
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
      JsonHandler.saveUsers(controller.getUsers());
    } catch (IOException ex) {
      System.err.println("Error saving data to file\n" + ex.getMessage());
    }
  }

}
