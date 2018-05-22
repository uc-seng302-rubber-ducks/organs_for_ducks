package seng302.Controller.CliCommands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import seng302.Controller.AppController;
import seng302.Model.User;
import seng302.Service.Log;

import java.io.InputStream;
import java.util.Scanner;

@Command(name = "user", description = "first name, lastname, DOB. Required will locate user and prompt for deletion")
public class DeleteUser implements Runnable {

  private InputStream inputStream = System.in;
  private Scanner sc = new Scanner(inputStream);
  private AppController controller = AppController.getInstance();

  @Option(names = {"-h",
      "help"}, required = false, usageHelp = true, description = "display a help message")
  private Boolean helpRequested = false;

  @Parameters(index = "0")
  private String NHI;

  @Override
  public void run() {
    if (helpRequested) {
      System.out.println(
          "Used to delete a Donor from the current Donor pool. Donor must be confirmed before deletion");
    }

    User toDelete = controller.findUser(NHI);
    if (toDelete == null) {
      System.out.println("No Donor with those details was found");
      return;
    }
    try {
      controller.deleteUser(toDelete);
      System.out.println("User successfully deleted");
    } catch (Exception e) {
      System.out.println("Failed to delete user");
      Log.warning("failed to delete user " + NHI, e);
    }
  }

  public void setScanner(Scanner sc) {
    this.sc = sc;
  }

  public void setController(AppController controller) {
    this.controller = controller;

  }
}
