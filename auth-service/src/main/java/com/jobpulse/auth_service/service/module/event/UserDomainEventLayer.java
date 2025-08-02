package com.jobpulse.auth_service.service.module.event;

import com.jobpulse.auth_service.dto.RegisteringUserAction;
import com.jobpulse.auth_service.model.User;
import com.jobpulse.auth_service.model.event.DomainEvent;
import com.jobpulse.auth_service.model.event.UserCreatedEvent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDomainEventLayer implements UserDomainEventLayerContract {
    private final List<DomainEvent> events = new ArrayList<>();

    @Override
    public User registeringUser(RegisteringUserAction action) {
        User user = User.builder()
            .email(action.getEmail())
            .password(action.getEncodedPassword())
            .role(action.getRole())
            .build();

        raiseEvent(UserCreatedEvent.from(user));
        return user;
    }

    @Override
    public void raiseEvent(DomainEvent event) {
        this.events.add(event);
    }

    @Override
    public void clearEvents() {
        this.events.clear();
    }

    @Override
    public boolean hasEvents() {
        return !this.events.isEmpty();
    }

    @Override
    public List<DomainEvent> domainEvents() {
        return new ArrayList<>(this.events);
    }

    @Override
    public void clearDomainEvents() {
        this.events.clear();
    }

    @Override
    public void publishEvents() {
        if (!hasEvents()) {
            return;
        }

        List<DomainEvent> eventsToPublish = new ArrayList<>(this.events);
        clearEvents();

        eventsToPublish.forEach(event -> {
            // TODO: Implement event publishing mechanism (e.g., Spring Events, Message Queue)
        });
    }

    @Override
    public void rollback() {
        clearDomainEvents();
    }
}
