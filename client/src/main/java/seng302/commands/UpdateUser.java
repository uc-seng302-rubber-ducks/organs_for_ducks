package seng302.commands;

import picocli.CommandLine;
import picocli.CommandLine.Option;

@CommandLine.Command(name = "user", description = "subcommand specifying user updates. use this to access the donate or receive subcommands", subcommands = {
        UpdateUserDonate.class,
        UpdateUserReceive.class,
        UpdateUserDetails.class})
public class UpdateUser implements Runnable {

    @Option(names = {"-h", "help"}, usageHelp = true)
    private boolean helpRequested = true;

    @Override
    public void run() {
        CommandLine.usage(this, System.err);

    }
}
