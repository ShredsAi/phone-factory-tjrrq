package ai.shreds.application.services;

import ai.shreds.shared.dtos.SharedPurchaseOrderDataDTO;

/**
 * Strategy interface for transmitting purchase orders to suppliers through various communication channels.
 */
public interface ApplicationSupplierCommunicationStrategy {
    
    /**
     * Transmits a purchase order to a supplier.
     * 
     * @param purchaseOrder The purchase order to transmit
     */
    void transmitOrder(SharedPurchaseOrderDataDTO purchaseOrder);
    
    /**
     * Determines if this strategy supports the given communication method.
     * 
     * @param communicationMethod The communication method to check
     * @return true if this strategy supports the given method, false otherwise
     */
    boolean isSupported(String communicationMethod);
}