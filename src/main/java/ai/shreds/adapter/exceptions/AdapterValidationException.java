package ai.shreds.adapter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when validation fails in the adapter layer.
 * This includes invalid request payloads, missing required fields, or invalid format.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AdapterValidationException extends RuntimeException {

    public AdapterValidationException(String message) {
        super(message);
    }

    public AdapterValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
