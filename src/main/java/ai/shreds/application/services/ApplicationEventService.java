package ai.shreds.application.services;

import ai.shreds.application.ports.ApplicationEventOutputPort;
import ai.shreds.application.ports.ApplicationKafkaOutputPort;
import ai.shreds.shared.dtos.SharedPurchaseOrderCreatedEventDTO;
import ai.shreds.shared.dtos.SharedPurchaseOrderTransmittedEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for publishing events related to purchase orders.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationEventService {

    private final ApplicationEventOutputPort eventOutputPort;
    private final ApplicationKafkaOutputPort kafkaOutputPort;
    
    /**
     * Publishes a purchase order created event through the Spring application event publisher.
     * 
     * @param orderData The purchase order created event data
     */
    public void publishPurchaseOrderCreated(SharedPurchaseOrderCreatedEventDTO orderData) {
        log.info("Publishing PurchaseOrderCreated event for order: {}", orderData.getOrderId());
        
        try {
            eventOutputPort.publishPurchaseOrderCreated(orderData);
            log.debug("PurchaseOrderCreated event published successfully for order: {}", orderData.getOrderId());
        } catch (Exception e) {
            log.error("Failed to publish PurchaseOrderCreated event for order: {}", orderData.getOrderId(), e);
            // Depending on business requirements, we might want to retry, throw, or just log
            // For now we'll log and continue execution
        }
    }
    
    /**
     * Publishes a purchase order transmitted event to a Kafka topic.
     * 
     * @param orderData The purchase order transmitted event data
     */
    public void publishPurchaseOrderTransmitted(SharedPurchaseOrderTransmittedEventDTO orderData) {
        log.info("Publishing PurchaseOrderTransmitted event for order: {}", orderData.getOrderId());
        
        try {
            kafkaOutputPort.publishPurchaseOrderTransmitted(orderData);
            log.debug("PurchaseOrderTransmitted event published successfully to Kafka for order: {}", 
                    orderData.getOrderId());
        } catch (Exception e) {
            log.error("Failed to publish PurchaseOrderTransmitted event to Kafka for order: {}", 
                    orderData.getOrderId(), e);
            // We might want to implement a retry mechanism or compensating action here
            // depending on the business requirements
        }
    }
}