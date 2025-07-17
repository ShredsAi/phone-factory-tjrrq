package ai.shreds.domain.value_objects;

/**
 * Enum representing the various states of an approval workflow.
 * Used to track the progress of purchase order approvals through the system.
 */
public enum DomainWorkflowStatus {
    /**
     * Workflow is waiting to be started or processed
     */
    PENDING,
    
    /**
     * Workflow is currently being processed (approvals in progress)
     */
    IN_PROGRESS,
    
    /**
     * Workflow has been completed successfully with approval
     */
    APPROVED,
    
    /**
     * Workflow has been rejected by an approver
     */
    REJECTED,
    
    /**
     * Workflow has been cancelled before completion
     */
    CANCELLED
}