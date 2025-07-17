package ai.shreds.domain.ports;

import ai.shreds.shared.dtos.SharedModificationRequestDTO;
import ai.shreds.shared.dtos.SharedModificationResultDTO;

/**
 * Domain input port for modifying purchase orders.
 * This port is implemented by domain services within the domain layer.
 */
public interface DomainInputPortModifyOrder {
    /**
     * Modifies an existing purchase order based on the modification request.
     * 
     * @param orderId ID of the purchase order to be modified
     * @param modifications The modification details including type, line item ID, new quantity, etc.
     * @return The result of the modification operation including success status, new total amount, etc.
     * @throws ai.shreds.domain.exceptions.DomainInvalidOrderStateException if the order cannot be modified in its current state
     */
    SharedModificationResultDTO modifyOrder(String orderId, SharedModificationRequestDTO modifications);
}