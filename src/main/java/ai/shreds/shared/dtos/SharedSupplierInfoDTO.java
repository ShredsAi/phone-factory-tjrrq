package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * DTO for supplier information.
 * Contains essential supplier details used across the procurement system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedSupplierInfoDTO {
    
    /** 
     * Unique identifier for the supplier 
     */
    @NotBlank(message = "Supplier ID cannot be blank")
    @Size(min = 1, max = 50, message = "Supplier ID must be between 1 and 50 characters")
    private String supplierId;
    
    /** 
     * Company name of the supplier 
     */
    @NotBlank(message = "Company name cannot be blank")
    @Size(min = 1, max = 200, message = "Company name must be between 1 and 200 characters")
    private String companyName;
    
    /** 
     * Current status of the supplier: ACTIVE, INACTIVE, SUSPENDED, etc. 
     */
    @NotBlank(message = "Status cannot be blank")
    @Size(min = 1, max = 50, message = "Status must be between 1 and 50 characters")
    private String status;
    
    /** 
     * Lead time in days for this supplier - must be positive 
     */
    @NotNull(message = "Lead time cannot be null")
    @Min(value = 1, message = "Lead time must be at least 1 day")
    @Max(value = 365, message = "Lead time cannot exceed 365 days")
    private Integer leadTime;
    
    /** 
     * Flag indicating if the supplier is active and can receive orders 
     */
    @NotNull(message = "Active flag cannot be null")
    private Boolean isActive;
}