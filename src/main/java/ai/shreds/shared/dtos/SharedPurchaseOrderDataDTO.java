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
 * DTO representing full purchase order data.
 * Contains comprehensive information about a purchase order including all line items and metadata.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedPurchaseOrderDataDTO {
    
    /** 
     * Unique identifier of the purchase order 
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
     * Order creation date as ISO-8601 string (YYYY-MM-DDTHH:MM:SSZ) 
     */
    @NotBlank(message = "Order date cannot be blank")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{3})?Z?$", 
             message = "Order date must be in ISO-8601 format")
    private String orderDate;
    
    /** 
     * Current status of the order: DRAFT, PENDING_APPROVAL, APPROVED, SENT, etc. 
     */
    @NotBlank(message = "Status cannot be blank")
    @Size(min = 1, max = 50, message = "Status must be between 1 and 50 characters")
    private String status;
    
    /** 
     * Line items in the purchase order - must contain at least one item 
     */
    @NotEmpty(message = "Line items cannot be empty")
    @Valid
    private List<SharedOrderLineItemEventDTO> lineItems;
    
    /** 
     * Total amount for the purchase order 
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
     * Payment terms agreed upon with the supplier 
     */
    @Size(max = 500, message = "Payment terms cannot exceed 500 characters")
    private String paymentTerms;
    
    /** 
     * Delivery conditions specified for the order 
     */
    @Size(max = 500, message = "Delivery conditions cannot exceed 500 characters")
    private String deliveryConditions;
}