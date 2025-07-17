package ai.shreds.adapter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import ai.shreds.shared.dtos.SharedErrorResponseDTO;
import ai.shreds.application.exceptions.ApplicationOrderNotFoundException;
import ai.shreds.application.exceptions.ApplicationInvalidApprovalException;
import ai.shreds.application.exceptions.ApplicationWorkflowException;
import ai.shreds.application.exceptions.ApplicationSupplierValidationException;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for the adapter layer.
 * Catches exceptions from controllers and returns standardized error responses.
 */
@Slf4j
@ControllerAdvice
public class AdapterExceptionHandler {

    /**
     * Handles validation exceptions thrown by the adapter layer.
     * 
     * @param ex The validation exception
     * @param request The web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(AdapterValidationException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleValidationException(
            AdapterValidationException ex, WebRequest request) {
        log.error("Adapter validation error: {}", ex.getMessage(), ex);
        
        SharedErrorResponseDTO errorResponse = SharedErrorResponseDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                ex.getMessage()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handles authorization exceptions thrown by the adapter layer.
     * 
     * @param ex The authorization exception
     * @param request The web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(AdapterAuthorizationException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleAuthorizationException(
            AdapterAuthorizationException ex, WebRequest request) {
        log.error("Adapter authorization error: {}", ex.getMessage(), ex);
        
        SharedErrorResponseDTO errorResponse = SharedErrorResponseDTO.of(
                HttpStatus.FORBIDDEN.value(),
                "Authorization Error",
                ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Handles Spring validation errors from @Valid annotations.
     * 
     * @param ex The method argument not valid exception
     * @param request The web request
     * @return ResponseEntity with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Method argument validation error: {}", ex.getMessage(), ex);
        
        List<String> errors = new ArrayList<>();
        
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        
        String errorMessage = errors.isEmpty() ? "Validation failed" : String.join(", ", errors);
        
        SharedErrorResponseDTO errorResponse = SharedErrorResponseDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                errorMessage
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handles constraint violation exceptions from @Validated annotations.
     * 
     * @param ex The constraint violation exception
     * @param request The web request
     * @return ResponseEntity with validation error details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        log.error("Constraint violation error: {}", ex.getMessage(), ex);
        
        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        SharedErrorResponseDTO errorResponse = SharedErrorResponseDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                errorMessage
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handles order not found exceptions from the application layer.
     * 
     * @param ex The order not found exception
     * @param request The web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(ApplicationOrderNotFoundException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleOrderNotFoundException(
            ApplicationOrderNotFoundException ex, WebRequest request) {
        log.error("Order not found error: {}", ex.getMessage(), ex);
        
        SharedErrorResponseDTO errorResponse = SharedErrorResponseDTO.of(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles invalid approval exceptions from the application layer.
     * 
     * @param ex The invalid approval exception
     * @param request The web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(ApplicationInvalidApprovalException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleInvalidApprovalException(
            ApplicationInvalidApprovalException ex, WebRequest request) {
        log.error("Invalid approval error: {}", ex.getMessage(), ex);
        
        SharedErrorResponseDTO errorResponse = SharedErrorResponseDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Approval",
                ex.getMessage()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handles workflow exceptions from the application layer.
     * 
     * @param ex The workflow exception
     * @param request The web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(ApplicationWorkflowException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleWorkflowException(
            ApplicationWorkflowException ex, WebRequest request) {
        log.error("Workflow error: {}", ex.getMessage(), ex);
        
        SharedErrorResponseDTO errorResponse = SharedErrorResponseDTO.of(
                HttpStatus.CONFLICT.value(),
                "Workflow Error",
                ex.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handles supplier validation exceptions from the application layer.
     * 
     * @param ex The supplier validation exception
     * @param request The web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(ApplicationSupplierValidationException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleSupplierValidationException(
            ApplicationSupplierValidationException ex, WebRequest request) {
        log.error("Supplier validation error: {}", ex.getMessage(), ex);
        
        SharedErrorResponseDTO errorResponse = SharedErrorResponseDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                "Supplier Validation Error",
                ex.getMessage()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handles illegal argument exceptions.
     * 
     * @param ex The illegal argument exception
     * @param request The web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        log.error("Illegal argument error: {}", ex.getMessage(), ex);
        
        SharedErrorResponseDTO errorResponse = SharedErrorResponseDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handles all other unhandled exceptions.
     * 
     * @param ex The generic exception
     * @param request The web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<SharedErrorResponseDTO> handleGenericException(
            Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        SharedErrorResponseDTO errorResponse = SharedErrorResponseDTO.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please try again later."
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handles runtime exceptions.
     * 
     * @param ex The runtime exception
     * @param request The web request
     * @return ResponseEntity with error details
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<SharedErrorResponseDTO> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        log.error("Runtime error: {}", ex.getMessage(), ex);
        
        SharedErrorResponseDTO errorResponse = SharedErrorResponseDTO.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Runtime Error",
                "A runtime error occurred. Please try again later."
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}