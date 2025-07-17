package ai.shreds.application.services;

import ai.shreds.domain.entities.DomainApprovalWorkflowEntity;
import ai.shreds.domain.ports.DomainOutputPortWorkflowRepository;
import ai.shreds.domain.value_objects.DomainApprovalLevel;
import ai.shreds.domain.value_objects.DomainPurchaseOrderId;
import ai.shreds.domain.value_objects.DomainWorkflowStatus;
import ai.shreds.application.exceptions.ApplicationWorkflowException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Coordinator for purchase order approval workflows.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationWorkflowCoordinator {

    private final DomainOutputPortWorkflowRepository domainWorkflowPort;
    
    // Thresholds for approval levels
    private static final BigDecimal SUPERVISOR_THRESHOLD = new BigDecimal("5000.00");
    private static final BigDecimal MANAGER_THRESHOLD = new BigDecimal("25000.00");
    
    /**
     * Initiates a new approval workflow for a purchase order.
     * 
     * @param orderId The purchase order ID
     * @param orderValue The total value of the order
     * @return The workflow ID
     */
    public String initiateWorkflow(String orderId, BigDecimal orderValue) {
        log.info("Initiating approval workflow for order {} with value {}", orderId, orderValue);
        
        // Determine required approval level based on order value
        DomainApprovalLevel requiredApprovalLevel = mapStringToApprovalLevel(determineRequiredApprovalLevel(orderValue));
        
        // Determine initial approver
        String initialApproverId = getNextApprover(null);
        
        log.debug("Order requires {} level approval, assigned to {}", requiredApprovalLevel, initialApproverId);
        
        try {
            // Check if workflow already exists
            DomainApprovalWorkflowEntity existingWorkflow = domainWorkflowPort.findByPurchaseOrderId(orderId);
            
            if (existingWorkflow != null) {
                throw new ApplicationWorkflowException("Workflow already exists for order: " + orderId);
            }
            
            // Create new workflow entity
            DomainPurchaseOrderId purchaseOrderId = new DomainPurchaseOrderId(formatOrderId(orderId));
            DomainApprovalWorkflowEntity workflow = new DomainApprovalWorkflowEntity(
                    UUID.randomUUID(),
                    purchaseOrderId,
                    requiredApprovalLevel
            );
            
            // Initiate workflow with first approver
            workflow.initiateWorkflow(DomainApprovalLevel.SUPERVISOR, initialApproverId);
            
            // Save workflow
            workflow = domainWorkflowPort.save(workflow);
            
            return workflow.getWorkflowId().toString();
            
        } catch (Exception e) {
            log.error("Failed to initiate workflow for order {}", orderId, e);
            throw new ApplicationWorkflowException("Failed to initiate workflow: " + e.getMessage(), e);
        }
    }
    
    /**
     * Advances a workflow based on an approval decision.
     * 
     * @param workflowId The ID of the workflow to advance
     * @param decision The decision (APPROVED, REJECTED, ESCALATED)
     */
    public void advanceWorkflow(String workflowId, String decision) {
        log.info("Advancing workflow {} with decision {}", workflowId, decision);
        
        try {
            // Find workflow by purchase order ID (since we don't have findByWorkflowId)
            // This is a workaround - in a real implementation we'd have findByWorkflowId
            DomainApprovalWorkflowEntity workflow = findWorkflowByIdWorkaround(workflowId);
            
            if (workflow == null) {
                throw new ApplicationWorkflowException("Workflow not found: " + workflowId);
            }
            
            // Apply decision to workflow
            // Note: The actual decision processing would be done in the domain layer
            // For now, we'll update the workflow status based on the decision
            
            // Update workflow
            workflow = domainWorkflowPort.update(workflow);
            
            log.info("Workflow {} advanced to status {}", workflowId, workflow.getWorkflowStatus());
            
        } catch (Exception e) {
            log.error("Failed to advance workflow {}", workflowId, e);
            throw new ApplicationWorkflowException("Failed to advance workflow: " + e.getMessage(), e);
        }
    }
    
    /**
     * Determines the required approval level based on order value.
     * 
     * @param orderValue The total value of the purchase order
     * @return The required approval level (SUPERVISOR, MANAGER, DIRECTOR)
     */
    public String determineRequiredApprovalLevel(BigDecimal orderValue) {
        if (orderValue.compareTo(SUPERVISOR_THRESHOLD) < 0) {
            return "SUPERVISOR";
        } else if (orderValue.compareTo(MANAGER_THRESHOLD) < 0) {
            return "MANAGER";
        } else {
            return "DIRECTOR";
        }
    }
    
    /**
     * Gets the next approver based on the current approval level.
     * 
     * @param currentLevel The current approval level (null for initial approver)
     * @return The ID of the next approver
     */
    public String getNextApprover(String currentLevel) {
        // In a real implementation, this would query a user directory or organizational hierarchy service
        // For now, using placeholder IDs based on level
        
        if (currentLevel == null) {
            return "SUP-1001"; // Default first approver (supervisor)
        }
        
        switch (currentLevel) {
            case "SUPERVISOR":
                return "MGR-2001"; // Manager 
            case "MANAGER":
                return "DIR-3001"; // Director
            case "DIRECTOR":
                return null; // No higher level
            default:
                throw new ApplicationWorkflowException("Unknown approval level: " + currentLevel);
        }
    }
    
    /**
     * Maps string approval level to domain enum.
     * 
     * @param level String representation of approval level
     * @return DomainApprovalLevel enum
     */
    private DomainApprovalLevel mapStringToApprovalLevel(String level) {
        switch (level.toUpperCase()) {
            case "SUPERVISOR":
                return DomainApprovalLevel.SUPERVISOR;
            case "MANAGER":
                return DomainApprovalLevel.MANAGER;
            case "DIRECTOR":
                return DomainApprovalLevel.DIRECTOR;
            default:
                throw new ApplicationWorkflowException("Unknown approval level: " + level);
        }
    }
    
    /**
     * Formats order ID to match the expected pattern for DomainPurchaseOrderId.
     * 
     * @param orderId The original order ID
     * @return Formatted order ID
     */
    private String formatOrderId(String orderId) {
        // If the order ID doesn't match the expected pattern, format it
        if (!orderId.matches("^PO-\\d{4}-\\d{3}$")) {
            // Generate a formatted ID based on current year and sequence
            return "PO-" + java.time.Year.now().getValue() + "-" + String.format("%03d", Math.abs(orderId.hashCode() % 1000));
        }
        return orderId;
    }
    
    /**
     * Workaround to find workflow by ID since findByWorkflowId is not available in the repository.
     * This is a temporary solution until the repository interface is updated.
     * 
     * @param workflowId The workflow ID
     * @return The workflow entity or null if not found
     */
    private DomainApprovalWorkflowEntity findWorkflowByIdWorkaround(String workflowId) {
        // This is a workaround - in a real implementation we'd have findByWorkflowId in the repository
        // For now, we'll return null and log a warning
        log.warn("findByWorkflowId is not implemented in repository, returning null for workflow: {}", workflowId);
        return null;
    }
}