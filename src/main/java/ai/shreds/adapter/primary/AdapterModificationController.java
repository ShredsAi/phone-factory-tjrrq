package ai.shreds.adapter.primary;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import ai.shreds.application.ports.ApplicationModificationInputPort;
import ai.shreds.shared.dtos.SharedModificationRequestDTO;
import ai.shreds.shared.dtos.SharedModificationResponseDTO;
import ai.shreds.adapter.exceptions.AdapterValidationException;

import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;

/**
 * REST controller for processing purchase order modification requests.
 * Handles modifications to purchase orders before they are sent to suppliers.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/procurement/orders")
@Validated
public class AdapterModificationController {

    private final ApplicationModificationInputPort applicationModificationService;

    public AdapterModificationController(ApplicationModificationInputPort applicationModificationService) {
        this.applicationModificationService = applicationModificationService;
    }

    /**
     * Modify an existing purchase order before it's sent to the supplier.
     * 
     * @param orderId The purchase order ID to modify
     * @param request The modification request containing changes to apply
     * @return ResponseEntity with modification response including reapproval requirement
     */
    @PatchMapping("/{orderId}/modifications")
    public ResponseEntity<SharedModificationResponseDTO> modifyOrder(
            @PathVariable("orderId") 
            @NotBlank(message = "Order ID cannot be blank")
            @Pattern(regexp = "^[A-Z0-9-]+$", message = "Order ID must contain only uppercase letters, numbers, and hyphens")
            String orderId,
            @Valid @RequestBody SharedModificationRequestDTO request) {
        
        log.info("Processing modification request for order: {}, modification type: {}", 
                orderId, request.getModificationType());
        
        // Additional validation
        validateModificationRequest(orderId, request);
        
        try {
            SharedModificationResponseDTO response = applicationModificationService.modifyOrder(orderId, request);
            log.info("Modification processed successfully for order: {}, requires reapproval: {}", 
                    orderId, response.getRequiresReapproval());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing modification for order: {}", orderId, e);
            throw e; // Let the global exception handler deal with it
        }
    }

    /**
     * Validates the modification request for completeness and business rules.
     * 
     * @param orderId The order ID
     * @param request The modification request
     * @throws AdapterValidationException if validation fails
     */
    private void validateModificationRequest(String orderId, SharedModificationRequestDTO request) {
        if (request == null) {
            throw new AdapterValidationException("Modification request must not be null");
        }
        
        if (request.getModificationType() == null || request.getModificationType().trim().isEmpty()) {
            throw new AdapterValidationException("Modification type is required");
        }
        
        // Validate modification type values
        String modificationType = request.getModificationType().toUpperCase();
        if (!modificationType.equals("QUANTITY_CHANGE") && 
            !modificationType.equals("LINE_ITEM_REMOVAL") && 
            !modificationType.equals("DELIVERY_DATE_CHANGE")) {
            throw new AdapterValidationException(
                    "Modification type must be one of: QUANTITY_CHANGE, LINE_ITEM_REMOVAL, DELIVERY_DATE_CHANGE");
        }
        
        // Line item ID is required for all modification types
        if (request.getLineItemId() == null || request.getLineItemId().trim().isEmpty()) {
            throw new AdapterValidationException("Line item ID is required for modification");
        }
        
        // Validate line item ID format
        if (!request.getLineItemId().matches("^[A-Za-z0-9-]+$")) {
            throw new AdapterValidationException("Line item ID must contain only letters, numbers, and hyphens");
        }
        
        // Quantity is required for QUANTITY_CHANGE
        if (modificationType.equals("QUANTITY_CHANGE")) {
            if (request.getNewQuantity() == null) {
                throw new AdapterValidationException("New quantity is required for QUANTITY_CHANGE modification");
            }
            
            if (request.getNewQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new AdapterValidationException("New quantity must be greater than zero");
            }
        }
        
        // Reason is mandatory
        if (request.getReason() == null || request.getReason().trim().isEmpty()) {
            throw new AdapterValidationException("Reason for modification is required");
        }
        
        if (request.getReason().length() > 1000) {
            throw new AdapterValidationException("Reason cannot exceed 1000 characters");
        }
    }
}