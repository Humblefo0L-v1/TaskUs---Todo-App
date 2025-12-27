package com.todoApi.controller;

import com.todoApi.dto.AuthResponse;
import com.todoApi.dto.LoginRequest;
import com.todoApi.dto.RegisterRequest;
import com.todoApi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController - Handles authentication endpoints
 * 
 * Endpoints:
 * - POST /api/auth/register - Create new user account
 * - POST /api/auth/login - Authenticate and get JWT token
 * 
 * These endpoints are PUBLIC (no authentication required)
 * They must be accessible before the user has a token!
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user account
     * 
     * Endpoint: POST /api/auth/register
     * 
     * Request body example:
     * {
     * "username": "john_doe",
     * "email": "john@example.com",
     * "password": "password123"
     * }
     * 
     * Response (201 Created):
     * {
     * "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     * "userId": 1,
     * "username": "john_doe",
     * "email": "john@example.com"
     * }
     * 
     * After registration, user is automatically logged in (receives token)
     * 
     * @param request Registration data
     * @return AuthResponse with JWT token
     * @throws RuntimeException if username/email already exists (handled by
     *                          exception handler)
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);

        // Return 201 Created - a new resource (user) was created
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Login with existing account
     * 
     * Endpoint: POST /api/auth/login
     * 
     * Request body example:
     * {
     * "email": "john@example.com",
     * "password": "password123"
     * }
     * 
     * Response (200 OK):
     * {
     * "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     * "userId": 1,
     * "username": "john_doe",
     * "email": "john@example.com"
     * }
     * 
     * The client should:
     * 1. Store the token (localStorage/sessionStorage)
     * 2. Include it in Authorization header for all future requests:
     * Authorization: Bearer <token>
     * 
     * @param request Login credentials
     * @return AuthResponse with JWT token
     * @throws RuntimeException if credentials invalid (handled by exception
     *                          handler)
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);

        // Return 200 OK - successful authentication
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint - verify auth service is running
     * 
     * Endpoint: GET /api/auth/health
     * 
     * This is useful for testing and monitoring
     * Returns a simple message to confirm the service is up
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Authentication service is running");
    }
}