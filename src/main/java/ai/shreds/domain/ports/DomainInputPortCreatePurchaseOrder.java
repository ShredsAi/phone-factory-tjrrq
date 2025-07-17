package ai.shreds.domain.ports;

import ai.shreds.domain.value_objects.DomainPurchaseOrderId;
import ai.shreds.shared.dtos.SharedPurchaseOrderDataDTO;

/**
 * Domain input port for creating purchase orders.
 * This port is implemented by domain services within the domain layer.
 */
public interface DomainInputPortCreatePurchaseOrder {
    /**
     * Creates a new purchase order from the provided order data.
     * 
     * @param orderData the purchase order data containing all necessary information
     * @return the unique identifier of the created purchase order
     * @throws ai.shreds.domain.exceptions.DomainOrderCreationException if order creation fails
     */
    DomainPurchaseOrderId createOrder(SharedPurchaseOrderDataDTO orderData);
}