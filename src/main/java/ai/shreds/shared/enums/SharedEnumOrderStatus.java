package ai.shreds.shared.enums;

/**
 * Enumeration representing the possible statuses
 * of purchase orders in the procurement workflow.
 */
public enum SharedEnumOrderStatus {
    
    /** 
     * Order is in draft state, not yet submitted for approval 
     */
    DRAFT,
    
    /** 
     * Order is awaiting approval from authorized personnel 
     */
    PENDING_APPROVAL,
    
    /** 
     * Order has been approved and is ready for transmission 
     */
    APPROVED,
    
    /** 
     * Order has been sent to the supplier 
     */
    SENT,
    
    /** 
     * Order has been acknowledged by the supplier 
     */
    ACKNOWLEDGED,
    
    /** 
     * Order has been delivered and completed 
     */
    DELIVERED,
    
    /** 
     * Order has been rejected during approval process 
     */
    REJECTED,
    
    /** 
     * Order has been cancelled 
     */
    CANCELLED
}