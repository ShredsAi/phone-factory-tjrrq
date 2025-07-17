package ai.shreds.adapter.primary;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import ai.shreds.application.ports.ApplicationAcknowledgmentInputPort;
import ai.shreds.shared.dtos.SharedSupplierAcknowledgmentRequestDTO;
import ai.shreds.adapter.exceptions.AdapterValidationException;

import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for receiving supplier acknowledgment webhooks.
 * Handles acknowledgments from suppliers confirming receipt of purchase orders.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/procurement/supplier-acknowledgments")
@Validated
public class AdapterSupplierAcknowledgmentController {

    private final ApplicationAcknowledgmentInputPort applicationAcknowledgmentService;

    public AdapterSupplierAcknowledgmentController(ApplicationAcknowledgmentInputPort applicationAcknowledgmentService) {
        this.applicationAcknowledgmentService = applicationAcknowledgmentService;
    }

    /**
     * Receives and processes acknowledgments from suppliers after they receive purchase orders.
     * 
     * @param request The acknowledgment data from the supplier
     * @return ResponseEntity with HTTP 200 OK to acknowledge receipt
     */
    @PostMapping
    public ResponseEntity<Void> receiveAcknowledgment(@Valid @RequestBody SharedSupplierAcknowledgmentRequestDTO request) {
        log.info("Received supplier acknowledgment for purchase order: {}, supplier order ID: {}", 
                request.getPurchaseOrderId(), request.getSupplierOrderId());
        
        validateAcknowledgmentRequest(request);
        
        try {
            applicationAcknowledgmentService.processAcknowledgment(request);
            log.info("Successfully processed supplier acknowledgment for purchase order: {}", request.getPurchaseOrderId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error processing supplier acknowledgment for purchase order: {}", request.getPurchaseOrderId(), e);
            throw e; // Let the global exception handler deal with it
        }
    }

    /**
     * Validates the supplier acknowledgment request for completeness and business rules.
     * 
     * @param request The acknowledgment request
     * @throws AdapterValidationException if validation fails
     */
    private void validateAcknowledgmentRequest(SharedSupplierAcknowledgmentRequestDTO request) {
        if (request == null) {
            throw new AdapterValidationException("Acknowledgment request must not be null");
        }
        
        if (request.getSupplierOrderId() == null || request.getSupplierOrderId().trim().isEmpty()) {
            throw new AdapterValidationException("Supplier order ID is required");
        }
        
        if (request.getPurchaseOrderId() == null || request.getPurchaseOrderId().trim().isEmpty()) {
            throw new AdapterValidationException("Purchase order ID is required");
        }
        
        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            throw new AdapterValidationException("Status is required");
        }
        
        // Validate status values
        String status = request.getStatus().toUpperCase();
        if (!status.equals("ACCEPTED") && !status.equals("PARTIALLY_ACCEPTED") && 
            !status.equals("REJECTED") && !status.equals("PENDING")) {
            throw new AdapterValidationException(
                    "Status must be one of: ACCEPTED, PARTIALLY_ACCEPTED, REJECTED, PENDING");
        }
        
        // Confirm delivery date should be provided if the order is accepted or partially accepted
        if ((status.equals("ACCEPTED") || status.equals("PARTIALLY_ACCEPTED")) && 
            (request.getConfirmedDeliveryDate() == null || request.getConfirmedDeliveryDate().trim().isEmpty())) {
            throw new AdapterValidationException("Confirmed delivery date is required for ACCEPTED or PARTIALLY_ACCEPTED orders");
        }
        
        // Line item confirmations should be provided if order is accepted or partially accepted
        if ((status.equals("ACCEPTED") || status.equals("PARTIALLY_ACCEPTED")) && 
            (request.getLineItemConfirmations() == null || request.getLineItemConfirmations().isEmpty())) {
            throw new AdapterValidationException("Line item confirmations are required for ACCEPTED or PARTIALLY_ACCEPTED orders");
        }
    }
}