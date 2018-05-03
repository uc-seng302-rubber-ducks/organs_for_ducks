package seng302.Exception;

public class UserNotFoundException extends Exception {

  public UserNotFoundException() {
    super("The specified user could not be found");
  }

  public UserNotFoundException(String message) {
    super(message);
  }
}
