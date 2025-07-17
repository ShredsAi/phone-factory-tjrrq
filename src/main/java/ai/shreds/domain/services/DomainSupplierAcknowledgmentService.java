package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainPurchaseOrderAggregate;
import ai.shreds.domain.exceptions.DomainInvalidOrderStateException;
import ai.shreds.domain.ports.DomainInputPortProcessAcknowledgment;
import ai.shreds.domain.ports.DomainOutputPortPurchaseOrderRepository;
import ai.shreds.domain.ports.DomainOutputPortNotification;
import ai.shreds.domain.value_objects.DomainOrderStatus;
import ai.shreds.shared.dtos.SharedSupplierAcknowledgmentRequestDTO;
import ai.shreds.shared.dtos.SharedLineItemConfirmationDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Domain service for processing supplier acknowledgments of purchase orders.
 * Implements the DomainInputPortProcessAcknowledgment interface.
 */
public class DomainSupplierAcknowledgmentService implements DomainInputPortProcessAcknowledgment {

    private final DomainOutputPortPurchaseOrderRepository purchaseOrderRepository;
    private final DomainOutputPortNotification notificationPort;

    /**
     * Creates a new DomainSupplierAcknowledgmentService with the required dependencies.
     *
     * @param purchaseOrderRepository repository for purchase order operations
     * @param notificationPort notification service for sending alerts
     */
    public DomainSupplierAcknowledgmentService(DomainOutputPortPurchaseOrderRepository purchaseOrderRepository,
                                               DomainOutputPortNotification notificationPort) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.notificationPort = notificationPort;
    }

    @Override
    public void processAcknowledgment(String orderId, SharedSupplierAcknowledgmentRequestDTO acknowledgment) {
        // Retrieve the purchase order
        DomainPurchaseOrderAggregate order = purchaseOrderRepository.findById(orderId);
        if (order == null) {
            throw new DomainInvalidOrderStateException("Purchase order not found: " + orderId);
        }

        // Validate that the order is in the correct state for acknowledgment
        if (order.getStatus() != DomainOrderStatus.SENT) {
            throw new DomainInvalidOrderStateException(
                order.getStatus().name(), 
                "processAcknowledgment"
            );
        }

        // Validate that the supplier ID matches
        if (!order.getSupplierId().getValue().equals(acknowledgment.getSupplierId())) {
            throw new IllegalArgumentException(
                "Supplier ID mismatch. Expected: " + order.getSupplierId().getValue() + 
                ", but got: " + acknowledgment.getSupplierId()
            );
        }

        // Parse confirmed delivery date
        LocalDateTime confirmedDeliveryDate = null;
        if (acknowledgment.getConfirmedDeliveryDate() != null && 
            !acknowledgment.getConfirmedDeliveryDate().isEmpty()) {
            confirmedDeliveryDate = LocalDateTime.parse(acknowledgment.getConfirmedDeliveryDate());
        }

        // Process the acknowledgment based on status
        switch (acknowledgment.getStatus().toUpperCase()) {
            case "ACCEPTED":
            case "CONFIRMED":
                // Acknowledge the order with delivery date
                order.acknowledge(
                    acknowledgment.getSupplierId(), 
                    confirmedDeliveryDate != null ? confirmedDeliveryDate : LocalDateTime.now().plusDays(7)
                );
                
                // Process line item confirmations if provided
                if (acknowledgment.getLineItemConfirmations() != null) {
                    processLineItemConfirmations(order, acknowledgment.getLineItemConfirmations());
                }
                
                // Send notification about successful acknowledgment
                notificationPort.sendOrderStatusNotification(
                    order.getSupplierId().getValue(),
                    orderId,
                    "ACKNOWLEDGED"
                );
                break;
                
            case "REJECTED":
            case "DECLINED":
                // Reject the order and notify stakeholders
                order.reject("Order rejected by supplier: " + acknowledgment.getSupplierId());
                
                notificationPort.sendOrderStatusNotification(
                    order.getSupplierId().getValue(),
                    orderId,
                    "REJECTED_BY_SUPPLIER"
                );
                break;
                
            case "PARTIAL":
                // Handle partial acknowledgment - acknowledge but flag for attention
                order.acknowledge(acknowledgment.getSupplierId(), confirmedDeliveryDate);
                
                if (acknowledgment.getLineItemConfirmations() != null) {
                    processLineItemConfirmations(order, acknowledgment.getLineItemConfirmations());
                }
                
                notificationPort.sendOrderStatusNotification(
                    order.getSupplierId().getValue(),
                    orderId,
                    "PARTIALLY_ACKNOWLEDGED"
                );
                break;
                
            default:
                throw new IllegalArgumentException(
                    "Invalid acknowledgment status: " + acknowledgment.getStatus()
                );
        }

        // Update the order in the repository
        purchaseOrderRepository.update(order);
    }

    /**
     * Processes line item confirmations from the supplier.
     * In a more complex implementation, this would validate quantities and handle partial confirmations.
     *
     * @param order the purchase order being acknowledged
     * @param confirmations the list of line item confirmations
     */
    private void processLineItemConfirmations(DomainPurchaseOrderAggregate order, 
                                            List<SharedLineItemConfirmationDTO> confirmations) {
        // Basic validation - in a real implementation, this would be more sophisticated
        for (SharedLineItemConfirmationDTO confirmation : confirmations) {
            // Find the corresponding line item in the order
            boolean lineItemFound = order.getLineItems().stream()
                .anyMatch(item -> item.getLineItemId().toString().equals(confirmation.getLineItemId()));
            
            if (!lineItemFound) {
                throw new IllegalArgumentException(
                    "Line item not found in order: " + confirmation.getLineItemId()
                );
            }
            
            // Validate confirmed quantity is positive
            if (confirmation.getConfirmedQuantity() == null || 
                confirmation.getConfirmedQuantity().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                    "Invalid confirmed quantity for line item: " + confirmation.getLineItemId()
                );
            }
            
            // In a real implementation, we might:
            // - Update line item quantities
            // - Handle partial confirmations
            // - Recalculate order totals
            // - Flag items with delivery date changes
        }
    }
}