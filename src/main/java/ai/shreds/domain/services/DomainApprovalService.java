package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainApprovalWorkflowEntity;
import ai.shreds.domain.entities.DomainPurchaseOrderAggregate;
import ai.shreds.domain.exceptions.DomainInvalidOrderStateException;
import ai.shreds.domain.exceptions.DomainWorkflowViolationException;
import ai.shreds.domain.ports.DomainInputPortProcessApproval;
import ai.shreds.domain.ports.DomainOutputPortAuthorization;
import ai.shreds.domain.ports.DomainOutputPortNotification;
import ai.shreds.domain.ports.DomainOutputPortPurchaseOrderRepository;
import ai.shreds.domain.ports.DomainOutputPortWorkflowRepository;
import ai.shreds.domain.value_objects.DomainApprovalDecision;
import ai.shreds.domain.value_objects.DomainApprovalLevel;
import ai.shreds.domain.value_objects.DomainWorkflowStatus;
import ai.shreds.shared.dtos.SharedApprovalRequestDTO;

/**
 * Domain service for processing purchase order approvals.
 * Implements the DomainInputPortProcessApproval interface.
 */
public class DomainApprovalService implements DomainInputPortProcessApproval {

    private final DomainOutputPortWorkflowRepository workflowRepository;
    private final DomainOutputPortPurchaseOrderRepository purchaseOrderRepository;
    private final DomainOutputPortAuthorization authorizationPort;
    private final DomainOutputPortNotification notificationPort;

    /**
     * Creates a new DomainApprovalService with the required dependencies.
     *
     * @param workflowRepository repository for approval workflows
     * @param purchaseOrderRepository repository for purchase orders
     * @param authorizationPort service for validating approver authority
     * @param notificationPort service for sending notifications
     */
    public DomainApprovalService(DomainOutputPortWorkflowRepository workflowRepository,
                                 DomainOutputPortPurchaseOrderRepository purchaseOrderRepository,
                                 DomainOutputPortAuthorization authorizationPort,
                                 DomainOutputPortNotification notificationPort) {
        this.workflowRepository = workflowRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.authorizationPort = authorizationPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public DomainWorkflowStatus processApproval(String orderId, SharedApprovalRequestDTO approvalData) {
        // Retrieve the purchase order
        DomainPurchaseOrderAggregate order = purchaseOrderRepository.findById(orderId);
        if (order == null) {
            throw new DomainWorkflowViolationException("Purchase order not found: " + orderId);
        }
        
        // Check if order is in correct state for approval
        if (order.getStatus() != ai.shreds.domain.value_objects.DomainOrderStatus.PENDING_APPROVAL) {
            throw new DomainInvalidOrderStateException(order.getStatus().name(), "processApproval");
        }
        
        // Retrieve the approval workflow
        DomainApprovalWorkflowEntity workflow = workflowRepository.findByPurchaseOrderId(orderId);
        if (workflow == null) {
            throw new DomainWorkflowViolationException("Approval workflow not found for order: " + orderId);
        }
        
        // Validate that the approver has authority to make this decision
        validateApproverAuthority(approvalData.getApproverId(), order.getTotalAmount(), workflow.getCurrentApprovalLevel().name());
        
        // Convert decision string to enum
        DomainApprovalDecision decision = convertDecision(approvalData.getDecision());
        
        // Advance the workflow with the approval decision
        workflow.advanceWorkflow(decision, approvalData.getApproverId(), approvalData.getComments());
        
        // Update the workflow in repository
        workflow = workflowRepository.update(workflow);
        
        // If workflow is complete, update the purchase order status
        if (workflow.isComplete()) {
            if (workflow.getWorkflowStatus() == DomainWorkflowStatus.APPROVED) {
                order.approve();
                notificationPort.sendOrderStatusNotification(order.getSupplierId().getValue(), 
                    orderId, "APPROVED");
            } else if (workflow.getWorkflowStatus() == DomainWorkflowStatus.REJECTED) {
                order.reject(approvalData.getComments());
                notificationPort.sendOrderStatusNotification(order.getSupplierId().getValue(), 
                    orderId, "REJECTED");
            }
            purchaseOrderRepository.update(order);
        } else if (workflow.getWorkflowStatus() == DomainWorkflowStatus.IN_PROGRESS) {
            // If workflow is still in progress, notify the next approver
            String nextApproverId = workflow.getCurrentApproverId();
            if (nextApproverId != null) {
                notificationPort.sendApprovalNotification(nextApproverId, orderId, order.getTotalAmount());
            }
        }
        
        return workflow.getWorkflowStatus();
    }
    
    /**
     * Validates that the approver has authority to make a decision at the current approval level.
     * 
     * @param approverId the ID of the approver
     * @param orderValue the monetary value of the order
     * @param currentLevel the current approval level required
     * @throws DomainWorkflowViolationException if the approver lacks authority
     */
    private void validateApproverAuthority(String approverId, ai.shreds.domain.value_objects.DomainMonetaryAmount orderValue, String currentLevel) {
        boolean isAuthorized = authorizationPort.validateApprovalAuthority(approverId, orderValue);
        if (!isAuthorized) {
            throw new DomainWorkflowViolationException(
                "Approver " + approverId + " is not authorized to approve orders of value " + orderValue);
        }
        
        // Verify that the approver's level matches or exceeds the current level required
        String approverLevel = authorizationPort.getUserApprovalLevel(approverId);
        if (!isApprovalLevelSufficient(approverLevel, currentLevel)) {
            throw new DomainWorkflowViolationException(
                "Approver level " + approverLevel + " is insufficient for current workflow level " + currentLevel);
        }
    }
    
    /**
     * Determines if an approver's level is sufficient for the current workflow level.
     * 
     * @param approverLevel the approver's level
     * @param requiredLevel the currently required level
     * @return true if the approver's level is sufficient
     */
    private boolean isApprovalLevelSufficient(String approverLevel, String requiredLevel) {
        // Convert string levels to enum values
        DomainApprovalLevel approverLevelEnum = DomainApprovalLevel.valueOf(approverLevel);
        DomainApprovalLevel requiredLevelEnum = DomainApprovalLevel.valueOf(requiredLevel);
        
        // Compare the ordinal values - higher ordinal means higher authority
        return approverLevelEnum.ordinal() >= requiredLevelEnum.ordinal();
    }
    
    /**
     * Converts a decision string from the DTO to a domain enum value.
     * 
     * @param decisionStr the decision string
     * @return the corresponding domain decision enum
     * @throws IllegalArgumentException if the decision string is invalid
     */
    private DomainApprovalDecision convertDecision(String decisionStr) {
        try {
            return DomainApprovalDecision.valueOf(decisionStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid decision: " + decisionStr, e);
        }
    }
}