package seng302.Controller.CliCommands;


import picocli.CommandLine;

@CommandLine.Command(name = "create", subcommands = {CreateUser.class, CreateClinician.class})
public class Create implements Runnable {

  @Override
  public void run() {
    CommandLine.usage(this, System.err);
  }
}
