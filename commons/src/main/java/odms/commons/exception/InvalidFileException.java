package odms.commons.exception;

public class InvalidFileException extends Exception {

    public InvalidFileException() {
        this("Invalid file selected.");
    }

    public InvalidFileException(String message) {
        super(message);
    }
}
