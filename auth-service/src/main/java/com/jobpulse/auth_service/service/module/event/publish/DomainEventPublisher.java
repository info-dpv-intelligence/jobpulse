package com.jobpulse.auth_service.service.module.event.publish;

import com.jobpulse.auth_service.domain.DomainEvent;
import com.jobpulse.auth_service.domain.UserRegisteredEvent;
import com.jobpulse.auth_service.events.UserEvent;
import com.jobpulse.auth_service.logging.LoggingConfig;
import com.jobpulse.auth_service.logging.LoggingConstants;
import com.jobpulse.auth_service.logging.StructuredLogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

// TODO: review the publish switch case statements
@Service
public class DomainEventPublisher {
    
    private final StructuredLogger logger;

    private UserEventProducer userEventProducer;

    @Autowired
    public DomainEventPublisher(
        UserEventProducer userEventProducer,
        LoggingConfig.LoggerFactory loggerFactory
    ) {
        this.userEventProducer = userEventProducer;
        this.logger = loggerFactory.getLogger(DomainEventPublisher.class);
    }

    public void publish(DomainEvent domainEvent) {
        try {
            switch (domainEvent.getEventType()) {
                case "UserRegistered":
                    publishUserRegisteredEvent((UserRegisteredEvent) domainEvent);
                    break;
                default:
                    Map<String, Object> context = new HashMap<>();
                    context.put("unknownEventType", domainEvent.getEventType());
                    logger.warn(
                        LoggingConstants.EVENT_ERROR,
                        "Unknown domain event type: " + domainEvent.getEventType(),
                        context
                    );
            }
        } catch (Exception e) {
            Map<String, Object> errorContext = new HashMap<>();
            errorContext.put("eventType", domainEvent.getEventType());
            logger.error(
                LoggingConstants.EVENT_ERROR,
                LoggingConstants.ERROR_SYSTEM,
                "Failed to publish domain event: " + domainEvent.getEventType(),
                e,
                errorContext
            );
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
        
        Map<String, Object> context = new HashMap<>();
        context.put(LoggingConstants.USER_ID, domainEvent.getUserId());
        context.put("email", domainEvent.getEmail());
        context.put("role", domainEvent.getRole());
        logger.info(
            LoggingConstants.EVENT_DOMAIN_EVENT_PUBLISHED,
            "Published UserRegistered event for user: " + domainEvent.getUserId(),
            context
        );
    }
}
