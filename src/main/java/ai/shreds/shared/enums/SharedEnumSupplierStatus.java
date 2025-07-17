package ai.shreds.shared.enums;

/**
 * Enumeration representing the possible statuses
 * of suppliers in the procurement system.
 */
public enum SharedEnumSupplierStatus {
    
    /** 
     * Supplier is active and can receive orders 
     */
    ACTIVE,
    
    /** 
     * Supplier is inactive and cannot receive orders 
     */
    INACTIVE,
    
    /** 
     * Supplier is suspended due to performance issues 
     */
    SUSPENDED,
    
    /** 
     * Supplier is pending approval for activation 
     */
    PENDING_APPROVAL,
    
    /** 
     * Supplier is under review by procurement team 
     */
    UNDER_REVIEW
}