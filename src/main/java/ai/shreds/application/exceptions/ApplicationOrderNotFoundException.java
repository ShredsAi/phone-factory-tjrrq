package ai.shreds.application.exceptions;

/**
 * Exception thrown when a purchase order is not found in the application layer.
 */
public class ApplicationOrderNotFoundException extends RuntimeException {

    public ApplicationOrderNotFoundException(String orderId) {
        super("Purchase order not found: " + orderId);
    }

    public ApplicationOrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}