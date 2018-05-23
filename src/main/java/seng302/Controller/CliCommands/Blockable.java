package seng302.Controller.CliCommands;

public interface Blockable {
    /**
     * Method to call after the block is resolved
     */
    void confirm(String input);
}
