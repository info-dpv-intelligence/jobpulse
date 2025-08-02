package com.jobpulse.auth_service.service.module.event;

import com.jobpulse.auth_service.dto.request.RegisteringUserAction;
import com.jobpulse.auth_service.model.User;
import com.jobpulse.auth_service.model.event.DomainEventInterface;
import com.jobpulse.auth_service.model.event.UserCreatedEvent;
import com.jobpulse.auth_service.model.event.UserCreatedPayload;
import com.jobpulse.auth_service.service.module.event.broker.EventBrokerContract;
import com.jobpulse.auth_service.service.module.event.broker.model.PublishEventsCommand;
import com.jobpulse.auth_service.service.module.event.broker.model.UserRegistrationTransactionCompletedEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserRegistrationDomainLayer implements UserRegistrationDomainLayerContract {

    private final List<DomainEventInterface<UserCreatedEvent, UserCreatedPayload>> events = new ArrayList<>();
    private final EventBrokerContract eventBroker;
    private final ApplicationEventPublisher eventsToPublisher;

    @Autowired
    public UserRegistrationDomainLayer(
        EventBrokerContract eventBroker,
        ApplicationEventPublisher eventsToPublisher
    ) {
        this.eventBroker = eventBroker;
        this.eventsToPublisher = eventsToPublisher;
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
        eventsToPublisher.publishEvent(new UserRegistrationTransactionCompletedEvent());

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
    @Async("domainEventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishEvents(UserRegistrationTransactionCompletedEvent event) {
        if (!hasEvents()) {
            return;
        }

        eventBroker.publishUserRegisterdEvents(
            PublishEventsCommand.<UserCreatedEvent, UserCreatedPayload>builder()
                .events(domainEvents())
                .build()
        );

        clearDomainEvents();
    }

    @Override
    public void rollback() {
        clearDomainEvents();
    }
}
