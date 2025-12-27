package com.todoApi.service;

import com.todoApi.dto.AuthResponse;
import com.todoApi.dto.LoginRequest;
import com.todoApi.dto.RegisterRequest;
import com.todoApi.model.User;
import com.todoApi.repository.UserRepository;
import com.todoApi.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthService - Handles user registration and authentication
 * 
 * Responsibilities:
 * - Register new users (with password hashing)
 * - Authenticate users during login
 * - Generate JWT tokens for authenticated users
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Register a new user
     * 
     * Business Logic:
     * 1. Check if username already exists
     * 2. Check if email already exists
     * 3. Hash the password (NEVER store plain text!)
     * 4. Create and save the user
     * 5. Generate JWT token
     * 6. Return token and user info
     * 
     * @param request Registration data (username, email, password)
     * @return AuthResponse with JWT token and user details
     * @throws RuntimeException if username or email already exists
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Step 1: Validate username is available
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }

        // Step 2: Validate email is available
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        // Step 3: Create new user entity
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // Step 4: Hash the password before storing
        // CRITICAL: Never store plain text passwords!
        // passwordEncoder uses BCrypt - one-way hashing with salt
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashedPassword);

        // Step 5: Save user to database
        // createdAt is automatically set by @CreationTimestamp
        user = userRepository.save(user);

        // Step 6: Generate JWT token for immediate login
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // Step 7: Return response with token and user info
        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail());
    }

    /**
     * Authenticate a user and generate JWT token
     * 
     * Business Logic:
     * 1. Find user by email
     * 2. Verify password matches
     * 3. Generate JWT token
     * 4. Return token and user info
     * 
     * @param request Login credentials (email, password)
     * @return AuthResponse with JWT token and user details
     * @throws RuntimeException if email not found or password incorrect
     */
    public AuthResponse login(LoginRequest request) {
        // Step 1: Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Step 2: Verify password
        // passwordEncoder.matches() hashes the input and compares with stored hash
        // This is secure - we never decrypt the stored password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Step 3: Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // Step 4: Return response
        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail());
    }

    /**
     * Validate if a token is still valid
     * 
     * This can be used for token refresh endpoints
     * or to verify tokens before processing requests
     * 
     * @param token JWT token to validate
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    /**
     * Get user ID from token
     * 
     * Useful for extracting the authenticated user's ID
     * from the JWT token in requests
     * 
     * @param token JWT token
     * @return User ID embedded in the token
     */
    public Long getUserIdFromToken(String token) {
        return jwtUtil.extractUserId(token);
    }
}