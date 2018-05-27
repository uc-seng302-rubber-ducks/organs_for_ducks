package seng302.exception;

public class ProfileAlreadyExistsException extends Exception {

    public ProfileAlreadyExistsException() {
        super("A profile with that identifier already exists.");
    }

    public ProfileAlreadyExistsException(String message) {
        super(message);
    }
}
