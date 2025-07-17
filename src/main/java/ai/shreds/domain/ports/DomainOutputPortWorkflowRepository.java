package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainApprovalWorkflowEntity;
import java.util.UUID;

public interface DomainOutputPortWorkflowRepository {
    
    /**
     * Saves an approval workflow entity to the repository
     * @param workflow the approval workflow entity to save
     * @return the saved approval workflow entity
     */
    DomainApprovalWorkflowEntity save(DomainApprovalWorkflowEntity workflow);
    
    /**
     * Finds an approval workflow by purchase order ID
     * @param orderId the purchase order ID to search for
     * @return the workflow if found, null otherwise
     */
    DomainApprovalWorkflowEntity findByPurchaseOrderId(String orderId);
    
    /**
     * Updates an existing approval workflow
     * @param workflow the approval workflow entity to update
     * @return the updated approval workflow entity
     */
    DomainApprovalWorkflowEntity update(DomainApprovalWorkflowEntity workflow);
}