package ai.shreds.shared.enums;

/**
 * Enumeration representing the possible approval decisions
 * in the procurement workflow.
 */
public enum SharedEnumApprovalDecision {
    
    /** 
     * The purchase order has been approved by the current approver 
     */
    APPROVED,
    
    /** 
     * The purchase order has been rejected by the current approver 
     */
    REJECTED,
    
    /** 
     * The purchase order has been escalated to a higher approval level 
     */
    ESCALATED
}