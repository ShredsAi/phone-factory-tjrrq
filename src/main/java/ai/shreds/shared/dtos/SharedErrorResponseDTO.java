package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Standardized error response DTO for REST API error handling.
 * Used across all controllers to provide consistent error responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharedErrorResponseDTO {
    
    /**
     * HTTP status code (e.g., 400, 404, 500)
     */
    @NotNull(message = "Status code cannot be null")
    private Integer status;
    
    /**
     * Error type or code (e.g., "Bad Request", "Not Found", "Validation Error")
     */
    @NotBlank(message = "Error cannot be blank")
    private String error;
    
    /**
     * Detailed error message explaining what went wrong
     */
    @NotBlank(message = "Message cannot be blank")
    private String message;
    
    /**
     * Timestamp when the error occurred (ISO-8601 format)
     */
    @NotBlank(message = "Timestamp cannot be blank")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d{3})?Z$", 
             message = "Timestamp must be in ISO-8601 format")
    private String timestamp;
    
    /**
     * Static builder method to create an error response with the current timestamp.
     *
     * @param status HTTP status code
     * @param error Error type or code
     * @param message Detailed error message
     * @return A new SharedErrorResponseDTO instance
     */
    public static SharedErrorResponseDTO of(Integer status, String error, String message) {
        return new SharedErrorResponseDTO(
            status,
            error,
            message,
            DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        );
    }
}
