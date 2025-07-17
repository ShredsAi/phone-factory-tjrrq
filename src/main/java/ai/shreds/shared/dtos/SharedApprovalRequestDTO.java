package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for approval decision requests in the procurement workflow.
 * Contains the decision made by an approver along with supporting information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedApprovalRequestDTO {
    
    /** 
     * Decision value: APPROVED, REJECTED, or ESCALATED 
     */
    @NotBlank(message = "Decision cannot be blank")
    @Size(min = 1, max = 50, message = "Decision must be between 1 and 50 characters")
    private String decision;
    
    /** 
     * Approver user identifier - must be a valid employee ID 
     */
    @NotBlank(message = "Approver ID cannot be blank")
    @Size(min = 1, max = 50, message = "Approver ID must be between 1 and 50 characters")
    private String approverId;
    
    /** 
     * Optional comments from the approver explaining their decision 
     */
    @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
    private String comments;
    
    /** 
     * Approval level of the approver: SUPERVISOR, MANAGER, or DIRECTOR 
     */
    @NotBlank(message = "Approval level cannot be blank")
    @Size(min = 1, max = 50, message = "Approval level must be between 1 and 50 characters")
    private String approvalLevel;
}