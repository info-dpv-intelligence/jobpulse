package com.jobpulse.auth_service.dto.request;

import com.jobpulse.auth_service.model.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Schema(description = "Action for registering a new user")
@Value
@Builder
public class RegisteringUserAction {
    @Schema(description = "User's email address", example = "user@example.com")
    String email;

    @Schema(description = "User's encoded password")
    String encodedPassword;

    @Schema(description = "User's role in the system")
    UserRole role;
}
