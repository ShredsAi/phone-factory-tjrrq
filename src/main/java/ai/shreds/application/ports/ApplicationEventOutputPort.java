package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedPurchaseOrderCreatedEventDTO;

public interface ApplicationEventOutputPort {

    /**
     * Publish an event when a purchase order is created.
     * @param event the purchase order created event data
     */
    void publishPurchaseOrderCreated(SharedPurchaseOrderCreatedEventDTO event);
}