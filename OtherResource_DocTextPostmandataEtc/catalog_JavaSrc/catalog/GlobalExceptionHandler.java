package com.ott.catalog.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * CENTRALIZED EXCEPTION HANDLER for Catalog Service.
 *
 * @RestControllerAdvice means this class handles exceptions
 * thrown by ANY controller in this service — in one place.
 *
 * Without this, you'd need try-catch in every controller method.
 * This is the enterprise standard pattern used in projects like Disney.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles: Plan not found (404)
     * Triggered when: GET /catalog/plans/{planId} and plan doesn't exist
     */
    @ExceptionHandler(PlanNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlanNotFoundException(
            PlanNotFoundException ex,
            HttpServletRequest request) {

        log.error("Plan not found: {}", ex.getPlanId());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles: Validation errors (400)
     * Triggered when: Request body fails @NotNull, @NotBlank etc validation
     * Example: POST /catalog/plans with missing planName
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException ex) {

        log.error("Validation failed: {}", ex.getMessage());

        // Collect all field validation errors into a map
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handles: Any unexpected exception (500)
     * This is the safety net — catches anything not handled above
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Something went wrong. Please try again later.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
