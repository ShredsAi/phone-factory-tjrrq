package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.Valid;
import java.math.BigDecimal;

/**
 * DTO representing a line item in purchase order events.
 * Contains detailed information about a single line item in a purchase order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedOrderLineItemEventDTO {
    
    /** 
     * Unique identifier of the line item (UUID format) 
     */
    @NotBlank(message = "Line item ID cannot be blank")
    @Size(min = 1, max = 50, message = "Line item ID must be between 1 and 50 characters")
    private String lineItemId;
    
    /** 
     * Material identifier 
     */
    @NotBlank(message = "Material ID cannot be blank")
    @Size(min = 1, max = 50, message = "Material ID must be between 1 and 50 characters")
    private String materialId;
    
    /** 
     * Quantity ordered - must be positive 
     */
    @NotNull(message = "Quantity cannot be null")
    @DecimalMin(value = "0.0001", message = "Quantity must be greater than 0")
    @Digits(integer = 14, fraction = 4, message = "Quantity must have at most 14 integer digits and 4 decimal places")
    private BigDecimal quantity;
    
    /** 
     * Unit price information 
     */
    @NotNull(message = "Unit price cannot be null")
    @Valid
    private SharedMonetaryAmountDTO unitPrice;
    
    /** 
     * Total price for this line item 
     */
    @NotNull(message = "Line total cannot be null")
    @Valid
    private SharedMonetaryAmountDTO lineTotal;
    
    /** 
     * Requested delivery date as ISO-8601 string (YYYY-MM-DDTHH:MM:SSZ) 
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{3})?Z?$", 
             message = "Requested delivery date must be in ISO-8601 format")
    private String requestedDeliveryDate;
}
