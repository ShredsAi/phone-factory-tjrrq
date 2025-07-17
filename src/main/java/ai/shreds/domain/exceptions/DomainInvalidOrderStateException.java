package ai.shreds.domain.exceptions;

/**
 * Exception thrown when an operation is attempted on a purchase order that is not in the correct state.
 * For example, trying to approve an order that is already approved or trying to modify a sent order.
 */
public class DomainInvalidOrderStateException extends RuntimeException {

    /**
     * Creates a new DomainInvalidOrderStateException with details about the invalid state transition.
     *
     * @param currentState the current state of the order
     * @param attemptedAction the action that was attempted
     */
    public DomainInvalidOrderStateException(String currentState, String attemptedAction) {
        super("Cannot perform action '" + attemptedAction + "' on order in state '" + currentState + "'");
    }

    /**
     * Creates a new DomainInvalidOrderStateException with a custom message.
     *
     * @param message the exception message
     */
    public DomainInvalidOrderStateException(String message) {
        super(message);
    }

    /**
     * Creates a new DomainInvalidOrderStateException with a custom message and cause.
     *
     * @param message the exception message
     * @param cause the underlying cause of the exception
     */
    public DomainInvalidOrderStateException(String message, Throwable cause) {
        super(message, cause);
    }
}