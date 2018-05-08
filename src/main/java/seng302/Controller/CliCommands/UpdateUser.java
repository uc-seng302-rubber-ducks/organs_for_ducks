package seng302.Controller.CliCommands;

import picocli.CommandLine;

@CommandLine.Command(name = "user", description = "TODO", subcommands = {UpdateAddOrgans.class, UpdateRemoveOrgans.class,
        UpdateDetails.class})
public class UpdateUser implements  Runnable{
    @Override
    public void run() {

    }
}
