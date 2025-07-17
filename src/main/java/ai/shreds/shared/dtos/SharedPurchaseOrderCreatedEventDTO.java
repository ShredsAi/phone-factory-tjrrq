package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.Valid;
import java.util.List;

/**
 * DTO for internal PurchaseOrderCreated event publication.
 * Contains all necessary information about a newly created purchase order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedPurchaseOrderCreatedEventDTO {
    
    /** 
     * Type of the event - typically PURCHASE_ORDER_CREATED 
     */
    @NotBlank(message = "Event type cannot be blank")
    @Size(min = 1, max = 50, message = "Event type must be between 1 and 50 characters")
    private String eventType;
    
    /** 
     * Identifier of the purchase order 
     */
    @NotBlank(message = "Order ID cannot be blank")
    @Size(min = 1, max = 50, message = "Order ID must be between 1 and 50 characters")
    private String orderId;
    
    /** 
     * Identifier of the supplier 
     */
    @NotBlank(message = "Supplier ID cannot be blank")
    @Size(min = 1, max = 50, message = "Supplier ID must be between 1 and 50 characters")
    private String supplierId;
    
    /** 
     * Total monetary amount of the purchase order 
     */
    @NotNull(message = "Total amount cannot be null")
    @Valid
    private SharedMonetaryAmountDTO totalAmount;
    
    /** 
     * Expected delivery date as ISO-8601 string (YYYY-MM-DDTHH:MM:SSZ) 
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{3})?Z?$", 
             message = "Expected delivery date must be in ISO-8601 format")
    private String expectedDeliveryDate;
    
    /** 
     * List of line items in the order - must contain at least one item 
     */
    @NotEmpty(message = "Line items cannot be empty")
    @Valid
    private List<SharedOrderLineItemEventDTO> lineItems;
}