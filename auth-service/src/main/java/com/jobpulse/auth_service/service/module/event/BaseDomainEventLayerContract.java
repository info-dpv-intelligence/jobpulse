package com.jobpulse.auth_service.service.module.event;

import com.jobpulse.auth_service.model.event.DomainEventInterface;
import java.util.List;


public interface BaseDomainEventLayerContract<E, P, T> {
    void raiseEvent(DomainEventInterface<E, P> event);
    void clearEvents();
    boolean hasEvents();
    List<DomainEventInterface<E, P>> domainEvents();
    void clearDomainEvents();
    void publishEvents(T event);
    void rollback();
}
