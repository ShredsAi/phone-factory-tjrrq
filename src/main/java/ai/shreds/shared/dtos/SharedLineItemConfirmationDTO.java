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
import java.math.BigDecimal;

/**
 * DTO for line item confirmation from supplier acknowledgment.
 * Contains the supplier's confirmation details for a specific line item.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedLineItemConfirmationDTO {
    
    /** 
     * Identifier of the line item being confirmed (UUID format) 
     */
    @NotBlank(message = "Line item ID cannot be blank")
    @Size(min = 1, max = 50, message = "Line item ID must be between 1 and 50 characters")
    private String lineItemId;
    
    /** 
     * Quantity confirmed by the supplier - must be positive 
     */
    @NotNull(message = "Confirmed quantity cannot be null")
    @DecimalMin(value = "0.0", message = "Confirmed quantity must be zero or greater")
    @Digits(integer = 14, fraction = 4, message = "Confirmed quantity must have at most 14 integer digits and 4 decimal places")
    private BigDecimal confirmedQuantity;
    
    /** 
     * Confirmed delivery date as ISO-8601 string (YYYY-MM-DDTHH:MM:SSZ) 
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{3})?Z?$", 
             message = "Confirmed delivery date must be in ISO-8601 format")
    private String confirmedDeliveryDate;
    
    /** 
     * Status of the line item confirmation: CONFIRMED, PARTIAL_CONFIRMATION, REJECTED, etc. 
     */
    @NotBlank(message = "Status cannot be blank")
    @Size(min = 1, max = 50, message = "Status must be between 1 and 50 characters")
    private String status;
}