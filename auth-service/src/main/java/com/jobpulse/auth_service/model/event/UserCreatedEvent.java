package com.jobpulse.auth_service.model.event;

import java.time.ZonedDateTime;

import lombok.Builder;

@Builder
public record UserCreatedEvent(
    EventType eventType,
    ZonedDateTime createdAt,
    UserCreatedEventPayload payload
) implements DomainEventInterface<UserCreatedEventPayload> {
    public static UserCreatedEventBuilder builder() {
        return new UserCreatedEventBuilder()
            .eventType(EventType.USER_CREATED)
            .createdAt(ZonedDateTime.now());
    }
}
