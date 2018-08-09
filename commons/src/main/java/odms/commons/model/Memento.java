package odms.commons.model;


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
        return obj instanceof Memento && (this.state.equals(((Memento) obj).state));
    }

    @Override
    public int hashCode() {
        return Objects.hash(state);
    }
}