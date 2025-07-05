package com.jobpulse.job_service.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent {
    private EventType eventType;
    private String userId;
    private String email;

    public enum EventType {
        CREATED, UPDATED
    }

    public static UserEvent created(String userId, String email) {
        return new UserEvent(EventType.CREATED, userId, email);
    }

    public static UserEvent updated(String userId, String email) {
        return new UserEvent(EventType.UPDATED, userId, email);
    }
}
