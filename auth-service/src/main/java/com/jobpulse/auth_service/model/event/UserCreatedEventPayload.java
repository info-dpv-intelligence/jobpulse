package com.jobpulse.auth_service.model.event;

import com.jobpulse.auth_service.model.UserRole;

import lombok.Builder;

@Builder
public record UserCreatedEventPayload(
    String email,
    UserRole role,
    EventType eventType
) {
    public static UserCreatedEventPayloadBuilder builder() {
        return new UserCreatedEventPayloadBuilder().eventType(
            EventType.USER_CREATED
        );
    }
}
