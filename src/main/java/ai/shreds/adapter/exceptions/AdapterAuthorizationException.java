package ai.shreds.adapter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when authorization fails in the adapter layer.
 * This includes unauthorized access attempts and insufficient privileges.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AdapterAuthorizationException extends RuntimeException {

    public AdapterAuthorizationException(String message) {
        super(message);
    }

    public AdapterAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
