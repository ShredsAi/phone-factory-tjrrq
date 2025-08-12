package ai.shreds.application.services;

import ai.shreds.application.ports.ApplicationApprovalInputPort;
import ai.shreds.domain.ports.DomainInputPortProcessApproval;
import ai.shreds.domain.ports.DomainOutputPortAuthorization;
import ai.shreds.domain.ports.DomainOutputPortNotification;
import ai.shreds.domain.value_objects.DomainMonetaryAmount;
import ai.shreds.domain.value_objects.DomainWorkflowStatus;
import ai.shreds.shared.dtos.SharedApprovalRequestDTO;
import ai.shreds.shared.dtos.SharedApprovalResponseDTO;
import ai.shreds.application.exceptions.ApplicationInvalidApprovalException;
import ai.shreds.application.exceptions.ApplicationOrderNotFoundException;
import ai.shreds.application.exceptions.ApplicationWorkflowException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service for handling purchase order approval workflows.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationApprovalWorkflowService implements ApplicationApprovalInputPort {

    private final ApplicationWorkflowCoordinator workflowCoordinator;
    private final DomainInputPortProcessApproval domainApprovalPort;
    private final DomainOutputPortAuthorization domainAuthorizationPort;
    private final DomainOutputPortNotification domainNotificationPort;
    
    @Override
    @Transactional
    public SharedApprovalResponseDTO processApproval(String orderId, SharedApprovalRequestDTO approvalData) {
        log.info("Processing approval request for order: {} by approver: {}", 
                orderId, approvalData.getApproverId());
        
        try {
            // Validate approver authority
            validateApproverAuthority(approvalData.getApproverId(), 
                    approvalData.getApprovalLevel(), orderId);
            
            // Process approval through domain layer
            DomainWorkflowStatus workflowStatus = domainApprovalPort.processApproval(
                    orderId, approvalData);
            
            // If approval is granted and there's a next level, notify next approver
            if ("APPROVED".equals(approvalData.getDecision()) && 
                    !workflowStatus.name().equals("APPROVED") && 
                    !workflowStatus.name().equals("REJECTED")) {
                
                // Determine next approver based on the level
                String nextApprover = determineNextApprover(approvalData.getApprovalLevel());
                if (nextApprover != null) {
                    notifyNextApprover(orderId, nextApprover);
                }
            }
            
            // Create response
            SharedApprovalResponseDTO response = new SharedApprovalResponseDTO();
            response.setStatus("SUCCESS");
            response.setOrderId(orderId);
            response.setWorkflowStatus(workflowStatus.name());
            
            log.info("Approval processed successfully for order: {}, new status: {}", 
                    orderId, workflowStatus.name());
            
            return response;
            
        } catch (ApplicationInvalidApprovalException | ApplicationWorkflowException e) {
            log.error("Approval processing failed for order: {}", orderId, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error processing approval for order: {}", orderId, e);
            throw new ApplicationWorkflowException("Failed to process approval: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validates that the approver has authority to make the approval decision.
     * 
     * @param approverId The ID of the approver
     * @param approvalLevel The level at which the approval is being made
     * @param orderId The ID of the order being approved
     * @throws ApplicationInvalidApprovalException If the approver lacks authority
     */
    private void validateApproverAuthority(String approverId, String approvalLevel, String orderId) {
        if (approverId == null || approverId.trim().isEmpty()) {
            throw new ApplicationInvalidApprovalException("Approver ID must be provided");
        }
        
        // Get order value for authority check (simplified - would get real value from repository)
        DomainMonetaryAmount orderValue = new DomainMonetaryAmount(new BigDecimal("10000.00"), "USD"); // Placeholder
        
        // Check authorization through domain layer
        boolean isAuthorized = domainAuthorizationPort.validateApprovalAuthority(
                approverId, orderValue);
        
        if (!isAuthorized) {
            log.warn("Approver {} is not authorized to approve order {} at level {}", 
                    approverId, orderId, approvalLevel);
            throw new ApplicationInvalidApprovalException(
                    "Approver is not authorized to make this approval decision");
        }
        
        // Additional validation for correct approval level
        String expectedLevel = domainAuthorizationPort.getUserApprovalLevel(approverId);
        if (!approvalLevel.equals(expectedLevel)) {
            log.warn("Approver {} is at level {} but attempted to approve at level {}", 
                    approverId, expectedLevel, approvalLevel);
            throw new ApplicationInvalidApprovalException(
                    "Approver is not at the correct level for this approval");
        }
    }
    
    /**
     * Determines the next approver based on the current approval level.
     * 
     * @param currentLevel The current approval level
     * @return The ID of the next approver, or null if there is none
     */
    private String determineNextApprover(String currentLevel) {
        return workflowCoordinator.getNextApprover(currentLevel);
    }
    
    /**
     * Notifies the next approver that an order requires their approval.
     * 
     * @param orderId The ID of the order requiring approval
     * @param nextApproverId The ID of the next approver
     */
    private void notifyNextApprover(String orderId, String nextApproverId) {
        log.debug("Notifying next approver {} for order {}", nextApproverId, orderId);
        
        try {
            // For now we use a placeholder monetary amount
            DomainMonetaryAmount orderValue = new DomainMonetaryAmount(new BigDecimal("10000.00"), "USD");
            
            // Send notification through domain layer
            domainNotificationPort.sendApprovalNotification(nextApproverId, orderId, orderValue);
            
            log.debug("Notification sent to approver {} for order {}", nextApproverId, orderId);
            
        } catch (Exception e) {
            // Log but don't fail the whole process if notification fails
            log.error("Failed to send approval notification to {} for order {}", 
                    nextApproverId, orderId, e);
        }
    }
}
