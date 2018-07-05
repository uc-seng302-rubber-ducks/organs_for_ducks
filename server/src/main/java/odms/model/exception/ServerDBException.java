package odms.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * generic exception to be thrown by controllers. This allows the controller to send a response code
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ServerDBException extends RuntimeException {
    public ServerDBException() {
        super();
    }
    public ServerDBException(String message, Throwable cause) {
        super(message, cause);
    }
    public ServerDBException(String message) {
        super(message);
    }
    public ServerDBException(Throwable cause) {
        super(cause);
    }
}
