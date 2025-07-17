package ai.shreds.domain.ports;

import ai.shreds.shared.dtos.SharedPurchaseOrderDataDTO;

/**
 * Domain output port for supplier API communication.
 * This port is implemented by infrastructure clients that connect to supplier REST APIs.
 */
public interface DomainOutputPortSupplierApi {

    /**
     * Transmits a purchase order to a supplier via their REST API.
     *
     * @param supplierId the ID of the supplier
     * @param orderData the purchase order data to transmit
     * @return the supplier's internal order ID or transaction reference
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureExternalServiceException if transmission fails
     */
    String transmitPurchaseOrder(String supplierId, SharedPurchaseOrderDataDTO orderData);

    /**
     * Checks the status of a purchase order with a supplier.
     *
     * @param supplierId the ID of the supplier
     * @param supplierOrderId the supplier's internal order ID
     * @return the current status of the order from the supplier's perspective
     * @throws ai.shreds.infrastructure.exceptions.InfrastructureExternalServiceException if status check fails
     */
    String checkOrderStatus(String supplierId, String supplierOrderId);
}