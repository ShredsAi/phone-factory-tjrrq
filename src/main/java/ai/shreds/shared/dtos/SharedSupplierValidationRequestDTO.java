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
 * DTO for supplier validation requests.
 * Used to verify if a supplier can fulfill a specific material in the requested quantity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedSupplierValidationRequestDTO {
    
    /** 
     * Identifier of the supplier to validate 
     */
    @NotBlank(message = "Supplier ID cannot be blank")
    @Size(min = 1, max = 50, message = "Supplier ID must be between 1 and 50 characters")
    private String supplierId;
    
    /** 
     * Identifier of the material to check 
     */
    @NotBlank(message = "Material ID cannot be blank")
    @Size(min = 1, max = 50, message = "Material ID must be between 1 and 50 characters")
    private String materialId;
    
    /** 
     * Requested quantity to validate - must be positive 
     */
    @NotNull(message = "Quantity cannot be null")
    @DecimalMin(value = "0.0001", message = "Quantity must be greater than 0")
    @Digits(integer = 14, fraction = 4, message = "Quantity must have at most 14 integer digits and 4 decimal places")
    private BigDecimal quantity;
}