package odms.commons.exception;

public class InvalidOrganTypeException extends Exception {

    public InvalidOrganTypeException() {
        super("There was an invalid organ entry in a database table.");
    }

    public InvalidOrganTypeException(String message) {
        super(message);
    }

    public InvalidOrganTypeException(Throwable cause) {
        super(cause);
    }

    public InvalidOrganTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
