package com.jobpulse.auth_service.dto.request;

import com.jobpulse.auth_service.model.UserRole;
import lombok.Builder;

@Builder
public record RegisteringUserAction(
    String email,
    String encodedPassword,
    UserRole role
) {
}
