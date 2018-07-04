package odms.exception;

public class InvalidFieldsException extends Exception {
    public InvalidFieldsException() {
        this("A field contained invalid data. Please check your fields");
    }

    public InvalidFieldsException(String message) {
        super(message);
    }
}
