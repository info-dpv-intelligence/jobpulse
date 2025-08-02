package com.jobpulse.auth_service.service.module.event.broker.kafka;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.jobpulse.auth_service.model.event.DomainEventInterface;
import com.jobpulse.auth_service.model.event.UserCreatedEvent;
import com.jobpulse.auth_service.model.event.UserCreatedPayload;
import com.jobpulse.auth_service.service.module.event.broker.EventBrokerContract;   
import com.jobpulse.auth_service.service.module.event.broker.model.PublishEventsCommand;

@Service
public class KafkaEventBrokerService implements EventBrokerContract {

    @Value("${user.events.topic:user.events}")
    private String userEventsTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaEventBrokerService(
        KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishUserRegisterdEvents(PublishEventsCommand<UserCreatedEvent, UserCreatedPayload> command) {
        List<DomainEventInterface<UserCreatedEvent, UserCreatedPayload>> events = command.getEvents();
        
        if (events != null && !events.isEmpty()) {
            for (DomainEventInterface<UserCreatedEvent, UserCreatedPayload> event : events) {
                kafkaTemplate.send(userEventsTopic, event.payload().email(), event.payload());
            }
        }
    }
}
