package seng302.model;


import java.util.Objects;

public class Memento<T> {

    private T state;

    public Memento(T savedState) {
        this.state = savedState;
    }

    public T getState() {
        return state;
    }


    @Override
    public String toString() {
        return "Memento containing state:\n" + state.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Memento)) {
            return false;
        }
        return (this.state.equals(((Memento) obj).state));
    }

    @Override
    public int hashCode() {
        return Objects.hash(state);
    }
}