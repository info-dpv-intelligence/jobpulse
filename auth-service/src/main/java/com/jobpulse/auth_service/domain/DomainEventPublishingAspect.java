package com.jobpulse.auth_service.domain;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.jobpulse.auth_service.service.module.event.publish.DomainEventPublisher;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
public class DomainEventPublishingAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(DomainEventPublishingAspect.class);
    
    @Autowired
    private DomainEventPublisher domainEventPublisher;
    
    @AfterReturning(value = "@annotation(publishDomainEvents)", returning = "result")
    public void publishDomainEvents(JoinPoint joinPoint, PublishDomainEvents publishDomainEvents, Object result) {
        List<AggregateRoot> aggregates = new ArrayList<>();
        
        try {
            for (Object arg : joinPoint.getArgs()) {
                collectAggregates(arg, aggregates);
            }
            
            collectAggregates(result, aggregates);
            
            if (publishDomainEvents.async()) {
                publishEventsFromAggregatesAsync(aggregates);
            } else {
                publishEventsFromAggregates(aggregates);
            }
            
        } catch (Exception e) {
            logger.error("Failed to publish domain events for method: {}", 
                        joinPoint.getSignature().getName(), e);
            
            if (publishDomainEvents.failOnError()) {
                throw new DomainEventPublishingException("Domain event publishing failed", e);
            }
        }
    }

    private void collectAggregates(Object obj, List<AggregateRoot> aggregates) {
        if (obj == null) {
            return;
        }
        
        if (obj instanceof AggregateRoot) {
            AggregateRoot aggregate = (AggregateRoot) obj;
            if (aggregate.hasEvents()) {
                aggregates.add(aggregate);
            }
            return;
        }
        
        if (obj.getClass().getPackage() != null && 
            obj.getClass().getPackage().getName().contains("com.jobpulse")) {
            
            try {
                Field[] fields = obj.getClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object fieldValue = field.get(obj);
                    collectAggregates(fieldValue, aggregates);
                }
            } catch (Exception e) {
                logger.debug("Could not access field in object of type: {}", obj.getClass().getName());
            }
        }
    }
    
    private void publishEventsFromAggregates(List<AggregateRoot> aggregates) {
        for (AggregateRoot aggregate : aggregates) {
            try {
                for (DomainEvent event : aggregate.getDomainEvents()) {
                    domainEventPublisher.publish(event);
                }
                
                aggregate.clearEvents();
                
            } catch (Exception e) {
                logger.error("Failed to publish events for aggregate: {}", 
                           aggregate.getClass().getSimpleName(), e);
                throw e;
            }
        }
        
        if (!aggregates.isEmpty()) {
            logger.debug("Successfully published domain events from {} aggregates", aggregates.size());
        }
    }
    
    @Async("domainEventExecutor")
    private void publishEventsFromAggregatesAsync(List<AggregateRoot> aggregates) {
        try {
            for (AggregateRoot aggregate : aggregates) {
                List<DomainEvent> events = List.copyOf(aggregate.getDomainEvents());
                
                for (DomainEvent event : events) {
                    domainEventPublisher.publish(event);
                }
                
                aggregate.clearEvents();
            }
            
            if (!aggregates.isEmpty()) {
                logger.debug("Successfully published domain events from {} aggregates asynchronously", aggregates.size());
            }
        } catch (Exception e) {
            logger.error("Failed to publish domain events asynchronously", e);
        }
    }
}
