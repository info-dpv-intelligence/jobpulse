package com.jobpulse.auth_service.service;

import com.jobpulse.auth_service.domain.AggregateRoot;
import com.jobpulse.auth_service.domain.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DomainEventService {
    
    private static final Logger logger = LoggerFactory.getLogger(DomainEventService.class);
    
    @Autowired
    private DomainEventPublisher domainEventPublisher;
    
    public void publishEventsAndClear(AggregateRoot aggregate) {
        if (aggregate == null || !aggregate.hasEvents()) {
            return;
        }
        
        publishEventsAndClear(Arrays.asList(aggregate));
    }
    
    public void publishEventsAndClear(AggregateRoot... aggregates) {
        publishEventsAndClear(Arrays.asList(aggregates));
    }
    
    public void publishEventsAndClear(List<AggregateRoot> aggregates) {
        if (aggregates == null || aggregates.isEmpty()) {
            return;
        }
        
        List<AggregateRoot> aggregatesWithEvents = aggregates.stream()
            .filter(aggregate -> aggregate != null && aggregate.hasEvents())
            .toList();
            
        if (aggregatesWithEvents.isEmpty()) {
            return;
        }
        
        try {
            for (AggregateRoot aggregate : aggregatesWithEvents) {
                publishEventsFromAggregate(aggregate);
            }
            
            logger.debug("Successfully published domain events from {} aggregates", 
                        aggregatesWithEvents.size());
            
        } catch (Exception e) {
            logger.error("Failed to publish domain events from aggregates", e);
            throw new RuntimeException("Domain event publishing failed", e);
        }
    }
    
    private void publishEventsFromAggregate(AggregateRoot aggregate) {
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
    
    public void publishEventsOnly(AggregateRoot... aggregates) {
        publishEventsOnly(Arrays.asList(aggregates));
    }
    
    public void publishEventsOnly(List<AggregateRoot> aggregates) {
        if (aggregates == null || aggregates.isEmpty()) {
            return;
        }
        
        List<AggregateRoot> aggregatesWithEvents = aggregates.stream()
            .filter(aggregate -> aggregate != null && aggregate.hasEvents())
            .toList();
            
        if (aggregatesWithEvents.isEmpty()) {
            return;
        }
        
        try {
            for (AggregateRoot aggregate : aggregatesWithEvents) {
                for (DomainEvent event : aggregate.getDomainEvents()) {
                    domainEventPublisher.publish(event);
                }
            }
            
            logger.debug("Successfully published domain events from {} aggregates (without clearing)", 
                        aggregatesWithEvents.size());
            
        } catch (Exception e) {
            logger.error("Failed to publish domain events from aggregates", e);
            throw new RuntimeException("Domain event publishing failed", e);
        }
    }
    
    @Async("domainEventExecutor")
    public void publishEventsAndClearAsync(AggregateRoot aggregate) {
        if (aggregate == null || !aggregate.hasEvents()) {
            return;
        }
        
        try {
            List<DomainEvent> events = List.copyOf(aggregate.getDomainEvents());
            for (DomainEvent event : events) {
                domainEventPublisher.publish(event);
            }
            aggregate.clearEvents();
            logger.debug("Successfully published {} domain events asynchronously", events.size());
        } catch (Exception e) {
            logger.error("Failed to publish domain events asynchronously", e);
        }
    }
}
