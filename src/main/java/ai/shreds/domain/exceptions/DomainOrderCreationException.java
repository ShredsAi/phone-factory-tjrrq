package ai.shreds.domain.exceptions;

/**
 * Exception thrown when a purchase order cannot be created due to validation errors,
 * business rule violations, or technical issues.
 */
public class DomainOrderCreationException extends RuntimeException {

    /**
     * Creates a new DomainOrderCreationException with a message.
     *
     * @param message the exception message
     */
    public DomainOrderCreationException(String message) {
        super(message);
    }

    /**
     * Creates a new DomainOrderCreationException with a message and cause.
     *
     * @param message the exception message
     * @param cause the underlying cause of the exception
     */
    public DomainOrderCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}