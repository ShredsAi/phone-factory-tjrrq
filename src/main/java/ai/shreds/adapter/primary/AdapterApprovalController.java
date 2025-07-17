package ai.shreds.adapter.primary;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import ai.shreds.application.ports.ApplicationApprovalInputPort;
import ai.shreds.shared.dtos.SharedApprovalRequestDTO;
import ai.shreds.shared.dtos.SharedApprovalResponseDTO;
import ai.shreds.adapter.exceptions.AdapterValidationException;

import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for processing purchase order approval decisions.
 * Handles approval workflow requests from authorized personnel.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/procurement/orders")
@Validated
public class AdapterApprovalController {

    private final ApplicationApprovalInputPort applicationApprovalService;

    public AdapterApprovalController(ApplicationApprovalInputPort applicationApprovalService) {
        this.applicationApprovalService = applicationApprovalService;
    }

    /**
     * Process an approval decision for a purchase order.
     * 
     * @param orderId The purchase order ID to approve/reject
     * @param request The approval request containing decision, approver info, and comments
     * @return ResponseEntity with approval response containing updated workflow status
     */
    @PostMapping("/{orderId}/approvals")
    public ResponseEntity<SharedApprovalResponseDTO> processApproval(
            @PathVariable("orderId") 
            @NotBlank(message = "Order ID cannot be blank")
            @Pattern(regexp = "^[A-Z0-9-]+$", message = "Order ID must contain only uppercase letters, numbers, and hyphens")
            String orderId,
            @Valid @RequestBody SharedApprovalRequestDTO request) {
        
        log.info("Processing approval request for order: {} by approver: {}", orderId, request.getApproverId());
        
        // Additional validation
        validateApprovalRequest(orderId, request);
        
        try {
            SharedApprovalResponseDTO response = applicationApprovalService.processApproval(orderId, request);
            log.info("Approval processed successfully for order: {} with status: {}", orderId, response.getStatus());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing approval for order: {}", orderId, e);
            throw e; // Let the global exception handler deal with it
        }
    }

    /**
     * Validates the approval request for completeness and business rules.
     * 
     * @param orderId The order ID
     * @param request The approval request
     * @throws AdapterValidationException if validation fails
     */
    private void validateApprovalRequest(String orderId, SharedApprovalRequestDTO request) {
        if (request == null) {
            throw new AdapterValidationException("Approval request must not be null");
        }
        
        if (request.getDecision() == null || request.getDecision().trim().isEmpty()) {
            throw new AdapterValidationException("Decision is required for approval request");
        }
        
        if (request.getApproverId() == null || request.getApproverId().trim().isEmpty()) {
            throw new AdapterValidationException("Approver ID is required for approval request");
        }
        
        if (request.getApprovalLevel() == null || request.getApprovalLevel().trim().isEmpty()) {
            throw new AdapterValidationException("Approval level is required for approval request");
        }
        
        // Validate decision values
        String decision = request.getDecision().toUpperCase();
        if (!decision.equals("APPROVED") && !decision.equals("REJECTED") && !decision.equals("ESCALATED")) {
            throw new AdapterValidationException("Decision must be one of: APPROVED, REJECTED, ESCALATED");
        }
        
        // Validate approval level values
        String approvalLevel = request.getApprovalLevel().toUpperCase();
        if (!approvalLevel.equals("SUPERVISOR") && !approvalLevel.equals("MANAGER") && !approvalLevel.equals("DIRECTOR")) {
            throw new AdapterValidationException("Approval level must be one of: SUPERVISOR, MANAGER, DIRECTOR");
        }
        
        // Validate approver ID format
        if (!request.getApproverId().matches("^[A-Z0-9-]+$")) {
            throw new AdapterValidationException("Approver ID must contain only uppercase letters, numbers, and hyphens");
        }
        
        // Comments are optional but if provided, should not be too long
        if (request.getComments() != null && request.getComments().length() > 1000) {
            throw new AdapterValidationException("Comments cannot exceed 1000 characters");
        }
    }
}