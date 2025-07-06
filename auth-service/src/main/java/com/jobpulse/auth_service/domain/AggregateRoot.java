package com.jobpulse.auth_service.domain;

import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for domain entities that can raise domain events.
 */
@Getter
public abstract class AggregateRoot {
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected void raiseEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public void clearEvents() {
        domainEvents.clear();
    }

    public boolean hasEvents() {
        return !domainEvents.isEmpty();
    }
}
