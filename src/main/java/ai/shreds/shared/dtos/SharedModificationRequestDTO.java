package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;

/**
 * DTO for modification request of a purchase order.
 * Contains the details of what should be modified in an existing order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedModificationRequestDTO {
    
    /** 
     * Type of modification: UPDATE_QUANTITY, UPDATE_PRICE, ADD_LINE_ITEM, etc. 
     */
    @NotBlank(message = "Modification type cannot be blank")
    @Size(min = 1, max = 50, message = "Modification type must be between 1 and 50 characters")
    private String modificationType;
    
    /** 
     * Identifier of the line item to modify (UUID format) 
     */
    @NotBlank(message = "Line item ID cannot be blank")
    @Size(min = 1, max = 50, message = "Line item ID must be between 1 and 50 characters")
    private String lineItemId;
    
    /** 
     * New quantity for the line item - must be positive 
     */
    @DecimalMin(value = "0.0001", message = "New quantity must be greater than 0")
    @Digits(integer = 14, fraction = 4, message = "New quantity must have at most 14 integer digits and 4 decimal places")
    private BigDecimal newQuantity;
    
    /** 
     * Reason for the modification - required for audit trail 
     */
    @NotBlank(message = "Reason cannot be blank")
    @Size(min = 1, max = 500, message = "Reason must be between 1 and 500 characters")
    private String reason;
}