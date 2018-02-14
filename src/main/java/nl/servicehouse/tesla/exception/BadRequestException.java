package nl.servicehouse.tesla.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be thrown when exception occurs while sending request with invalid parameters
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = -8578985654581195872L;

    private static final String DEFAULT_MESSAGE = "Bad request was made";

    /**
     * Default constructor
     */
    public BadRequestException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Parameterized constructor with custom message
     *
     * @param message custom error message
     */
    public BadRequestException(final String message) {
        super(message);
    }

    /**
     * Parameterized constructor with custom cause
     *
     * @param cause cause for exception
     */
    public BadRequestException(final Throwable cause) {
        super(cause);
    }

    /**
     * Parameterized constructor with custom message and cause
     *
     * @param message custom error message
     * @param cause cause for exception
     */
    public BadRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
