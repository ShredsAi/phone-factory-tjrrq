package ai.shreds.application.exceptions;

/**
 * Exception thrown when supplier validation fails.
 */
public class ApplicationSupplierValidationException extends RuntimeException {

    public ApplicationSupplierValidationException(String message) {
        super(message);
    }

    public ApplicationSupplierValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}