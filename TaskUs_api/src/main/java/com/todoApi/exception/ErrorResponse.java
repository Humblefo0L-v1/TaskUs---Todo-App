package com.todoApi.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ErrorResponse - Standard format for error responses
 * 
 * This provides a consistent error structure across the API
 * 
 * Example response:
 * {
 * "timestamp": "2025-12-17T10:30:00",
 * "status": 400,
 * "error": "Bad Request",
 * "message": "Validation failed",
 * "path": "/api/todos",
 * "errors": {
 * "title": "Title is required",
 * "description": "Description is too long"
 * }
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * Timestamp when the error occurred
     */
    private LocalDateTime timestamp;

    /**
     * HTTP status code (400, 401, 404, 500, etc.)
     */
    private int status;

    /**
     * HTTP status text ("Bad Request", "Unauthorized", etc.)
     */
    private String error;

    /**
     * Human-readable error message
     */
    private String message;

    /**
     * The API endpoint where the error occurred
     */
    private String path;

    /**
     * Optional: Field-specific validation errors
     * Key = field name, Value = error message
     * 
     * Example: {"email": "Email is required", "password": "Password too short"}
     */
    private Map<String, String> errors;

    /**
     * Constructor without field-specific errors
     */
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}