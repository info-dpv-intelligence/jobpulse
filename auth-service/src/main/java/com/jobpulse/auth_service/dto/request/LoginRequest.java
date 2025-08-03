package com.jobpulse.auth_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "Request payload for user login")
public class LoginRequest {
    @NotBlank
    @Email
    @Schema(description = "User email address", example = "user@example.com")
    String email;

    @NotBlank
    @Schema(description = "User password", example = "password123")
    String password;
}