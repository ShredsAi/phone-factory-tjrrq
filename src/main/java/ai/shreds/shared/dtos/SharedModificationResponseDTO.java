package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for modification response of a purchase order.
 * Contains the outcome of processing a modification request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedModificationResponseDTO {
    
    /** 
     * Outcome status: MODIFIED, FAILURE, PENDING, or REJECTED 
     */
    @NotBlank(message = "Status cannot be blank")
    @Size(min = 1, max = 50, message = "Status must be between 1 and 50 characters")
    private String status;
    
    /** 
     * Identifier of the related purchase order 
     */
    @NotBlank(message = "Order ID cannot be blank")
    @Size(min = 1, max = 50, message = "Order ID must be between 1 and 50 characters")
    private String orderId;
    
    /** 
     * Flag indicating whether this modification requires re-approval 
     */
    @NotNull(message = "Requires reapproval flag cannot be null")
    private Boolean requiresReapproval;
}