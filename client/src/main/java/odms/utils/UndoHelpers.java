package odms.utils;

import odms.model._abstract.Undoable;

public class UndoHelpers {

    /**
     * Pops all but the specified number of changes off the stack.
     *
     * @param offset an denotes how many changes to leave in the stack.
     */
    public static void removeFormChanges(int offset, Undoable obj, int undoMarker) {
        while (obj.getUndoStack().size() > undoMarker + offset) {
            obj.getUndoStack().pop();
        }
    }
}
