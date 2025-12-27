package com.todoApi.security;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig - Configures Spring Security for JWT authentication
 * 
 * This class defines:
 * 1. Which endpoints are public (no authentication needed)
 * 2. Which endpoints require authentication
 * 3. How to handle authentication (JWT tokens)
 * 4. Password encoding strategy
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        /**
         * Configure HTTP security
         * 
         * This defines the security rules for our API
         * 
         * @param http HttpSecurity object to configure
         * @return Configured SecurityFilterChain
         */
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // CORS must be configured BEFORE CSRF
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // Disable CSRF
                                .csrf(csrf -> csrf.disable())

                                // Configure authorization rules
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/api/auth/**",
                                                                "/h2-console/**",
                                                                "/error")
                                                .permitAll()
                                                .anyRequest().authenticated())

                                // Stateless session management
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // Add JWT filter
                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                // Allow H2 console frames
                http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // Allow both local and production frontends
                configuration.setAllowedOrigins(Arrays.asList(
                                "http://localhost:4200",
                                System.getenv("FRONTEND_URL") != null ? System.getenv("FRONTEND_URL")
                                                : "http://localhost:4200"));

                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setExposedHeaders(Arrays.asList("Authorization"));
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/api/**", configuration);
                return source;
        }

        /**
         * Password encoder bean
         * 
         * BCryptPasswordEncoder uses the BCrypt hashing algorithm
         * - One-way hash (can't reverse it)
         * - Salted (same password produces different hashes)
         * - Configurable strength (number of rounds)
         * 
         * This bean is injected into AuthService
         * 
         * @return PasswordEncoder instance
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}