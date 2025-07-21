package com.jobpulse.auth_service.events;

import com.jobpulse.auth_service.events.UserRegisteredEvent;
import com.jobpulse.auth_service.service.module.event.publish.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserRegisteredEventListener {

    private static final Logger logger = LoggerFactory.getLogger(UserRegisteredEventListener.class);
    private final DomainEventPublisher domainEventPublisher;

    @Autowired
    public UserRegisteredEventListener(DomainEventPublisher domainEventPublisher) {
        this.domainEventPublisher = domainEventPublisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegistered(UserRegisteredEvent event) {
        try {
            domainEventPublisher.publish(event);
        } catch (Exception e) {
            logger.error("Failed to publish UserRegisteredEvent for user: {} - Error: {}", 
                        event.getEmail(), e.getMessage(), e);
        }
    }
}
