package seng302.Exception;

public class UserAlreadyExistsException extends Exception {

  public UserAlreadyExistsException() {
    super("A user with that NHI already exists");
  }

  public UserAlreadyExistsException(String message) {
    super(message);
  }
}
