package com.jobpulse.auth_service.dto;

import com.jobpulse.auth_service.model.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "Request payload for user registration")
@Data
public class RegisterRequest {
    
    @Schema(description = "User's email address", example = "user@example.com", required = true)
    @NotBlank
    @Email
    private String email;

    @Schema(description = "User's password", example = "securePassword123", required = true)
    @NotBlank
    private String password;

    @Schema(description = "User's role in the system", example = "JOB_APPLICANT", required = true)
    @NotNull
    private UserRole role;
}