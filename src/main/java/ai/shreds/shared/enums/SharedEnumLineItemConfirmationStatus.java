package ai.shreds.shared.enums;

/**
 * Enumeration representing the possible confirmation statuses
 * for individual line items in supplier acknowledgments.
 */
public enum SharedEnumLineItemConfirmationStatus {
    
    /** 
     * Line item is fully confirmed by supplier 
     */
    CONFIRMED,
    
    /** 
     * Line item is partially confirmed (quantity less than requested) 
     */
    PARTIAL_CONFIRMATION,
    
    /** 
     * Line item is rejected by supplier 
     */
    REJECTED,
    
    /** 
     * Line item is pending supplier review 
     */
    PENDING_REVIEW,
    
    /** 
     * Line item is confirmed with modifications (price, delivery date, etc.) 
     */
    CONFIRMED_WITH_CHANGES
}