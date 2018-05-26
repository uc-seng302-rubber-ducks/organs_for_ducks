package seng302.Controller.CliCommands;


import picocli.CommandLine;

@CommandLine.Command(name = "create", description = "Allows access to the creation subcommand to create all user types",
        subcommands = {CreateUser.class, CreateClinician.class})
public class Create implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.err);
    }
}
