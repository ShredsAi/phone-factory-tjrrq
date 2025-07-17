package ai.shreds.infrastructure.exceptions;

/**
 * Exception thrown when an external service call fails.
 * Used to encapsulate errors from external service clients (Supplier Service, Auth Service, etc.)
 */
public class InfrastructureExternalServiceException extends RuntimeException {

    private final String serviceName;
    private final String errorCode;

    /**
     * Constructor with error message
     * @param message the error message
     */
    public InfrastructureExternalServiceException(String message) {
        super(message);
        this.serviceName = "unknown";
        this.errorCode = "EXTERNAL_SERVICE_ERROR";
    }

    /**
     * Constructor with error message and cause
     * @param message the error message
     * @param cause the underlying exception cause
     */
    public InfrastructureExternalServiceException(String message, Throwable cause) {
        super(message, cause);
        this.serviceName = "unknown";
        this.errorCode = "EXTERNAL_SERVICE_ERROR";
    }

    /**
     * Full constructor with service name and error code
     * @param message the error message
     * @param cause the underlying exception cause
     * @param serviceName the name of the external service
     * @param errorCode the error code
     */
    public InfrastructureExternalServiceException(String message, Throwable cause, String serviceName, String errorCode) {
        super(message, cause);
        this.serviceName = serviceName;
        this.errorCode = errorCode;
    }

    /**
     * Get the name of the external service that caused the exception
     * @return the service name
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Get the error code
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
}
