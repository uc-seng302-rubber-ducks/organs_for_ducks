package odms.commons.model._abstract;

/**
 * Interface used by CLI commands to block further input.
 * This is useful for commands (such as deletion) that require confirmation from the user
 */
public interface Blockable {
    /**
     * Method to call after the block is resolved
     * @param input name of the method to be called
     */
    void confirm(String input);
}
