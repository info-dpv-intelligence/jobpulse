package com.jobpulse.common_events.model;

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
    private String role;

    public enum EventType {
        REGISTERED,
        UPDATED,
        DELETED
    }

    public static UserEvent registered(String userId, String email, String role) {
        return new UserEvent(EventType.REGISTERED, userId, email, role);
    }

    public static UserEvent updated(String userId, String email, String role) {
        return new UserEvent(EventType.UPDATED, userId, email, role);
    }
    
    public static UserEvent deleted(String userId, String email, String role) {
        return new UserEvent(EventType.DELETED, userId, email, role);
    }
}