package com.jobpulse.auth_service.service;

import com.jobpulse.auth_service.dto.RegisterRequest;
import com.jobpulse.auth_service.dto.LoginRequest;
import org.springframework.http.ResponseEntity;

/**
 * Service contract interface for user management operations.
 * Defines the contract for user authentication functionality including
 * user registration and login operations.
 */
public interface UserServiceContract {
    
    /**
     * Registers a new user in the system.
     *
     * @param request the registration request containing user details
     * @return response entity with success message or error details
     */
    ResponseEntity<?> registerUser(RegisterRequest request);
    
    /**
     * Authenticates a user and returns access and refresh tokens.
     *
     * @param request the login request containing user credentials
     * @return response entity with authentication tokens or error details
     */
    ResponseEntity<?> login(LoginRequest request);
}
