package seng302.exception;

public class ProfileNotFoundException extends Exception {

    public ProfileNotFoundException() {
        super("The specified profile could not be found");
    }

    public ProfileNotFoundException(String message) {
        super(message);
    }
}
