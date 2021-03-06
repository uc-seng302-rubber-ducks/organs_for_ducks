package odms.commands;

import picocli.CommandLine;

/**
 * this command is never used, simply an entry point to other commands
 * Add usable commands to the subcommands of this class
 */
@CommandLine.Command(name = "root", subcommands = {View.class, Update.class, Delete.class, Create.class, Sql.class})
public class CliRoot implements Runnable {

    @CommandLine.Option(names = {"-h", "help"}, usageHelp = true, description = "display a help message")
    private Boolean helpRequested = false;

    @Override
    public void run() {
        CommandLine.usage(this, System.err); //NOSONAR
        //requires the system.err stream to be passed for the GUI CLI to work
    }
}
