package com.jobpulse.auth_service.events;

import lombok.Getter;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import java.util.ArrayList;
import java.util.List;

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

    @DomainEvents
    public List<DomainEvent> domainEvents() {
        return new ArrayList<>(domainEvents);
    }

    @AfterDomainEventPublication
    public void clearDomainEvents() {
        clearEvents();
    }
}
