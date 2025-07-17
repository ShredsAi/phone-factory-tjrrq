package ai.shreds.domain.ports;

import ai.shreds.shared.dtos.SharedPurchaseOrderDataDTO;

/**
 * Domain output port for EDI transmission of purchase orders.
 * This port is implemented by infrastructure clients that connect to EDI gateways.
 */
public interface DomainOutputPortEdiTransmission {

    /**
     * Sends a purchase order to a supplier via EDI.
     *
     * @param supplierId the ID of the supplier
     * @param orderData the purchase order data to transmit
     * @return the EDI transaction ID or confirmation reference
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureExternalServiceException if EDI transmission fails
     */
    String sendEdiOrder(String supplierId, SharedPurchaseOrderDataDTO orderData);

    /**
     * Receives and processes an EDI acknowledgment from a supplier.
     *
     * @param transactionId the EDI transaction ID
     * @return the acknowledgment status or details
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureExternalServiceException if acknowledgment processing fails
     */
    String receiveEdiAcknowledgment(String transactionId);
}