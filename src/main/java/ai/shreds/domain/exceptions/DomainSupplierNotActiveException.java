package ai.shreds.domain.exceptions;

/**
 * Exception thrown when attempting to create a purchase order with a supplier that is not active
 * or does not exist in the system.
 */
public class DomainSupplierNotActiveException extends RuntimeException {

    /**
     * Creates a new DomainSupplierNotActiveException with the supplier ID.
     *
     * @param supplierId the ID of the inactive supplier
     */
    public DomainSupplierNotActiveException(String supplierId) {
        super("Supplier is not active or does not exist: " + supplierId);
    }

    /**
     * Creates a new DomainSupplierNotActiveException with a custom message.
     *
     * @param message the exception message
     */
    public DomainSupplierNotActiveException(String message, Throwable cause) {
        super(message, cause);
    }
}