package seng302.Model.CliCommands;

import picocli.CommandLine;

/**
 * this command is never used, simply an entry point to other commands
 * Add usable commands to the subcommands of this class
 */
@CommandLine.Command(name = "root", subcommands = {Register.class, View.class, Update.class})
public class CliRoot implements Runnable {

  @Override
  public void run() {
    //do nothing
  }
}
