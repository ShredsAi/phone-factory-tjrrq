package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

/**
 * DTO representing the result of an order modification.
 * Contains information about the outcome of a modification request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedModificationResultDTO {
    
    /** 
     * Indicates if the modification was successful 
     */
    @NotNull(message = "Success indicator cannot be null")
    private Boolean success;
    
    /** 
     * Identifier of the modified order 
     */
    @NotBlank(message = "Order ID cannot be blank")
    @Size(min = 1, max = 50, message = "Order ID must be between 1 and 50 characters")
    private String orderId;
    
    /** 
     * New total amount after the modification 
     */
    @Valid
    private SharedMonetaryAmountDTO newTotalAmount;
    
    /** 
     * Indicates if the modification requires re-approval 
     */
    @NotNull(message = "Requires reapproval flag cannot be null")
    private Boolean requiresReapproval;
    
    /** 
     * Optional message or details about the modification result 
     */
    @Size(max = 500, message = "Message cannot exceed 500 characters")
    private String message;
}