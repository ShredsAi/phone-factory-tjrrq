package ai.shreds.application.services;

import ai.shreds.application.ports.ApplicationModificationInputPort;
import ai.shreds.domain.ports.DomainInputPortModifyOrder;
import ai.shreds.shared.dtos.SharedModificationRequestDTO;
import ai.shreds.shared.dtos.SharedModificationResponseDTO;
import ai.shreds.shared.dtos.SharedModificationResultDTO;
import ai.shreds.shared.dtos.SharedMonetaryAmountDTO;
import ai.shreds.application.exceptions.ApplicationOrderNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service for handling purchase order modifications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationOrderModificationService implements ApplicationModificationInputPort {

    private final DomainInputPortModifyOrder domainModificationPort;
    private final ApplicationWorkflowCoordinator workflowCoordinator;
    private final ApplicationPriceValidator priceValidator;
    
    @Override
    @Transactional
    public SharedModificationResponseDTO modifyOrder(String orderId, SharedModificationRequestDTO modificationData) {
        log.info("Processing modification request for order: {}, modification type: {}", 
                orderId, modificationData.getModificationType());
        
        try {
            // Validate the modification request
            validateModification(orderId, modificationData);
            
            // Process modification through domain layer
            SharedModificationResultDTO result = domainModificationPort.modifyOrder(orderId, modificationData);
            
            // Create response
            SharedModificationResponseDTO response = new SharedModificationResponseDTO();
            response.setStatus(result.isSuccess() ? "MODIFIED" : "FAILED");
            response.setOrderId(orderId);
            response.setRequiresReapproval(result.isRequiresReapproval());
            
            // If reapproval is required, restart approval workflow
            if (result.isRequiresReapproval()) {
                restartApprovalWorkflow(orderId, result.getNewTotalAmount());
            }
            
            log.info("Order modification processed successfully for order: {}, requires reapproval: {}", 
                    orderId, result.isRequiresReapproval());
            
            return response;
            
        } catch (ApplicationOrderNotFoundException e) {
            log.error("Order not found for modification: {}", orderId, e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to process modification for order: {}", orderId, e);
            throw new RuntimeException("Failed to modify order: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validates the modification request.
     * 
     * @param orderId The order ID
     * @param modificationData The modification request data
     * @throws IllegalArgumentException If the modification is invalid
     */
    private void validateModification(String orderId, SharedModificationRequestDTO modificationData) {
        if (modificationData == null) {
            throw new IllegalArgumentException("Modification data cannot be null");
        }
        
        if (modificationData.getModificationType() == null || modificationData.getModificationType().trim().isEmpty()) {
            throw new IllegalArgumentException("Modification type must be specified");
        }
        
        // Different validations based on modification type
        switch (modificationData.getModificationType().toUpperCase()) {
            case "QUANTITY_CHANGE":
                validateQuantityChange(modificationData);
                break;
            case "ITEM_REMOVAL":
                validateItemRemoval(modificationData);
                break;
            case "ITEM_ADDITION":
                validateItemAddition(modificationData);
                break;
            default:
                throw new IllegalArgumentException("Unknown modification type: " + 
                        modificationData.getModificationType());
        }
    }
    
    /**
     * Validates a quantity change modification.
     * 
     * @param modificationData The modification data
     */
    private void validateQuantityChange(SharedModificationRequestDTO modificationData) {
        if (modificationData.getLineItemId() == null || modificationData.getLineItemId().trim().isEmpty()) {
            throw new IllegalArgumentException("Line item ID is required for quantity changes");
        }
        
        if (modificationData.getNewQuantity() == null) {
            throw new IllegalArgumentException("New quantity must be specified");
        }
        
        if (modificationData.getNewQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("New quantity must be greater than zero");
        }
    }
    
    /**
     * Validates an item removal modification.
     * 
     * @param modificationData The modification data
     */
    private void validateItemRemoval(SharedModificationRequestDTO modificationData) {
        if (modificationData.getLineItemId() == null || modificationData.getLineItemId().trim().isEmpty()) {
            throw new IllegalArgumentException("Line item ID is required for item removal");
        }
        
        if (modificationData.getReason() == null || modificationData.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Reason is required for item removal");
        }
    }
    
    /**
     * Validates an item addition modification.
     * 
     * @param modificationData The modification data
     */
    private void validateItemAddition(SharedModificationRequestDTO modificationData) {
        // Note: For item addition, we would expect additional fields that are not in the current DTO
        // This would need to be expanded in a real implementation
        throw new IllegalArgumentException("Item addition is not supported in the current implementation");
    }
    
    /**
     * Restarts the approval workflow if the order value has changed significantly.
     * 
     * @param orderId The order ID
     * @param newTotalAmount The new total amount of the order
     */
    private void restartApprovalWorkflow(String orderId, SharedMonetaryAmountDTO newTotalAmount) {
        if (newTotalAmount == null || newTotalAmount.getAmount() == null) {
            log.warn("Cannot restart approval workflow: missing total amount for order {}", orderId);
            return;
        }
        
        try {
            // Initiate a new workflow based on the new amount
            String workflowId = workflowCoordinator.initiateWorkflow(orderId, newTotalAmount.getAmount());
            log.info("Restarted approval workflow for modified order: {}, new workflow ID: {}", 
                    orderId, workflowId);
        } catch (Exception e) {
            log.error("Failed to restart approval workflow for order: {}", orderId, e);
            // We don't throw here to avoid failing the whole modification process
        }
    }
}