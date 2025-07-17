package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for approval decision responses in the procurement workflow.
 * Contains the outcome of processing an approval request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedApprovalResponseDTO {
    
    /** 
     * Outcome status: SUCCESS, FAILURE, WARNING, or PENDING 
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
     * Current workflow status after processing: PENDING, IN_PROGRESS, APPROVED, REJECTED, or CANCELLED 
     */
    @NotBlank(message = "Workflow status cannot be blank")
    @Size(min = 1, max = 50, message = "Workflow status must be between 1 and 50 characters")
    private String workflowStatus;
}