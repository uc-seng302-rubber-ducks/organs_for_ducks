package odms.commons.utils;

import odms.commons.model._abstract.Undoable;

/**
 * This class serves as a utility function for assisting in Undo/Redo operations
 */
public class UndoHelpers {

    /**
     * Private constructor to prevent instantiation
     */
    private UndoHelpers() {

    }

    /**
     * Pops all but the specified number of changes off the stack.
     *
     * @param offset an denotes how many changes to leave in the stack.
     * @param obj an undoable object to remove form changes from
     * @param undoMarker point at which to stop popping off the undo stack
     */
    public static void removeFormChanges(int offset, Undoable obj, int undoMarker) {
        while (obj.getUndoStack().size() > undoMarker + offset) {
            obj.getUndoStack().pop();
        }
    }
}
