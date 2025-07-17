package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainPurchaseOrderAggregate;
import ai.shreds.domain.exceptions.DomainInvalidOrderStateException;
import ai.shreds.domain.ports.DomainInputPortModifyOrder;
import ai.shreds.domain.ports.DomainOutputPortPurchaseOrderRepository;
import ai.shreds.domain.ports.DomainOutputPortSupplierValidation;
import ai.shreds.domain.ports.DomainOutputPortNotification;
import ai.shreds.domain.value_objects.DomainOrderStatus;
import ai.shreds.domain.value_objects.DomainOrderLineItem;
import ai.shreds.domain.value_objects.DomainMaterialId;
import ai.shreds.domain.value_objects.DomainMonetaryAmount;
import ai.shreds.shared.dtos.SharedModificationRequestDTO;
import ai.shreds.shared.dtos.SharedModificationResultDTO;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Domain service for modifying purchase orders.
 * Implements the DomainInputPortModifyOrder interface.
 */
public class DomainOrderModificationService implements DomainInputPortModifyOrder {

    private final DomainOutputPortPurchaseOrderRepository purchaseOrderRepository;
    private final DomainOutputPortSupplierValidation supplierValidation;
    private final DomainOutputPortNotification notificationPort;

    /**
     * Creates a new DomainOrderModificationService with the required dependencies.
     *
     * @param purchaseOrderRepository repository for purchase order operations
     * @param supplierValidation service for validating supplier capabilities
     * @param notificationPort notification service for sending alerts
     */
    public DomainOrderModificationService(DomainOutputPortPurchaseOrderRepository purchaseOrderRepository,
                                          DomainOutputPortSupplierValidation supplierValidation,
                                          DomainOutputPortNotification notificationPort) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.supplierValidation = supplierValidation;
        this.notificationPort = notificationPort;
    }

    @Override
    public SharedModificationResultDTO modifyOrder(String orderId, SharedModificationRequestDTO modifications) {
        // Retrieve the purchase order
        DomainPurchaseOrderAggregate order = purchaseOrderRepository.findById(orderId);
        if (order == null) {
            return createFailureResult(orderId, "Purchase order not found: " + orderId);
        }

        // Validate that the order can be modified
        if (!canModifyOrder(order)) {
            return createFailureResult(orderId, 
                "Order cannot be modified in current state: " + order.getStatus().name());
        }

        try {
            // Apply modifications based on type
            DomainMonetaryAmount newTotalAmount;
            boolean requiresReapproval = false;
            
            switch (modifications.getModificationType().toUpperCase()) {
                case "QUANTITY_CHANGE":
                    newTotalAmount = modifyLineItemQuantity(order, modifications);
                    requiresReapproval = determineIfReapprovalRequired(order, newTotalAmount);
                    break;
                    
                case "ADD_LINE_ITEM":
                    newTotalAmount = addLineItem(order, modifications);
                    requiresReapproval = determineIfReapprovalRequired(order, newTotalAmount);
                    break;
                    
                case "REMOVE_LINE_ITEM":
                    newTotalAmount = removeLineItem(order, modifications);
                    requiresReapproval = determineIfReapprovalRequired(order, newTotalAmount);
                    break;
                    
                default:
                    return createFailureResult(orderId, 
                        "Unsupported modification type: " + modifications.getModificationType());
            }

            // If changes require reapproval, update order status
            if (requiresReapproval && order.getStatus() == DomainOrderStatus.APPROVED) {
                // Reset to draft for reapproval
                order.submitForApproval();
                
                // Notify about reapproval requirement
                notificationPort.sendOrderStatusNotification(
                    order.getSupplierId().getValue(),
                    orderId,
                    "REQUIRES_REAPPROVAL"
                );
            }

            // Update the order in repository
            purchaseOrderRepository.update(order);

            // Return success result
            return createSuccessResult(orderId, newTotalAmount, requiresReapproval, 
                "Order modified successfully");

        } catch (Exception e) {
            return createFailureResult(orderId, "Modification failed: " + e.getMessage());
        }
    }

    /**
     * Modifies the quantity of a line item in the purchase order.
     * 
     * @param order the purchase order to modify
     * @param modifications the modification request
     * @return the new total amount after modification
     */
    private DomainMonetaryAmount modifyLineItemQuantity(DomainPurchaseOrderAggregate order, 
                                                        SharedModificationRequestDTO modifications) {
        UUID lineItemId = UUID.fromString(modifications.getLineItemId());
        BigDecimal newQuantity = modifications.getNewQuantity();
        
        // Validate new quantity
        if (newQuantity == null || newQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("New quantity must be positive");
        }
        
        // Find the line item
        DomainOrderLineItem targetLineItem = order.getLineItems().stream()
            .filter(item -> item.getLineItemId().equals(lineItemId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Line item not found: " + lineItemId));
        
        // Validate supplier capability for new quantity
        boolean canFulfill = supplierValidation.validateSupplierCapability(
            order.getSupplierId().getValue(),
            targetLineItem.getMaterialId().getValue(),
            newQuantity
        );
        
        if (!canFulfill) {
            throw new IllegalArgumentException(
                "Supplier cannot fulfill new quantity: " + newQuantity + 
                " for material: " + targetLineItem.getMaterialId().getValue()
            );
        }
        
        // Remove old line item and add new one with updated quantity
        order.removeLineItem(lineItemId);
        order.addLineItem(
            targetLineItem.getMaterialId(),
            newQuantity,
            targetLineItem.getUnitPrice()
        );
        
        return order.calculateTotalAmount();
    }

    /**
     * Adds a new line item to the purchase order.
     * 
     * @param order the purchase order to modify
     * @param modifications the modification request
     * @return the new total amount after addition
     */
    private DomainMonetaryAmount addLineItem(DomainPurchaseOrderAggregate order, 
                                            SharedModificationRequestDTO modifications) {
        // This is a simplified implementation
        // In a real system, we'd need material ID, unit price, etc. from the modification request
        throw new UnsupportedOperationException("Add line item functionality not yet implemented");
    }

    /**
     * Removes a line item from the purchase order.
     * 
     * @param order the purchase order to modify
     * @param modifications the modification request
     * @return the new total amount after removal
     */
    private DomainMonetaryAmount removeLineItem(DomainPurchaseOrderAggregate order, 
                                               SharedModificationRequestDTO modifications) {
        UUID lineItemId = UUID.fromString(modifications.getLineItemId());
        
        // Check if line item exists
        boolean exists = order.getLineItems().stream()
            .anyMatch(item -> item.getLineItemId().equals(lineItemId));
        
        if (!exists) {
            throw new IllegalArgumentException("Line item not found: " + lineItemId);
        }
        
        // Remove the line item
        order.removeLineItem(lineItemId);
        
        // Ensure order has at least one line item
        if (order.getLineItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot remove last line item from order");
        }
        
        return order.calculateTotalAmount();
    }

    /**
     * Determines if the order can be modified in its current state.
     * 
     * @param order the purchase order to check
     * @return true if the order can be modified, false otherwise
     */
    private boolean canModifyOrder(DomainPurchaseOrderAggregate order) {
        DomainOrderStatus status = order.getStatus();
        return status == DomainOrderStatus.DRAFT || 
               status == DomainOrderStatus.PENDING_APPROVAL || 
               status == DomainOrderStatus.APPROVED;
    }

    /**
     * Determines if the modification requires reapproval based on the change in total amount.
     * 
     * @param order the purchase order
     * @param newTotalAmount the new total amount after modification
     * @return true if reapproval is required, false otherwise
     */
    private boolean determineIfReapprovalRequired(DomainPurchaseOrderAggregate order, 
                                                 DomainMonetaryAmount newTotalAmount) {
        DomainMonetaryAmount oldTotalAmount = order.getTotalAmount();
        
        // Calculate percentage change
        if (oldTotalAmount.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            return true; // Any change from zero requires approval
        }
        
        BigDecimal changePercentage = newTotalAmount.getAmount()
            .subtract(oldTotalAmount.getAmount())
            .abs()
            .divide(oldTotalAmount.getAmount(), 4, java.math.RoundingMode.HALF_UP);
        
        // Require reapproval if change is more than 10% or exceeds $1000
        BigDecimal tenPercent = new BigDecimal("0.10");
        BigDecimal thousandDollars = new BigDecimal("1000.00");
        BigDecimal absoluteChange = newTotalAmount.getAmount().subtract(oldTotalAmount.getAmount()).abs();
        
        return changePercentage.compareTo(tenPercent) > 0 || 
               absoluteChange.compareTo(thousandDollars) > 0;
    }

    /**
     * Creates a success result DTO.
     * 
     * @param orderId the order ID
     * @param newTotalAmount the new total amount
     * @param requiresReapproval whether reapproval is required
     * @param message the success message
     * @return the success result DTO
     */
    private SharedModificationResultDTO createSuccessResult(String orderId, 
                                                           DomainMonetaryAmount newTotalAmount, 
                                                           boolean requiresReapproval, 
                                                           String message) {
        SharedModificationResultDTO result = new SharedModificationResultDTO();
        result.setSuccess(true);
        result.setOrderId(orderId);
        result.setNewTotalAmount(newTotalAmount.toSharedDTO());
        result.setRequiresReapproval(requiresReapproval);
        result.setMessage(message);
        return result;
    }

    /**
     * Creates a failure result DTO.
     * 
     * @param orderId the order ID
     * @param message the failure message
     * @return the failure result DTO
     */
    private SharedModificationResultDTO createFailureResult(String orderId, String message) {
        SharedModificationResultDTO result = new SharedModificationResultDTO();
        result.setSuccess(false);
        result.setOrderId(orderId);
        result.setRequiresReapproval(false);
        result.setMessage(message);
        return result;
    }
}