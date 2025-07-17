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
 * DTO for inventory reorder request events consumed from Kafka.
 * Contains all necessary information to initiate procurement of materials.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedReorderRequestEventDTO {
    
    /** 
     * Unique identifier for the event 
     */
    @NotBlank(message = "Event ID cannot be blank")
    @Size(min = 1, max = 100, message = "Event ID must be between 1 and 100 characters")
    private String eventId;
    
    /** 
     * Identifier of the material to be reordered 
     */
    @NotBlank(message = "Material ID cannot be blank")
    @Size(min = 1, max = 50, message = "Material ID must be between 1 and 50 characters")
    private String materialId;
    
    /** 
     * Current stock level of the material 
     */
    @NotNull(message = "Current stock level cannot be null")
    @DecimalMin(value = "0.0", message = "Current stock level cannot be negative")
    @Digits(integer = 14, fraction = 4, message = "Current stock level must have at most 14 integer digits and 4 decimal places")
    private BigDecimal currentStockLevel;
    
    /** 
     * Quantity to be reordered - must be positive 
     */
    @NotNull(message = "Reorder quantity cannot be null")
    @DecimalMin(value = "0.0001", message = "Reorder quantity must be greater than 0")
    @Digits(integer = 14, fraction = 4, message = "Reorder quantity must have at most 14 integer digits and 4 decimal places")
    private BigDecimal reorderQuantity;
    
    /** 
     * Priority level for the reorder: HIGH, MEDIUM, or LOW 
     */
    @NotBlank(message = "Priority cannot be blank")
    @Size(min = 1, max = 20, message = "Priority must be between 1 and 20 characters")
    private String priority;
    
    /** 
     * Requested delivery date as ISO-8601 string (YYYY-MM-DDTHH:MM:SSZ) 
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{3})?Z?$", 
             message = "Requested delivery date must be in ISO-8601 format")
    @NotBlank(message = "Requested delivery date cannot be blank")
    private String requestedDeliveryDate;
    
    /** 
     * Warehouse location requesting the material 
     */
    @NotBlank(message = "Warehouse location cannot be blank")
    @Size(min = 1, max = 100, message = "Warehouse location must be between 1 and 100 characters")
    private String warehouseLocation;
}