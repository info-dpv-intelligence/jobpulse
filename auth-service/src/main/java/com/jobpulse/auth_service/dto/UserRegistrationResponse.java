package com.jobpulse.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Response DTO for successful user registration.
 */
@Schema(description = "Response returned after successful user registration")
@Getter
@AllArgsConstructor
public class UserRegistrationResponse {
    
    @Schema(description = "Unique identifier of the newly created user", example = "123e4567-e89b-12d3-a456-426614174000")
    private final String userId;
    
    @Schema(description = "Success message", example = "User registered successfully")
    private final String message;
    
    public static UserRegistrationResponse success(String userId) {
        return new UserRegistrationResponse(userId, "User registered successfully");
    }
}
