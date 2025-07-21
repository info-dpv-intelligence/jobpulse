package com.jobpulse.auth_service.events;

import com.jobpulse.auth_service.enums.UserEventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent {
    private UserEventType eventType;
    private String userId;
    private String email;
    private String role;

    public static UserEvent registered(String userId, String email, String role) {
        return new UserEvent(UserEventType.USER_REGISTERED, userId, email, role);
    }

    public static UserEvent updated(String userId, String email, String role) {
        return new UserEvent(UserEventType.USER_UPDATED, userId, email, role);
    }
    
    public static UserEvent deleted(String userId, String email, String role) {
        return new UserEvent(UserEventType.USER_DELETED, userId, email, role);
    }
}
