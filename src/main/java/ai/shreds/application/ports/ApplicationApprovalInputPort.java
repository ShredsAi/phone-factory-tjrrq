package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedApprovalRequestDTO;
import ai.shreds.shared.dtos.SharedApprovalResponseDTO;

/**
 * Input port for processing purchase order approval requests.
 */
public interface ApplicationApprovalInputPort {

    /**
     * Process an approval decision for a purchase order.
     * @param orderId Identifier of the purchase order
     * @param approvalData Approval request data
     * @return Approval response with updated workflow status
     */
    SharedApprovalResponseDTO processApproval(String orderId, SharedApprovalRequestDTO approvalData);
}