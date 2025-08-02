package com.jobpulse.auth_service.model.event;

import com.jobpulse.auth_service.model.User;
import lombok.Getter;

@Getter
public class UserCreatedEvent extends DomainEvent {
    private final String userId;
    private final String email;

    public UserCreatedEvent(User user) {
        super(EventType.USER_CREATED);
        this.userId = user.getId().toString();
        this.email = user.getEmail();
    }

    public static UserCreatedEvent from(User user) {
        return new UserCreatedEvent(user);
    }
}
