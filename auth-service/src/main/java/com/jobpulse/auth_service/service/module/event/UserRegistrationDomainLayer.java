package com.jobpulse.auth_service.service.module.event;

import com.jobpulse.auth_service.dto.request.RegisteringUserAction;
import com.jobpulse.auth_service.model.User;
import com.jobpulse.auth_service.model.event.DomainEventInterface;
import com.jobpulse.auth_service.model.event.UserCreatedEvent;
import com.jobpulse.auth_service.model.event.UserCreatedEventPayload;
import com.jobpulse.auth_service.service.module.event.broker.EventBrokerContract;
import com.jobpulse.auth_service.service.module.event.broker.model.PublishEventsCommand;
import com.jobpulse.auth_service.service.module.event.broker.model.UserRegistrationTransactionCompletedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final List<DomainEventInterface<UserCreatedEventPayload>> events = new ArrayList<>();
    private final EventBrokerContract eventBroker;
    private final ApplicationEventPublisher eventsToPublisher;
    private final Logger logger = LoggerFactory.getLogger(getClass());

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

        DomainEventInterface<UserCreatedEventPayload> event = UserCreatedEvent
            .builder()
                .payload(
                    UserCreatedEventPayload
                        .builder()
                            .email(user.getEmail())
                            .role(user.getRole())
                        .build()
                )
            .build();
        raiseEvent(event);
        try {
            eventsToPublisher.publishEvent(new UserRegistrationTransactionCompletedEvent());
        } catch (Exception ex) {
            logger.error("Error while publising the UserRegistrationTransactionCompletedEvent", ex);
        }

        return user;
    }

    @Override
    public void raiseEvent(DomainEventInterface<UserCreatedEventPayload> event) {
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
    public List<DomainEventInterface<UserCreatedEventPayload>> domainEvents() {
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
            PublishEventsCommand.<UserCreatedEventPayload>builder()
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
