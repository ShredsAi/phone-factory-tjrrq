package ai.shreds.shared.enums;

/**
 * Enumeration representing the possible statuses
 * for supplier acknowledgment of purchase orders.
 */
public enum SharedEnumSupplierAcknowledgmentStatus {
    
    /** 
     * Purchase order has been received and accepted by supplier 
     */
    RECEIVED,
    
    /** 
     * Purchase order has been rejected by supplier 
     */
    REJECTED,
    
    /** 
     * Purchase order has been partially accepted by supplier 
     */
    PARTIAL_ACCEPTANCE,
    
    /** 
     * Purchase order is pending review by supplier 
     */
    PENDING_REVIEW,
    
    /** 
     * Purchase order has been accepted with modifications 
     */
    ACCEPTED_WITH_CHANGES
}