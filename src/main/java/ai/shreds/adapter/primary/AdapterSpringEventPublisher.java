package ai.shreds.adapter.primary;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;

import ai.shreds.application.ports.ApplicationEventOutputPort;
import ai.shreds.shared.dtos.SharedPurchaseOrderCreatedEventDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring event publisher adapter for publishing purchase order created events.
 * Implements the application's event output port for internal event publishing.
 * 
 * These events will be consumed by other components within the same application context,
 * such as notification services or other internal processing components.
 */
@Slf4j
@Component
public class AdapterSpringEventPublisher implements ApplicationEventOutputPort {

    private final ApplicationEventPublisher applicationEventPublisher;

    public AdapterSpringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Publishes a purchase order created event to the Spring application context.
     * Implements retry capability for transient errors with exponential backoff.
     * 
     * @param event The purchase order created event to publish
     * @throws RuntimeException if publishing fails after all retries
     */
    @Override
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 500, multiplier = 2))
    public void publishPurchaseOrderCreated(SharedPurchaseOrderCreatedEventDTO event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        if (event.getOrderId() == null || event.getOrderId().trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }
        
        if (event.getEventType() == null || !event.getEventType().equals("PurchaseOrderCreated")) {
            throw new IllegalArgumentException("Event type must be 'PurchaseOrderCreated'");
        }

        try {
            log.info("Publishing purchase order created event for order: {}", event.getOrderId());
            applicationEventPublisher.publishEvent(event);
            log.info("Successfully published purchase order created event for order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Error publishing purchase order created event for order: {}", event.getOrderId(), e);
            throw new RuntimeException("Failed to publish purchase order created event", e);
        }
    }
}