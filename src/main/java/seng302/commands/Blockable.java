package seng302.commands;

public interface Blockable {
    /**
     * Method to call after the block is resolved
     */
    void confirm(String input);
}
