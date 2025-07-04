package com.jobpulse.auth_service.service;

import com.jobpulse.auth_service.dto.RegisterRequest;
import com.jobpulse.auth_service.dto.LoginRequest;
import com.jobpulse.auth_service.dto.ServiceResult;
import com.jobpulse.auth_service.dto.UserRegistrationResponse;
import com.jobpulse.auth_service.dto.AuthResponse;

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
     * @return service result with registration response or error details
     */
    ServiceResult<UserRegistrationResponse> registerUser(RegisterRequest request);
    
    /**
     * Authenticates a user and returns access and refresh tokens.
     *
     * @param request the login request containing user credentials
     * @return service result with authentication tokens or error details
     */
    ServiceResult<AuthResponse> login(LoginRequest request);
}
