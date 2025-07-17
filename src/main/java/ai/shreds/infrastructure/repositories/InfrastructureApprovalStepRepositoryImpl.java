package ai.shreds.infrastructure.repositories;

import ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of additional business logic for approval step repository operations.
 * This class provides extended functionality beyond the basic JPA repository operations.
 */
@Repository
@Transactional
public class InfrastructureApprovalStepRepositoryImpl {
    
    private final InfrastructureApprovalStepRepository jpaRepository;
    
    @Autowired
    public InfrastructureApprovalStepRepositoryImpl(InfrastructureApprovalStepRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    /**
     * Saves an approval step and ensures proper workflow association
     * @param step the approval step to save
     * @return the saved approval step
     */
    public InfrastructureApprovalStepJpaEntity save(InfrastructureApprovalStepJpaEntity step) {
        try {
            if (step.getWorkflow() == null) {
                throw new InfrastructureRepositoryException("Approval step must be associated with a workflow");
            }
            return jpaRepository.save(step);
        } catch (Exception e) {
            throw new InfrastructureRepositoryException("Failed to save approval step: " + step.getStepId(), e);
        }
    }
    
    /**
     * Finds approval steps by workflow ID with proper error handling
     * @param workflowId the workflow ID
     * @return list of approval steps
     */
    @Transactional(readOnly = true)
    public List<InfrastructureApprovalStepJpaEntity> findByWorkflowId(UUID workflowId) {
        try {
            return jpaRepository.findByWorkflowId(workflowId);
        } catch (Exception e) {
            throw new InfrastructureRepositoryException("Failed to find approval steps by workflow ID: " + workflowId, e);
        }
    }
    
    /**
     * Finds the latest approval step for a given workflow
     * @param workflowId the workflow ID
     * @return the latest approval step if found
     */
    @Transactional(readOnly = true)
    public Optional<InfrastructureApprovalStepJpaEntity> findLatestByWorkflowId(UUID workflowId) {
        try {
            List<InfrastructureApprovalStepJpaEntity> steps = jpaRepository.findByWorkflowIdOrderByDecisionTimestampDesc(workflowId);
            return steps.isEmpty() ? Optional.empty() : Optional.of(steps.get(0));
        } catch (Exception e) {
            throw new InfrastructureRepositoryException("Failed to find latest approval step for workflow: " + workflowId, e);
        }
    }
    
    /**
     * Finds all approval steps for a given purchase order ID
     * @param purchaseOrderId the purchase order ID
     * @return list of approval steps
     */
    @Transactional(readOnly = true)
    public List<InfrastructureApprovalStepJpaEntity> findByPurchaseOrderId(String purchaseOrderId) {
        try {
            return jpaRepository.findByPurchaseOrderId(purchaseOrderId);
        } catch (Exception e) {
            throw new InfrastructureRepositoryException("Failed to find approval steps by purchase order ID: " + purchaseOrderId, e);
        }
    }
    
    /**
     * Finds approval steps by approver ID
     * @param approverId the approver ID
     * @return list of approval steps
     */
    @Transactional(readOnly = true)
    public List<InfrastructureApprovalStepJpaEntity> findByApproverId(String approverId) {
        try {
            return jpaRepository.findByApproverId(approverId);
        } catch (Exception e) {
            throw new InfrastructureRepositoryException("Failed to find approval steps by approver ID: " + approverId, e);
        }
    }
    
    /**
     * Finds approval steps by decision type
     * @param decision the decision type (APPROVED, REJECTED, ESCALATED)
     * @return list of approval steps
     */
    @Transactional(readOnly = true)
    public List<InfrastructureApprovalStepJpaEntity> findByDecision(String decision) {
        try {
            return jpaRepository.findByDecision(decision);
        } catch (Exception e) {
            throw new InfrastructureRepositoryException("Failed to find approval steps by decision: " + decision, e);
        }
    }
    
    /**
     * Checks if any approval steps exist for a given workflow
     * @param workflowId the workflow ID
     * @return true if steps exist, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByWorkflowId(UUID workflowId) {
        try {
            return jpaRepository.existsByWorkflowId(workflowId);
        } catch (Exception e) {
            throw new InfrastructureRepositoryException("Failed to check existence of approval steps for workflow: " + workflowId, e);
        }
    }
    
    /**
     * Deletes an approval step by ID
     * @param stepId the step ID to delete
     */
    public void deleteById(UUID stepId) {
        try {
            if (!jpaRepository.existsById(stepId)) {
                throw new InfrastructureRepositoryException("Approval step not found for deletion: " + stepId);
            }
            jpaRepository.deleteById(stepId);
        } catch (Exception e) {
            throw new InfrastructureRepositoryException("Failed to delete approval step: " + stepId, e);
        }
    }
    
    /**
     * Counts the number of approval steps for a workflow
     * @param workflowId the workflow ID
     * @return the count of approval steps
     */
    @Transactional(readOnly = true)
    public long countByWorkflowId(UUID workflowId) {
        try {
            return jpaRepository.findByWorkflowId(workflowId).size();
        } catch (Exception e) {
            throw new InfrastructureRepositoryException("Failed to count approval steps for workflow: " + workflowId, e);
        }
    }
}