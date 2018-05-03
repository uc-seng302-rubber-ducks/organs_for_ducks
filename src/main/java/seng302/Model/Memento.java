package seng302.Model;

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
    return (this.oldObject == ((Memento) obj).oldObject
        && this.newObject == ((Memento) obj).newObject);
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + oldObject.hashCode();
    result = 31 * result + newObject.hashCode();
    return result;
  }
}