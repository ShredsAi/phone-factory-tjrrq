package ai.shreds.application.exceptions;

/**
 * Exception thrown when an approval decision is invalid or unauthorized.
 */
public class ApplicationInvalidApprovalException extends RuntimeException {

    public ApplicationInvalidApprovalException(String message) {
        super(message);
    }

    public ApplicationInvalidApprovalException(String message, Throwable cause) {
        super(message, cause);
    }
}