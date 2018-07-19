package odms.commons.exception;

import java.io.IOException;

/**
 * method of percolating errors when a bad response is received from the server
 */
public class ApiException extends IOException {

    private final int responseCode;

    public ApiException(int responseCode, String message, Throwable cause) {
        super(message, cause);
        this.responseCode = responseCode;
    }


    public ApiException(int responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

}
