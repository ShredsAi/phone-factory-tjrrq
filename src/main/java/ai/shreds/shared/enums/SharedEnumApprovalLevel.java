package ai.shreds.shared.enums;

/**
 * Enumeration representing the organizational approval levels
 * in the procurement workflow hierarchy.
 */
public enum SharedEnumApprovalLevel {
    
    /** 
     * Supervisor level - required for orders under $5,000 
     */
    SUPERVISOR,
    
    /** 
     * Manager level - required for orders between $5,000 and $25,000 
     */
    MANAGER,
    
    /** 
     * Director level - required for orders over $25,000 
     */
    DIRECTOR
}