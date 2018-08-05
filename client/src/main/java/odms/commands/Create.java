package odms.commands;


import picocli.CommandLine;

@CommandLine.Command(name = "create", description = "Allows access to the creation sub-command to create public users or clinicians",
        subcommands = {CreateUser.class, CreateClinician.class})
public class Create implements Runnable {

    @CommandLine.Option(names = {"-h", "help"}, usageHelp = true)
    boolean helpRequested;

    @Override
    public void run() {
        CommandLine.usage(this, System.err);
    }
}
