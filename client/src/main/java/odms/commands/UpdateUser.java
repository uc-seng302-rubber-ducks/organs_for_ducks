package odms.commands;

import picocli.CommandLine;
import picocli.CommandLine.Option;

@CommandLine.Command(name = "user", description = "Allows access to the user update sub-commands to update basic details, organ donations or organ receiving", subcommands = {
        UpdateUserDonate.class,
        UpdateUserReceive.class,
        UpdateUserDetails.class})
public class UpdateUser implements Runnable {

    @Option(names = {"-h", "help"}, usageHelp = true)
    private boolean helpRequested = true;

    @Override
    public void run() {
        CommandLine.usage(this, System.err);//NOSONAR

    }
}
