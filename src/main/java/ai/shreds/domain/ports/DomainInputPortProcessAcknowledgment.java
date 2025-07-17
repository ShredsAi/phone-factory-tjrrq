package ai.shreds.domain.ports;

import ai.shreds.shared.dtos.SharedSupplierAcknowledgmentRequestDTO;

/**
 * Domain input port for processing supplier acknowledgments of purchase orders.
 * This port is implemented by domain services within the domain layer.
 */
public interface DomainInputPortProcessAcknowledgment {
    /**
     * Processes a supplier acknowledgment for a purchase order.
     * 
     * @param orderId ID of the purchase order being acknowledged
     * @param acknowledgment The acknowledgment details from the supplier
     * @throws ai.shreds.domain.exceptions.DomainInvalidOrderStateException if the order is not in a state where acknowledgment can be processed
     */
    void processAcknowledgment(String orderId, SharedSupplierAcknowledgmentRequestDTO acknowledgment);
}