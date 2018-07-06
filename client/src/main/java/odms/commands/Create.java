package odms.commands;


import picocli.CommandLine;

@CommandLine.Command(name = "create", description = "Allows access to the creation subcommand to create all user types",
        subcommands = {CreateUser.class, CreateClinician.class})
public class Create implements Runnable {

    @CommandLine.Option(names = {"-h", "help"}, usageHelp = true)
    boolean helpRequested;

    @Override
    public void run() {
        CommandLine.usage(this, System.err);
    }
}
