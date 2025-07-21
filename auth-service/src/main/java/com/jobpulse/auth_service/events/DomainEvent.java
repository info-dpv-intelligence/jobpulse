package com.jobpulse.auth_service.events;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {
    UUID getEventId();
    Instant getOccurredOn();
    String getEventType();
    String getAggregateId();
}
