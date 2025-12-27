package com.todoApi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AuthResponse - Response sent after successful login
 * 
 * Contains the JWT token and user information
 * The client will store this token and include it in future requests
 * 
 * Example JSON response:
 * {
 * "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 * "userId": 1,
 * "username": "john_doe",
 * "email": "john@example.com"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /**
     * JWT token - The "backstage pass"
     * Client includes this in Authorization header for all subsequent requests
     */
    private String token;

    /**
     * User ID - Useful for the client to store
     */
    private Long userId;

    /**
     * Username - For display purposes
     */
    private String username;

    /**
     * Email - For display purposes
     */
    private String email;

    /**
     * Note: We DON'T include the password!
     * Never send passwords back to the client, even hashed
     */
}