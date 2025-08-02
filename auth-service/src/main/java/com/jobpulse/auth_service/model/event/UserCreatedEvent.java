package com.jobpulse.auth_service.model.event;

import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder
public record UserCreatedEvent (
    UUID eventId,
    EventType eventType,
    ZonedDateTime createdAt,
    UUID aggregateId,
    UserCreatedPayload payload
) implements DomainEventInterface<UserCreatedEvent, UserCreatedPayload>{
    public static UserCreatedEventBuilder builder() {
        return new UserCreatedEventBuilder()
                .eventType(EventType.USER_CREATED)
                .createdAt(ZonedDateTime.now());
    }
}

