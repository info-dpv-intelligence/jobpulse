package com.jobpulse.auth_service.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import io.jsonwebtoken.security.Keys;

@org.springframework.context.annotation.Configuration
public class SecurityConfig {


    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.secret}") String jwtSecret) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/ping", 
                    "/ping/",
                    "/auth/register",
                    "/auth/register/",
                    "/auth/login",
                    "/auth/login/",
                    "/actuator/health",
                    "/actuator/**",
                    // OpenAPI/Swagger UI endpoints
                    "/v3/api-docs/**",
                    "/v3/api-docs.json",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/configuration/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> {})
                .bearerTokenResolver(request -> {
                    // Don't try to resolve bearer tokens for API docs endpoints
                    String requestURI = request.getRequestURI();
                    if (requestURI.startsWith("/v3/api-docs") || 
                        requestURI.startsWith("/swagger-ui") || 
                        requestURI.equals("/swagger-ui.html") ||
                        requestURI.startsWith("/swagger-resources") ||
                        requestURI.startsWith("/webjars") ||
                        requestURI.startsWith("/configuration")) {
                        return null;
                    }
                    
                    // Default behavior for other endpoints
                    String authHeader = request.getHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        return authHeader.substring(7);
                    }
                    return null;
                })
            );
        return http.build();
    }
}