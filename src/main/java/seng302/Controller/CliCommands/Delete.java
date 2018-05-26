package seng302.Controller.CliCommands;


import picocli.CommandLine;

@CommandLine.Command(name = "delete", description = " Command used to start the deletion process. Is required to reach the " +
        "deletion subcommands", subcommands = {DeleteUser.class, DeleteClinician.class})
public class Delete implements Runnable {

    @Override
    public void run() {
        CommandLine.usage(this, System.err);
    }
}
