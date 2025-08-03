package com.jobpulse.auth_service.service.module.event;

import com.jobpulse.auth_service.model.event.DomainEventInterface;

import java.util.List;


public interface BaseDomainEventLayerContract<E, P> {
    void raiseEvent(DomainEventInterface<P> event);
    void clearEvents();
    boolean hasEvents();
    List<DomainEventInterface<P>> domainEvents();
    void clearDomainEvents();
    void rollback();
}
