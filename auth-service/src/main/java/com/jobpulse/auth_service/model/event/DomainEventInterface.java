package com.jobpulse.auth_service.model.event;

import java.time.ZonedDateTime;
import java.util.UUID;

public interface DomainEventInterface<E, P> {
    UUID eventId();
    EventType eventType();
    ZonedDateTime createdAt();
    UUID aggregateId();
    P payload();
}
