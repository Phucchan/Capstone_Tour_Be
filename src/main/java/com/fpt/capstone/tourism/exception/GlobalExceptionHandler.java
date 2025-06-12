package com.fpt.capstone.tourism.exception;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles generic system exceptions.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<GeneralResponse<Object>> handleOverallException(Exception ex) {
        log.error("Error happened", ex);
        return ResponseEntity.internalServerError().body(GeneralResponse.of(ex));
    }

    /**
     * Handles custom business exceptions.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<GeneralResponse<Object>> handleBusinessException(BusinessException be) {
        log.error("Business error happened", be);
        return ResponseEntity.status(be.getHttpCode()).body(GeneralResponse.of(be));
    }
    /**
     * Handles invalid request parameters and method argument validation errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<GeneralResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation failed: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(GeneralResponse.of(errors, "Validation failed"));
    }
    /**
     * Handles access denied (authorization) errors.
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<GeneralResponse<String>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(GeneralResponse.of("You do not have permission to perform this action.", "Forbidden"));
    }
    /**
     * Handles invalid request parameters (e.g., incorrect data types).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<GeneralResponse<String>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(GeneralResponse.of("Invalid request parameters: " + ex.getMessage(), "Bad Request"));
    }

}
