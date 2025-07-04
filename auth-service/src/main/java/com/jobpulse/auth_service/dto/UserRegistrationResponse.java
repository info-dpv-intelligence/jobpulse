package com.jobpulse.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Response DTO for successful user registration.
 */
@Getter
@AllArgsConstructor
public class UserRegistrationResponse {
    private final String userId;
    private final String message;
    
    public static UserRegistrationResponse success(String userId) {
        return new UserRegistrationResponse(userId, "User registered successfully");
    }
}
