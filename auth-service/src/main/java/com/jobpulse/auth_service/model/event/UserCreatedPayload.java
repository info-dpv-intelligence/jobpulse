package com.jobpulse.auth_service.model.event;

import com.jobpulse.auth_service.model.UserRole;

import lombok.Builder;

@Builder
public record UserCreatedPayload(
    String email,
    UserRole role,
    EventType eventType
) {
    public static UserCreatedPayloadBuilder builder() {
        return UserCreatedPayload.builder().eventType(
            EventType.USER_CREATED
        );
    }
}
