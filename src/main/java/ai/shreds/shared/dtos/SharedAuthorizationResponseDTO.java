package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for responses from the authorization service.
 * Contains the result of an authorization check.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedAuthorizationResponseDTO {
    
    /** 
     * Indicates if the requested action is authorized 
     */
    @NotNull(message = "Authorization indicator cannot be null")
    private Boolean isAuthorized;
    
    /** 
     * User level granted by the authorization service (e.g., SUPERVISOR, MANAGER, DIRECTOR) 
     */
    @Size(max = 50, message = "User level cannot exceed 50 characters")
    private String userLevel;
}