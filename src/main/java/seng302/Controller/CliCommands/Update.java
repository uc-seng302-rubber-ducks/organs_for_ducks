package seng302.Controller.CliCommands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "update", description =
    "Update details of a single donor\nUse 'update add' or 'update remove'"
        + "to add or remove organs", subcommands = {UpdateAddOrgans.class, UpdateRemoveOrgans.class,
    UpdateDetails.class})
public class Update implements Runnable {

  @Option(names = {"-h", "-help", "help"}, usageHelp = true)
  private Boolean helpRequested = false;

  @Override
  public void run() {
    if (helpRequested) {
      System.out.println("help goes here");
    }
  }
}
