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
}