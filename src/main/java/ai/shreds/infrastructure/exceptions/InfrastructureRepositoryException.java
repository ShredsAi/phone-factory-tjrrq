package ai.shreds.infrastructure.exceptions;

/**
 * Exception thrown when repository operations in the infrastructure layer fail.
 * This includes database access errors, JPA exceptions, and data integrity issues.
 */
public class InfrastructureRepositoryException extends RuntimeException {

    /**
     * Constructs a new repository exception with the specified error message.
     *
     * @param message the error message
     */
    public InfrastructureRepositoryException(String message) {
        super(message);
    }

    /**
     * Constructs a new repository exception with the specified error message and cause.
     *
     * @param message the error message
     * @param cause the cause of the exception
     */
    public InfrastructureRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}