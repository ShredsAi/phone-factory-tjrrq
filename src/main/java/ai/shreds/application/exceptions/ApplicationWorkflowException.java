package ai.shreds.application.exceptions;

/**
 * Exception thrown for workflow-related errors in the application layer.
 */
public class ApplicationWorkflowException extends RuntimeException {

    public ApplicationWorkflowException(String message) {
        super(message);
    }

    public ApplicationWorkflowException(String message, Throwable cause) {
        super(message, cause);
    }
}