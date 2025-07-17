package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedPurchaseOrderTransmittedEventDTO;

public interface ApplicationKafkaOutputPort {

    /**
     * Publish an event when a purchase order has been transmitted.
     * @param event the purchase order transmitted event data
     */
    void publishPurchaseOrderTransmitted(SharedPurchaseOrderTransmittedEventDTO event);
}