package com.jobpulse.auth_service.model;

import com.jobpulse.auth_service.domain.AggregateRoot;
import com.jobpulse.auth_service.domain.UserRegisteredEvent;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "app_user")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class User extends AggregateRoot {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    /**
     * Factory method to create a new user.
     */
    public static User register(String email, String password, UserRole role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }

    public void confirmRegistration() {
        if (this.id != null) {
            raiseEvent(new UserRegisteredEvent(
                this.id.toString(), 
                this.email, 
                this.role.name()
            ));
        }
    }
}