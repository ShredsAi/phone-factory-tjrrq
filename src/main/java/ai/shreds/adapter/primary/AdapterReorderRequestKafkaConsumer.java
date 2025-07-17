package ai.shreds.adapter.primary;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;

import ai.shreds.application.ports.ApplicationReorderInputPort;
import ai.shreds.shared.dtos.SharedReorderRequestEventDTO;
import ai.shreds.adapter.exceptions.AdapterValidationException;

import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;

/**
 * Kafka consumer adapter for processing reorder request events.
 * Consumes messages from the ReorderRequestInitiated Kafka topic and 
 * triggers the purchase order creation process.
 */
@Slf4j
@Component
public class AdapterReorderRequestKafkaConsumer {

    private final ApplicationReorderInputPort applicationReorderService;

    public AdapterReorderRequestKafkaConsumer(ApplicationReorderInputPort applicationReorderService) {
        this.applicationReorderService = applicationReorderService;
    }

    /**
     * Consumes reorder request events from Kafka and processes them.
     * Uses Spring Retry for automatic retry on failures with exponential backoff.
     * 
     * @param message The reorder request message payload
     * @param key The Kafka message key (eventId)
     * @param partition The Kafka partition
     * @param topic The Kafka topic name
     * @param timestamp The message timestamp
     */
    @KafkaListener(topics = "${spring.kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void consume(
            @Payload SharedReorderRequestEventDTO message,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) Long timestamp) {
        
        log.info("Received reorder request event from topic: {} [partition: {}, key: {}, time: {}]", 
                topic, partition, key, timestamp);
        
        try {
            validateReorderRequest(message);
            log.debug("Processing reorder request: materiald={}, quantity={}, priority={}", 
                    message.getMaterialId(), message.getReorderQuantity(), message.getPriority());
            
            applicationReorderService.processReorderRequest(message);
            
            log.info("Successfully processed reorder request event: {}", message.getEventId());
        } catch (AdapterValidationException ex) {
            log.error("Validation error processing reorder request: {}", message.getEventId(), ex);
            // Don't retry validation errors
            throw ex;
        } catch (Exception ex) {
            log.error("Error processing reorder request: {}", message.getEventId(), ex);
            // Will be retried by @Retryable annotation
            throw ex;
        }
    }
    
    /**
     * Validates the reorder request for completeness and business rules.
     * 
     * @param message The reorder request
     * @throws AdapterValidationException if validation fails
     */
    private void validateReorderRequest(SharedReorderRequestEventDTO message) {
        if (message == null) {
            throw new AdapterValidationException("Reorder request message cannot be null");
        }
        
        if (message.getEventId() == null || message.getEventId().trim().isEmpty()) {
            throw new AdapterValidationException("Event ID is required");
        }
        
        if (message.getMaterialId() == null || message.getMaterialId().trim().isEmpty()) {
            throw new AdapterValidationException("Material ID is required");
        }
        
        if (message.getReorderQuantity() == null) {
            throw new AdapterValidationException("Reorder quantity is required");
        }
        
        if (message.getReorderQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AdapterValidationException("Reorder quantity must be greater than zero");
        }
        
        if (message.getCurrentStockLevel() == null) {
            throw new AdapterValidationException("Current stock level is required");
        }
        
        if (message.getCurrentStockLevel().compareTo(BigDecimal.ZERO) < 0) {
            throw new AdapterValidationException("Current stock level cannot be negative");
        }
        
        if (message.getPriority() == null || message.getPriority().trim().isEmpty()) {
            throw new AdapterValidationException("Priority is required");
        }
        
        // Validate priority values
        String priority = message.getPriority().toUpperCase();
        if (!priority.equals("LOW") && !priority.equals("MEDIUM") && !priority.equals("HIGH") && !priority.equals("CRITICAL")) {
            throw new AdapterValidationException("Priority must be one of: LOW, MEDIUM, HIGH, CRITICAL");
        }
        
        // Warehouse location is required
        if (message.getWarehouseLocation() == null || message.getWarehouseLocation().trim().isEmpty()) {
            throw new AdapterValidationException("Warehouse location is required");
        }
    }
}