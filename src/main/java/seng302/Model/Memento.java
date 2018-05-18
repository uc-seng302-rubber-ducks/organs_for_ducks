package seng302.Model;


import java.util.Objects;

public class Memento<T> {

  private T oldObject;
  private T newObject;

  public T getOldObject() {
    return oldObject;
  }

  public void setOldObject(T oldObject) {
    this.oldObject = oldObject;
  }

  public T getNewObject() {
    return newObject;
  }

  public void setNewObject(T newObject) {
    this.newObject = newObject;
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