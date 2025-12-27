package com.todoApi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JwtAuthenticationFilter - Intercepts every HTTP request
 * 
 * This filter runs BEFORE your controllers
 * It extracts and validates JWT tokens from request headers
 * 
 * Request Flow:
 * 1. Client sends request with: Authorization: Bearer <token>
 * 2. This filter intercepts the request
 * 3. Extracts the token
 * 4. Validates the token
 * 5. If valid, sets the authentication in Spring Security context
 * 6. Request proceeds to controller
 * 
 * OncePerRequestFilter ensures this runs exactly once per request
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /**
     * This method is called for EVERY request to the server
     * 
     * @param request     The HTTP request
     * @param response    The HTTP response
     * @param filterChain The chain of filters (continue processing)
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Step 1: Extract the Authorization header
        // Example header: "Authorization: Bearer
        // eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        String authHeader = request.getHeader("Authorization");

        String token = null;
        Long userId = null;

        // Step 2: Check if header exists and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extract token (everything after "Bearer ")
            token = authHeader.substring(7);

            try {
                // Step 3: Extract userId from token
                userId = jwtUtil.extractUserId(token);
            } catch (Exception e) {
                // Token is invalid or expired
                // Log the error and continue (request will be unauthorized)
                System.err.println("JWT Token extraction failed: " + e.getMessage());
            }
        }

        // Step 4: If we have a valid userId and no authentication is set yet
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Step 5: Validate the token
            if (jwtUtil.validateToken(token)) {

                // Step 6: Create authentication object
                // This tells Spring Security: "This user is authenticated"
                // We pass userId as the "principal" (the authenticated user)
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userId, // Principal (the authenticated user)
                        null, // Credentials (we don't need password here)
                        new ArrayList<>() // Authorities/Roles (empty for now)
                );

                // Step 7: Set additional details (like IP address, session ID)
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                // Step 8: Set authentication in Security Context
                // Now Spring Security knows this request is authenticated!
                // Controllers can access this to get the userId
                SecurityContextHolder.getContext().setAuthentication(authToken);

                System.out.println("✓ User authenticated: userId=" + userId);
            } else {
                System.err.println("✗ Invalid or expired token");
            }
        }

        // Step 9: Continue with the filter chain
        // This passes the request to the next filter or controller
        filterChain.doFilter(request, response);
    }

    /**
     * Optional: Skip this filter for certain URLs
     * 
     * For example, we don't need to check tokens for:
     * - /api/auth/login
     * - /api/auth/register
     * - /h2-console
     * 
     * These are public endpoints
     * 
     * Uncomment and customize if needed:
     */
    /*
     * @Override
     * protected boolean shouldNotFilter(HttpServletRequest request) {
     * String path = request.getRequestURI();
     * return path.startsWith("/api/auth/") || path.startsWith("/h2-console");
     * }
     */
}