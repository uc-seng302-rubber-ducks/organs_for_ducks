package odms.commons.model._abstract;

import odms.commons.model.Memento;

import java.util.Stack;

public abstract class Undoable<T> extends Deletable {

    private transient Stack<Memento<T>> undoStack = new Stack<>();
    private transient Stack<Memento<T>> redoStack = new Stack<>();

    public void setUndoStack(Stack<Memento<T>> undoStack) {
        this.undoStack = undoStack;
    }

    public Stack<Memento<T>> getUndoStack() {
        return undoStack;
    }

    public void setRedoStack(Stack<Memento<T>> redoStack) {
        this.redoStack = redoStack;
    }

    public Stack<Memento<T>> getRedoStack() {
        return redoStack;
    }

    public abstract void undo();

    public abstract void redo();
}
