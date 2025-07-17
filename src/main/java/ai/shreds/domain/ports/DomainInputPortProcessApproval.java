package ai.shreds.domain.ports;

import ai.shreds.domain.value_objects.DomainWorkflowStatus;
import ai.shreds.shared.dtos.SharedApprovalRequestDTO;

/**
 * Domain input port for processing approval decisions on purchase orders.
 * This port is implemented by domain services within the domain layer.
 */
public interface DomainInputPortProcessApproval {
    /**
     * Processes an approval decision for a purchase order.
     * 
     * @param orderId ID of the purchase order to be approved/rejected
     * @param decision The approval decision containing details like approver ID, comments, etc.
     * @return The current workflow status after processing the decision
     * @throws ai.shreds.domain.exceptions.DomainInvalidOrderStateException if the order is not in a state where approval can be processed
     * @throws ai.shreds.domain.exceptions.DomainWorkflowViolationException if the approval violates workflow rules
     */
    DomainWorkflowStatus processApproval(String orderId, SharedApprovalRequestDTO decision);
}