package com.todoApi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtUtil - Utility class for JWT token operations
 * 
 * Responsibilities:
 * - Generate JWT tokens when user logs in
 * - Extract information from tokens (like userId)
 * - Validate tokens (check signature, expiration)
 */
@Component
public class JwtUtil {

    /**
     * Secret key for signing tokens
     * This is loaded from application.properties
     * MUST be kept secret - never expose this!
     * 
     * In production, use environment variables or key management service
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Token expiration time in milliseconds
     * Default: 24 hours (86400000 ms)
     * After this time, token becomes invalid
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Generate a JWT token for a user
     * 
     * @param userId   The user's ID to embed in the token
     * @param username The user's username to embed in the token
     * @return A signed JWT token string
     * 
     *         Token structure:
     *         HEADER.PAYLOAD.SIGNATURE
     * 
     *         Payload contains:
     *         - sub (subject): userId
     *         - username: username
     *         - iat (issued at): timestamp
     *         - exp (expiration): timestamp
     */
    public String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        return createToken(claims, userId.toString());
    }

    /**
     * Create the actual JWT token
     * 
     * @param claims  Custom data to include in the token
     * @param subject The main subject (userId as string)
     * @return Signed JWT token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims) // Add custom data
                .setSubject(subject) // Set subject (userId)
                .setIssuedAt(now) // Set creation time
                .setExpiration(expirationDate) // Set expiration time
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Sign with secret
                .compact(); // Build the token
    }

    /**
     * Extract userId from token
     * 
     * @param token JWT token string
     * @return The userId embedded in the token
     */
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    /**
     * Extract username from token
     * 
     * @param token JWT token string
     * @return The username embedded in the token
     */
    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("username", String.class);
    }

    /**
     * Extract expiration date from token
     * 
     * @param token JWT token string
     * @return The expiration date
     */
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /**
     * Extract all claims (data) from token
     * 
     * This verifies the signature and decodes the payload
     * If signature is invalid, this will throw an exception
     * 
     * @param token JWT token string
     * @return Claims object containing all token data
     */
    private Claims extractAllClaims(String token) {
        // Use the parser() entrypoint available in the project's JJWT API
        // and pass the `Key` to `setSigningKey` for verification.
        return Jwts.parser()
                .setSigningKey(getSigningKey()) // Use secret Key to verify signature
                .build()
                .parseClaimsJws(token) // Parse and verify
                .getBody(); // Get the payload
    }

    /**
     * Check if token has expired
     * 
     * @param token JWT token string
     * @return true if expired, false if still valid
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate a JWT token
     * 
     * Checks:
     * 1. Token is not expired
     * 2. Signature is valid (done automatically in extractAllClaims)
     * 
     * @param token JWT token string
     * @return true if valid, false if invalid/expired
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            // Token is malformed, signature invalid, or other error
            return false;
        }
    }

    /**
     * Get the signing key from the secret
     * 
     * Converts the secret string into a cryptographic key
     * This key is used to sign and verify tokens
     * 
     * @return Signing key
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}