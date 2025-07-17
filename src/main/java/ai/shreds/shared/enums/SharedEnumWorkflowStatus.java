package ai.shreds.shared.enums;

/**
 * Enumeration representing the possible workflow statuses
 * in the procurement approval process.
 */
public enum SharedEnumWorkflowStatus {
    
    /** 
     * Workflow is pending initiation 
     */
    PENDING,
    
    /** 
     * Workflow is currently in progress 
     */
    IN_PROGRESS,
    
    /** 
     * Workflow has been approved and completed 
     */
    APPROVED,
    
    /** 
     * Workflow has been rejected 
     */
    REJECTED,
    
    /** 
     * Workflow has been cancelled 
     */
    CANCELLED
}