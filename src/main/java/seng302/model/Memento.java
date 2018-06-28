package seng302.model;


import java.util.Objects;

public class Memento<T> {

    private T oldObject;
    private T newObject;
    private T state; //New

    public Memento() {} //New

    public Memento(T savedState) { //New
        this.state = savedState;
    }

    public T getState() { //New
        return state;
    }

    public T getOldObject() {
        return oldObject;
    } //Changed

    public void setOldObject(T state) {
        this.oldObject = state;
    }

    public T getNewObject() {
        return newObject;
    } //Changed

    public void setNewObject(T state) {
        this.newObject = state;
    }

    @Override
    public String toString() {
        return "\nNEW OBJECT\n" + newObject.toString() + "\nOLD OBJECT\n" + oldObject.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Memento)) {
            return false;
        }
        return (this.oldObject.equals(((Memento) obj).oldObject)
                && this.newObject.equals(((Memento) obj).newObject));
    }

    @Override
    public int hashCode() {
        return Objects.hash(oldObject, newObject);
    }
}