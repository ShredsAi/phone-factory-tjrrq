package ai.shreds.adapter.primary;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;

import ai.shreds.application.ports.ApplicationKafkaOutputPort;
import ai.shreds.shared.dtos.SharedPurchaseOrderTransmittedEventDTO;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletableFuture;
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
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(purchaseOrderTopic, event.getOrderId(), event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully published PO transmitted event for order: {} to partition: {}",
                        event.getOrderId(), result.getRecordMetadata().partition());
                } else {
                    log.error("Failed to publish PO transmitted event for order: {}", event.getOrderId(), ex);
                }
            });

            // Optionally wait for the result with timeout to ensure delivery within a time frame
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
