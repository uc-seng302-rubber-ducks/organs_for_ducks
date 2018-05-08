package seng302.Controller.CliCommands;

import picocli.CommandLine;

/**
 * this command is never used, simply an entry point to other commands
 * Add usable commands to the subcommands of this class
 */
@CommandLine.Command(name = "root", subcommands= {View.class, Update.class, DeleteDonor.class, Create.class})
public class CliRoot implements Runnable{

  @CommandLine.Option(names = {"-h", "help"}, required = false, usageHelp = true, description = "display a help message")
  private Boolean helpRequested = false;
  @Override
  public void run() {
    //do nothing
  }
}
