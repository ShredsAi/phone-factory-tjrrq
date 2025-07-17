package ai.shreds.shared.enums;

/**
 * Enumeration representing the types of events
 * that can be published in the procurement system.
 */
public enum SharedEnumEventType {
    
    /** 
     * Event published when a new purchase order is created 
     */
    PURCHASE_ORDER_CREATED,
    
    /** 
     * Event published when a purchase order is transmitted to supplier 
     */
    PURCHASE_ORDER_TRANSMITTED,
    
    /** 
     * Event published when a purchase order is approved 
     */
    PURCHASE_ORDER_APPROVED,
    
    /** 
     * Event published when a purchase order is rejected 
     */
    PURCHASE_ORDER_REJECTED,
    
    /** 
     * Event published when a purchase order is modified 
     */
    PURCHASE_ORDER_MODIFIED,
    
    /** 
     * Event published when a purchase order is acknowledged by supplier 
     */
    PURCHASE_ORDER_ACKNOWLEDGED
}