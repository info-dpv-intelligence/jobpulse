package com.jobpulse.auth_service.service;

import com.jobpulse.auth_service.domain.DomainEvent;
import com.jobpulse.auth_service.domain.UserRegisteredEvent;
import com.jobpulse.auth_service.events.UserEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DomainEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(DomainEventPublisher.class);
    
    @Autowired
    private UserEventProducer userEventProducer;

    public void publish(DomainEvent domainEvent) {
        try {
            switch (domainEvent.getEventType()) {
                case "UserRegistered":
                    publishUserRegisteredEvent((UserRegisteredEvent) domainEvent);
                    break;
                default:
                    logger.warn("Unknown domain event type: {}", domainEvent.getEventType());
            }
        } catch (Exception e) {
            logger.error("Failed to publish domain event: {}", domainEvent.getEventType(), e);
        }
    }
    
    @Async("domainEventExecutor")
    public void publishAsync(DomainEvent domainEvent) {
        publish(domainEvent);
    }
    
    private void publishUserRegisteredEvent(UserRegisteredEvent domainEvent) {
        UserEvent integrationEvent = UserEvent.registered(
            domainEvent.getUserId(),
            domainEvent.getEmail(),
            domainEvent.getRole()
        );
        
        userEventProducer.sendUserEvent(integrationEvent);
        logger.info("Published UserRegistered event for user: {}", domainEvent.getUserId());
    }
}
