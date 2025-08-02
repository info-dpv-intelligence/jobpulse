package com.jobpulse.auth_service.service.module.event;

import com.jobpulse.auth_service.dto.request.RegisteringUserAction;
import com.jobpulse.auth_service.model.User;
import com.jobpulse.auth_service.model.event.DomainEventInterface;
import com.jobpulse.auth_service.model.event.UserCreatedEvent;
import com.jobpulse.auth_service.model.event.UserCreatedPayload;
import com.jobpulse.auth_service.service.module.event.broker.EventBrokerContract;
import com.jobpulse.auth_service.service.module.event.broker.model.PublishEventsCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserRegistrationDomainLayer implements UserRegistrationDomainLayerContract {

    private final List<DomainEventInterface<UserCreatedEvent, UserCreatedPayload>> events = new ArrayList<>();
    private final EventBrokerContract eventBroker;

    @Autowired
    public UserRegistrationDomainLayer(
        EventBrokerContract eventBroker
    ) {
        this.eventBroker = eventBroker;
    }

    @Override
    public User touchUserForRegistration(RegisteringUserAction action) {
        User user = User.builder()
            .email(action.getEmail())
            .password(action.getEncodedPassword())
            .role(action.getRole())
            .build();

        DomainEventInterface<UserCreatedEvent, UserCreatedPayload> event = UserCreatedEvent
            .builder()
                .payload(
                    UserCreatedPayload
                        .builder()
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build()
                )
            .build();
    
        raiseEvent(event);

        return user;
    }

    @Override
    public void raiseEvent(DomainEventInterface<UserCreatedEvent, UserCreatedPayload> event) {
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
    public List<DomainEventInterface<UserCreatedEvent, UserCreatedPayload>> domainEvents() {
        return this.events;
    }

    @Override
    public void clearDomainEvents() {
        this.events.clear();
    }

    @Override
    @Async
    @AfterDomainEventPublication
    public void publishEvents() {
        if (!hasEvents()) {
            return;
        }

        eventBroker.publishUserRegisterdEvents(
            PublishEventsCommand.<UserCreatedEvent, UserCreatedPayload>builder()
                .events(domainEvents())
                .build()
        );

        clearEvents();
    }

    @Override
    public void rollback() {
        clearDomainEvents();
    }
}
