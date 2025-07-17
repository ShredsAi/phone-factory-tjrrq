package ai.shreds.domain.ports;

import ai.shreds.domain.value_objects.DomainMonetaryAmount;

/**
 * Domain output port for authorization service interactions.
 * Implemented by infrastructure clients to validate user approval authority.
 */
public interface DomainOutputPortAuthorization {

    /**
     * Validates that the user has authority to approve a purchase order of the given value.
     *
     * @param userId    the ID of the user performing the approval
     * @param orderValue the monetary value of the order
     * @return true if authorized, false otherwise
     */
    boolean validateApprovalAuthority(String userId, DomainMonetaryAmount orderValue);

    /**
     * Retrieves the approval level of the given user (e.g., SUPERVISOR, MANAGER, DIRECTOR).
     *
     * @param userId the ID of the user
     * @return the user’s approval level
     */
    String getUserApprovalLevel(String userId);
}