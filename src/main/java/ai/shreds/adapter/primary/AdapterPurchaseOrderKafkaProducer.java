package ai.shreds.adapter.primary;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;

import ai.shreds.application.ports.ApplicationKafkaOutputPort;
import ai.shreds.shared.dtos.SharedPurchaseOrderTransmittedEventDTO;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Kafka producer adapter for publishing purchase order transmitted events.
 * Implements the application's Kafka output port.
 */
@Slf4j
@Component
public class AdapterPurchaseOrderKafkaProducer implements ApplicationKafkaOutputPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String purchaseOrderTopic;
    private final long sendTimeoutMs;

    public AdapterPurchaseOrderKafkaProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${spring.kafka.producer.topic}") String purchaseOrderTopic,
            @Value("${spring.kafka.producer.send-timeout-ms:5000}") long sendTimeoutMs) {
        this.kafkaTemplate = kafkaTemplate;
        this.purchaseOrderTopic = purchaseOrderTopic;
        this.sendTimeoutMs = sendTimeoutMs;
    }

    /**
     * Publishes a purchase order transmitted event to Kafka.
     * Implements retry capability for transient errors with exponential backoff.
     * 
     * @param event The purchase order transmitted event to publish
     * @throws RuntimeException if publishing fails after all retries
     */
    @Override
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void publishPurchaseOrderTransmitted(SharedPurchaseOrderTransmittedEventDTO event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        if (event.getOrderId() == null || event.getOrderId().trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }
        
        log.info("Publishing purchase order transmitted event for order: {}", event.getOrderId());
        
        try {
            // Use the order ID as the message key to ensure all events for the same order go to the same partition
            ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(purchaseOrderTopic, event.getOrderId(), event);
            
            // Add callback for async handling
            future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
                @Override
                public void onSuccess(SendResult<String, Object> result) {
                    log.info("Successfully published PO transmitted event for order: {} to partition: {}", 
                            event.getOrderId(), result.getRecordMetadata().partition());
                }

                @Override
                public void onFailure(Throwable ex) {
                    log.error("Failed to publish PO transmitted event for order: {}", event.getOrderId(), ex);
                }
            });
            
            // Optionally wait for the result with timeout to ensure delivery within a time frame
            // This makes the method synchronous but with a bounded wait time
            try {
                future.get(sendTimeoutMs, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while publishing purchase order transmitted event", e);
            } catch (ExecutionException e) {
                throw new RuntimeException("Error publishing purchase order transmitted event", e.getCause());
            } catch (TimeoutException e) {
                throw new RuntimeException("Timeout publishing purchase order transmitted event", e);
            }
            
        } catch (Exception e) {
            log.error("Error publishing PO transmitted event for order: {}", event.getOrderId(), e);
            throw new RuntimeException("Failed to publish purchase order transmitted event", e);
        }
    }
}