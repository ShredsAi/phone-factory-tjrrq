package ai.shreds.domain.value_objects;

/**
 * Enum representing the different approval levels in the organizational hierarchy.
 * Used to determine the required approval authority for purchase orders based on value thresholds.
 */
public enum DomainApprovalLevel {
    /**
     * Supervisor level approval - typically for orders under $5,000
     */
    SUPERVISOR,
    
    /**
     * Manager level approval - typically for orders between $5,000 and $25,000
     */
    MANAGER,
    
    /**
     * Director level approval - typically for orders over $25,000
     */
    DIRECTOR
}