package ai.shreds.shared.enums;

/**
 * Enumeration representing the possible response statuses
 * for API operations in the procurement workflow.
 */
public enum SharedEnumResponseStatus {
    
    /** 
     * Operation completed successfully 
     */
    SUCCESS,
    
    /** 
     * Operation failed with errors 
     */
    FAILURE,
    
    /** 
     * Operation completed with warnings 
     */
    WARNING,
    
    /** 
     * Operation is still pending or in progress 
     */
    PENDING
}