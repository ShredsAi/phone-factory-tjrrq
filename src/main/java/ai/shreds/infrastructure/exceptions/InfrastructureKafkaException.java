package ai.shreds.infrastructure.exceptions;

/**
 * Exception thrown when Kafka operations in the infrastructure layer fail.
 * This includes message publishing errors, consumer errors, and serialization issues.
 */
public class InfrastructureKafkaException extends RuntimeException {

    private final String topic;
    
    /**
     * Constructs a new Kafka exception with the specified error message.
     *
     * @param message the error message
     */
    public InfrastructureKafkaException(String message) {
        super(message);
        this.topic = "unknown";
    }

    /**
     * Constructs a new Kafka exception with the specified error message and cause.
     *
     * @param message the error message
     * @param cause the cause of the exception
     */
    public InfrastructureKafkaException(String message, Throwable cause) {
        super(message, cause);
        this.topic = "unknown";
    }
    
    /**
     * Constructs a new Kafka exception with the specified error message, cause, and topic.
     *
     * @param message the error message
     * @param cause the cause of the exception
     * @param topic the Kafka topic where the error occurred
     */
    public InfrastructureKafkaException(String message, Throwable cause, String topic) {
        super(message, cause);
        this.topic = topic;
    }
    
    /**
     * Get the Kafka topic associated with this exception.
     * 
     * @return the topic name or "unknown" if not specified
     */
    public String getTopic() {
        return topic;
    }
}