package com.jobpulse.auth_service.events;

import lombok.Getter;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserRegisteredEvent implements DomainEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    private final String userId;
    private final String email;
    private final String role;

    public UserRegisteredEvent(String userId, String email, String role) {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    @Override
    public String getEventType() {
        return "UserRegistered";
    }

    @Override
    public String getAggregateId() {
        return userId;
    }
}
