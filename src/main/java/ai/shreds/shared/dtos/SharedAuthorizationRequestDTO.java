package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;

/**
 * DTO for authorization request to the authorization service.
 * Used to verify if a user has permission to perform specific actions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedAuthorizationRequestDTO {
    
    /** 
     * User identifier requesting authorization 
     */
    @NotBlank(message = "User ID cannot be blank")
    @Size(min = 1, max = 50, message = "User ID must be between 1 and 50 characters")
    private String userId;
    
    /** 
     * Resource being accessed 
     */
    @NotBlank(message = "Resource cannot be blank")
    @Size(min = 1, max = 100, message = "Resource must be between 1 and 100 characters")
    private String resource;
    
    /** 
     * Action being performed 
     */
    @NotBlank(message = "Action cannot be blank")
    @Size(min = 1, max = 50, message = "Action must be between 1 and 50 characters")
    private String action;
    
    /** 
     * Additional context for authorization decision 
     */
    private Map<String, Object> context;
}