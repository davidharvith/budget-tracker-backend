package com.example.budgettracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler to catch and format errors across the entire application.
 * Provides consistent error response structure for common and unexpected exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles DuplicateResourceException thrown when attempting to create a resource that already exists.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateResource(DuplicateResourceException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles validation errors on DTOs annotated with @Valid.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles access denied exceptions when a user attempts to access a resource without permission.
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(AccessDeniedException ex) {
        return "Forbidden";
    }

    /**
     * Generic fallback for all uncaught exceptions to avoid leaking stack traces in production.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "An unexpected error occurred");
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
