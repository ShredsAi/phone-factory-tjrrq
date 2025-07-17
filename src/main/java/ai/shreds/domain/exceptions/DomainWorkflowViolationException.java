package ai.shreds.domain.exceptions;

/**
 * Exception thrown when an approval workflow rule is violated.
 * This includes cases like unauthorized approvers, incorrect workflow state transitions,
 * or attempts to bypass required approval levels.
 */
public class DomainWorkflowViolationException extends RuntimeException {

    /**
     * Creates a new DomainWorkflowViolationException with a message.
     *
     * @param message the exception message describing the workflow violation
     */
    public DomainWorkflowViolationException(String message) {
        super(message);
    }

    /**
     * Creates a new DomainWorkflowViolationException with a message and cause.
     *
     * @param message the exception message
     * @param cause the underlying cause of the exception
     */
    public DomainWorkflowViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}