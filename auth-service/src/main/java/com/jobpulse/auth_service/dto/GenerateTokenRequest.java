package com.jobpulse.auth_service.dto;

import com.jobpulse.auth_service.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request object for generating JWT tokens.
 * Contains only the necessary information needed for token generation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateTokenRequest {
    private UUID userId;
    private UserRole role;
    private String email;
}
