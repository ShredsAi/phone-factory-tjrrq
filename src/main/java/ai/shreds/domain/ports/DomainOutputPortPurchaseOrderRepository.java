package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainPurchaseOrderAggregate;
import ai.shreds.domain.value_objects.DomainOrderStatus;
import java.util.List;

public interface DomainOutputPortPurchaseOrderRepository {
    
    /**
     * Saves a purchase order aggregate to the repository
     * @param order the purchase order aggregate to save
     * @return the saved purchase order aggregate
     */
    DomainPurchaseOrderAggregate save(DomainPurchaseOrderAggregate order);
    
    /**
     * Finds a purchase order by its ID
     * @param orderId the order ID to search for
     * @return the purchase order aggregate if found, null otherwise
     */
    DomainPurchaseOrderAggregate findById(String orderId);
    
    /**
     * Updates an existing purchase order
     * @param order the purchase order aggregate to update
     * @return the updated purchase order aggregate
     */
    DomainPurchaseOrderAggregate update(DomainPurchaseOrderAggregate order);
    
    /**
     * Finds all purchase orders with a specific status
     * @param status the order status to filter by
     * @return list of purchase orders matching the status
     */
    List<DomainPurchaseOrderAggregate> findByStatus(DomainOrderStatus status);
}