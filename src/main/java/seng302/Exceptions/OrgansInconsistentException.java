package seng302.Exceptions;

public class OrgansInconsistentException extends Exception {

  public OrgansInconsistentException(String message) {
    super(message);
  }

  public OrgansInconsistentException(Throwable throwable) {
    super(throwable);
  }

  public OrgansInconsistentException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
