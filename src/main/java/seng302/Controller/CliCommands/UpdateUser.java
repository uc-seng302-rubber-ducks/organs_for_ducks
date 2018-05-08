package seng302.Controller.CliCommands;

import picocli.CommandLine;

@CommandLine.Command(name = "user", description = "TODO", subcommands = {UpdateUserDonate.class,
    UpdateUserReceive.class,
    UpdateDetails.class})
public class UpdateUser implements Runnable {

  @Override
  public void run() {

  }
}
