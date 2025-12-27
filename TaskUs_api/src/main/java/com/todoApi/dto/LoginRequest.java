package com.todoApi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginRequest - Data Transfer Object for login endpoint
 * 
 * This defines what data the client must send when logging in
 * 
 * Example JSON:
 * {
 * "email": "john@example.com",
 * "password": "password123"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * User's email address
     * We'll use email for login (instead of username)
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * User's plain text password
     * This will be sent over HTTPS (encrypted in transit)
     * Server will hash it and compare with stored hash
     */
    @NotBlank(message = "Password is required")
    private String password;
}