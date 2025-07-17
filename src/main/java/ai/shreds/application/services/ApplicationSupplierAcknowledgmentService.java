package ai.shreds.application.services;

import ai.shreds.application.ports.ApplicationAcknowledgmentInputPort;
import ai.shreds.domain.ports.DomainInputPortProcessAcknowledgment;
import ai.shreds.shared.dtos.SharedPurchaseOrderTransmittedEventDTO;
import ai.shreds.shared.dtos.SharedSupplierAcknowledgmentRequestDTO;
import ai.shreds.application.exceptions.ApplicationOrderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for processing supplier acknowledgments for purchase orders.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationSupplierAcknowledgmentService implements ApplicationAcknowledgmentInputPort {

    private final DomainInputPortProcessAcknowledgment domainAcknowledgmentPort;
    private final ApplicationEventService eventService;
    
    @Override
    @Transactional
    public void processAcknowledgment(SharedSupplierAcknowledgmentRequestDTO acknowledgmentData) {
        String purchaseOrderId = acknowledgmentData.getPurchaseOrderId();
        log.info("Processing supplier acknowledgment for purchase order: {}, status: {}", 
                purchaseOrderId, acknowledgmentData.getStatus());
        
        try {
            // Basic validation
            validateAcknowledgmentData(acknowledgmentData);
            
            // Process the acknowledgment through domain layer
            domainAcknowledgmentPort.processAcknowledgment(purchaseOrderId, acknowledgmentData);
            
            // Publish events as needed
            // Note: In a real implementation, we would get the updated order data from the domain layer
            // and map it to the event DTO
            if ("ACCEPTED".equalsIgnoreCase(acknowledgmentData.getStatus())) {
                publishAcknowledgmentEvents(purchaseOrderId, acknowledgmentData);
            }
            
            log.info("Supplier acknowledgment processed successfully for purchase order: {}", purchaseOrderId);
            
        } catch (ApplicationOrderNotFoundException e) {
            log.error("Order not found for acknowledgment: {}", purchaseOrderId, e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to process supplier acknowledgment for purchase order: {}", purchaseOrderId, e);
            throw new RuntimeException("Failed to process supplier acknowledgment: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validates the acknowledgment data.
     * 
     * @param acknowledgmentData The acknowledgment data to validate
     * @throws IllegalArgumentException If the data is invalid
     */
    private void validateAcknowledgmentData(SharedSupplierAcknowledgmentRequestDTO acknowledgmentData) {
        if (acknowledgmentData == null) {
            throw new IllegalArgumentException("Acknowledgment data cannot be null");
        }
        
        if (acknowledgmentData.getPurchaseOrderId() == null || acknowledgmentData.getPurchaseOrderId().trim().isEmpty()) {
            throw new IllegalArgumentException("Purchase order ID is required");
        }
        
        if (acknowledgmentData.getSupplierOrderId() == null || acknowledgmentData.getSupplierOrderId().trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier order ID is required");
        }
        
        if (acknowledgmentData.getStatus() == null || acknowledgmentData.getStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("Status is required");
        }
        
        // Validate status is one of the expected values
        String status = acknowledgmentData.getStatus().toUpperCase();
        if (!"ACCEPTED".equals(status) && !"PARTIALLY_ACCEPTED".equals(status) && 
                !"REJECTED".equals(status) && !"PENDING".equals(status)) {
            throw new IllegalArgumentException("Invalid status: " + acknowledgmentData.getStatus());
        }
        
        // If accepted, confirmedDeliveryDate is required
        if ("ACCEPTED".equals(status) && 
                (acknowledgmentData.getConfirmedDeliveryDate() == null || 
                 acknowledgmentData.getConfirmedDeliveryDate().trim().isEmpty())) {
            throw new IllegalArgumentException("Confirmed delivery date is required for accepted orders");
        }
        
        // If any line items are provided, validate them
        if (acknowledgmentData.getLineItemConfirmations() != null && 
                !acknowledgmentData.getLineItemConfirmations().isEmpty()) {
            
            acknowledgmentData.getLineItemConfirmations().forEach(lineItem -> {
                if (lineItem.getLineItemId() == null || lineItem.getLineItemId().trim().isEmpty()) {
                    throw new IllegalArgumentException("Line item ID is required for all line item confirmations");
                }
                
                if (lineItem.getStatus() == null || lineItem.getStatus().trim().isEmpty()) {
                    throw new IllegalArgumentException("Status is required for all line item confirmations");
                }
            });
        }
    }
    
    /**
     * Publishes events for accepted acknowledgments.
     * 
     * @param purchaseOrderId The purchase order ID
     * @param acknowledgmentData The acknowledgment data
     */
    private void publishAcknowledgmentEvents(String purchaseOrderId, 
            SharedSupplierAcknowledgmentRequestDTO acknowledgmentData) {
        
        try {
            // This would normally map from a repository-retrieved order, here we create minimal data
            // In a real implementation, we'd get the full order data from the repository
            SharedPurchaseOrderTransmittedEventDTO event = new SharedPurchaseOrderTransmittedEventDTO();
            event.setOrderId(purchaseOrderId);
            event.setTransmissionDate(acknowledgmentData.getConfirmedDeliveryDate());
            
            // Publish the event
            eventService.publishPurchaseOrderTransmitted(event);
            
            log.debug("Published supplier acknowledgment event for purchase order: {}", purchaseOrderId);
            
        } catch (Exception e) {
            // Log but don't fail the process if event publishing fails
            log.error("Failed to publish events for supplier acknowledgment of purchase order: {}", 
                    purchaseOrderId, e);
        }
    }
}