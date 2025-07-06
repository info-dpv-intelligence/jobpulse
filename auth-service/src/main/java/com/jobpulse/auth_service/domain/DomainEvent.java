package com.jobpulse.auth_service.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Base interface for all domain events.
 */
public interface DomainEvent {
    UUID getEventId();
    Instant getOccurredOn();
    String getEventType();
    String getAggregateId();
}
