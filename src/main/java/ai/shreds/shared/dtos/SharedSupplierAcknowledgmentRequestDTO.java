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
 * DTO for supplier acknowledgment request (webhook).
 * Contains the supplier's response to a purchase order transmission.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedSupplierAcknowledgmentRequestDTO {
    
    /** 
     * Supplier order identifier from supplier system 
     */
    @NotBlank(message = "Supplier order ID cannot be blank")
    @Size(min = 1, max = 50, message = "Supplier order ID must be between 1 and 50 characters")
    private String supplierOrderId;
    
    /** 
     * Purchase order identifier in this system 
     */
    @NotBlank(message = "Purchase order ID cannot be blank")
    @Size(min = 1, max = 50, message = "Purchase order ID must be between 1 and 50 characters")
    private String purchaseOrderId;
    
    /** 
     * Acknowledgment status: RECEIVED, REJECTED, PARTIAL_ACCEPTANCE, etc. 
     */
    @NotBlank(message = "Status cannot be blank")
    @Size(min = 1, max = 50, message = "Status must be between 1 and 50 characters")
    private String status;
    
    /** 
     * Confirmed delivery date as ISO-8601 string (YYYY-MM-DDTHH:MM:SSZ) 
     */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{3})?Z?$", 
             message = "Confirmed delivery date must be in ISO-8601 format")
    private String confirmedDeliveryDate;
    
    /** 
     * Confirmations per line item - must contain at least one item 
     */
    @NotEmpty(message = "Line item confirmations cannot be empty")
    @Valid
    private List<SharedLineItemConfirmationDTO> lineItemConfirmations;
}