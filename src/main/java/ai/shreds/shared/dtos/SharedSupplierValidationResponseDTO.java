package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.math.BigDecimal;

/**
 * DTO for supplier validation response.
 * Contains the result of validating supplier capability and pricing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedSupplierValidationResponseDTO {
    
    /** 
     * Indicates if the supplier is available for the requested material and quantity 
     */
    @NotNull(message = "Availability indicator cannot be null")
    private Boolean isAvailable;
    
    /** 
     * Current price from supplier catalog - must be positive when available 
     */
    @DecimalMin(value = "0.0", message = "Current price cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Current price must have at most 15 integer digits and 4 decimal places")
    private BigDecimal currentPrice;
    
    /** 
     * Currency code for the current price (ISO 4217 format) 
     */
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter ISO 4217 code")
    private String currency;
    
    /** 
     * Lead time in days for delivery 
     */
    @Min(value = 1, message = "Lead time must be at least 1 day")
    @Max(value = 365, message = "Lead time cannot exceed 365 days")
    private Integer leadTimeDays;
    
    /** 
     * Reason if not available or other notes 
     */
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;
}