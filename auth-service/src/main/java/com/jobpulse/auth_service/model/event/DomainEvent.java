package com.jobpulse.auth_service.model.event;

import lombok.Getter;
import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class DomainEvent {
    private final UUID eventId;
    private final EventType eventType;
    private final Instant occurredOn;

    protected DomainEvent(EventType eventType) {
        this.eventId = UUID.randomUUID();
        this.eventType = eventType;
        this.occurredOn = Instant.now();
    }
}
