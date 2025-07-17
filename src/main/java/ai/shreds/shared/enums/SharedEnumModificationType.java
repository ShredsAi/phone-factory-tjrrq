package ai.shreds.shared.enums;

/**
 * Enumeration representing the possible types of modifications
 * that can be made to a purchase order.
 */
public enum SharedEnumModificationType {
    
    /** 
     * Update the quantity of an existing line item 
     */
    UPDATE_QUANTITY,
    
    /** 
     * Update the price of an existing line item 
     */
    UPDATE_PRICE,
    
    /** 
     * Add a new line item to the purchase order 
     */
    ADD_LINE_ITEM,
    
    /** 
     * Remove an existing line item from the purchase order 
     */
    REMOVE_LINE_ITEM,
    
    /** 
     * Update delivery date for a line item 
     */
    UPDATE_DELIVERY_DATE,
    
    /** 
     * Update specifications or notes for a line item 
     */
    UPDATE_SPECIFICATIONS
}