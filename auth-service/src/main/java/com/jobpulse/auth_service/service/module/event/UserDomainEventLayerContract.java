package com.jobpulse.auth_service.service.module.event;

import com.jobpulse.auth_service.dto.RegisteringUserAction;
import com.jobpulse.auth_service.model.User;
import com.jobpulse.auth_service.model.event.DomainEvent;

import java.util.List;

public interface UserDomainEventLayerContract {
    User registeringUser(RegisteringUserAction action);

    void raiseEvent(DomainEvent event);

    void clearEvents();

    boolean hasEvents();

    List<DomainEvent> domainEvents();

    void clearDomainEvents();

    void publishEvents();

    void rollback();
}
