package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * Data Transfer Object for monetary amounts.
 * Contains amount and currency information with validation constraints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedMonetaryAmountDTO {
    
    /** 
     * The monetary amount value - must be non-negative 
     */
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", message = "Amount cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Amount must have at most 15 integer digits and 4 decimal places")
    private BigDecimal amount;
    
    /** 
     * The ISO 4217 currency code (e.g., USD, EUR, GBP) 
     */
    @NotBlank(message = "Currency cannot be blank")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter ISO 4217 code")
    private String currency;
}
