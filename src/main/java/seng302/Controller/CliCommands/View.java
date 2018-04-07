package seng302.Controller.CliCommands;

import java.time.LocalDate;
import java.util.Date;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import seng302.Controller.AppController;
import seng302.Model.Donor;
import seng302.View.IoHelper;

@Command(name = "view", description = "View all currently registered donors based on set parameters.")
public class View implements Runnable {

  @Option(names = {"-h", "help",
      ""}, required = false, usageHelp = true, description = "display a help message")
  private Boolean helpRequested = false;

  @Option(names = {"-a", "all", "-all"})
  private Boolean viewAll = false;

  @Option(names = {"-f", "-fname", "-n", "-name"})
  private String firstName;

  @Option(names = {"-l", "-lname"})
  private String lastName;

  @Option(names = {"-dob"})
  private String dobString;

  @Option(names = {"-id"})
  private int id = -1;

  @Override
  public void run() {
    if (helpRequested) {
      System.out.println("help goes here");
    }

    AppController controller = AppController.getInstance();
    if (viewAll) {
      System.out.println(IoHelper.prettyStringDonors(controller.getDonors()));
      return;
    }
    if (id != -1) {
      System.out.println(controller.getDonor(id));
      return;
    }

    if (firstName != null) {
      String name;
      if (lastName != null) {
        name = firstName + " " + lastName;
      } else {
        name = firstName;
      }
      if (dobString != null) {
        LocalDate dob = IoHelper.readDate(dobString);
        if (dob != null) {
          Donor donor = controller.findDonor(name, dob);
          if(donor == (Donor)null) {
          System.out.println("No donors found");
          } else {
            System.out.println(donor);
          }
        }
      } else {
        System.out.println(IoHelper
            .prettyStringDonors(controller.findDonors(name)));
      }
    }
  }
}

