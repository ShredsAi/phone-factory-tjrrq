package ai.shreds.shared.enums;

/**
 * Enumeration representing the possible statuses
 * for purchase order modification operations.
 */
public enum SharedEnumModificationStatus {
    
    /** 
     * Modification was successfully applied 
     */
    MODIFIED,
    
    /** 
     * Modification failed due to validation or system errors 
     */
    FAILURE,
    
    /** 
     * Modification is pending approval or processing 
     */
    PENDING,
    
    /** 
     * Modification was rejected by business rules or approver 
     */
    REJECTED
}