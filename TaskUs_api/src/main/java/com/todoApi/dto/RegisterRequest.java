package com.todoApi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RegisterRequest - Data Transfer Object for registration endpoint
 * 
 * This defines what data the client must send when creating an account
 * 
 * Example JSON:
 * {
 * "username": "john_doe",
 * "email": "john@example.com",
 * "password": "password123"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /**
     * Desired username
     * Must be unique in the system
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    /**
     * Email address
     * Must be unique, will be used for login
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * Plain text password
     * Will be hashed before storing
     * Should enforce minimum length/complexity in real apps
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
}