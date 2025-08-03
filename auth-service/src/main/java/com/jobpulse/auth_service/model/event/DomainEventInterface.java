package com.jobpulse.auth_service.model.event;

import java.time.ZonedDateTime;

public interface DomainEventInterface<P> {
    EventType eventType();
    ZonedDateTime createdAt();
    P payload();
}
