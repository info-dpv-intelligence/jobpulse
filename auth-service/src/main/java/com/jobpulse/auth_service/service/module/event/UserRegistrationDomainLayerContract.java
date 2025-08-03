package com.jobpulse.auth_service.service.module.event;

import com.jobpulse.auth_service.dto.request.RegisteringUserAction;
import com.jobpulse.auth_service.model.User;
import com.jobpulse.auth_service.model.event.UserCreatedEvent;
import com.jobpulse.auth_service.model.event.UserCreatedEventPayload;
import com.jobpulse.auth_service.service.module.event.broker.model.UserRegistrationTransactionCompletedEvent;

public interface UserRegistrationDomainLayerContract extends BaseDomainEventLayerContract<UserCreatedEvent, UserCreatedEventPayload> {
    User touchUserForRegistration(RegisteringUserAction action);
    void publishEvents(UserRegistrationTransactionCompletedEvent event);
}
