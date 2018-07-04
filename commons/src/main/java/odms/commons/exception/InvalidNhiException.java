package odms.commons.exception;

public class InvalidNhiException extends Exception {

    public InvalidNhiException() {
        this("Invalid NHI was entered");
    }

    public InvalidNhiException(String message) {
        super(message);
    }
}
