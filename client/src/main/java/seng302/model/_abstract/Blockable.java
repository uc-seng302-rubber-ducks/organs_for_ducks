package seng302.model._abstract;

/**
 * Interface used by CLI commands to block further input.
 * This is useful for commands (such as deletion) that require confirmation from the user
 */
public interface Blockable {
    /**
     * Method to call after the block is resolved
     */
    void confirm(String input);
}
