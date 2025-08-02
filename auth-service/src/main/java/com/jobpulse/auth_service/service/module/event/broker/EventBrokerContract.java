package com.jobpulse.auth_service.service.module.event.broker;

import com.jobpulse.auth_service.model.event.UserCreatedEvent;
import com.jobpulse.auth_service.model.event.UserCreatedPayload;
import com.jobpulse.auth_service.service.module.event.broker.model.PublishEventsCommand;

public interface EventBrokerContract {
    void publishUserRegisterdEvents(PublishEventsCommand<UserCreatedEvent, UserCreatedPayload> command);
}
