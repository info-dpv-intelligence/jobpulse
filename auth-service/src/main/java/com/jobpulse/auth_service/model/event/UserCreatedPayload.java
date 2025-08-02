package com.jobpulse.auth_service.model.event;

import com.jobpulse.auth_service.model.UserRole;

import lombok.Builder;

@Builder
public record UserCreatedPayload(
    String email,
    UserRole role
) {
}
