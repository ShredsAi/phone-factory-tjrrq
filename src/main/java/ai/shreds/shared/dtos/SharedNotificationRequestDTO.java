package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;

/**
 * DTO for notification requests to the notification service.
 * Used to send notifications to users via various channels.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedNotificationRequestDTO {
    
    /** 
     * Recipient identifier (user ID, email, phone number, etc.) 
     */
    @NotBlank(message = "Recipient ID cannot be blank")
    @Size(min = 1, max = 100, message = "Recipient ID must be between 1 and 100 characters")
    private String recipientId;
    
    /** 
     * Notification channel (EMAIL, SMS, PUSH, etc.) 
     */
    @NotBlank(message = "Channel cannot be blank")
    @Size(min = 1, max = 20, message = "Channel must be between 1 and 20 characters")
    private String channel;
    
    /** 
     * Template identifier for the notification 
     */
    @NotBlank(message = "Template ID cannot be blank")
    @Size(min = 1, max = 100, message = "Template ID must be between 1 and 100 characters")
    private String templateId;
    
    /** 
     * Template parameters for dynamic content 
     */
    private Map<String, String> params;
}