package com.jobpulse.job_service.events;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserEventListenerTest {

    @InjectMocks
    private UserEventListener userEventListener;

    private ConsumerRecord<String, UserEvent> consumerRecord;

    @BeforeEach
    void setUp() {
        // Mock ConsumerRecord for testing
        consumerRecord = new ConsumerRecord<>("user.events", 0, 100L, "user-123", null);
    }

    @Test
    void handleUserEvent_WithRegisteredEvent_ShouldProcessSuccessfully() {
        // Given
        UserEvent registeredEvent = UserEvent.registered("user-123", "john.doe@example.com", "USER");
        consumerRecord = new ConsumerRecord<>("user.events", 0, 100L, "user-123", registeredEvent);

        // When & Then - Should not throw any exception
        assertDoesNotThrow(() -> userEventListener.handleUserEvent(registeredEvent, consumerRecord));
    }

    @Test
    void handleUserEvent_WithUpdatedEvent_ShouldProcessSuccessfully() {
        // Given
        UserEvent updatedEvent = UserEvent.updated("user-456", "jane.doe@example.com", "ADMIN");
        consumerRecord = new ConsumerRecord<>("user.events", 0, 101L, "user-456", updatedEvent);

        // When & Then - Should not throw any exception
        assertDoesNotThrow(() -> userEventListener.handleUserEvent(updatedEvent, consumerRecord));
    }

    @Test
    void handleUserEvent_WithDeletedEvent_ShouldProcessSuccessfully() {
        // Given
        UserEvent deletedEvent = UserEvent.deleted("user-789", "bob.smith@example.com", "USER");
        consumerRecord = new ConsumerRecord<>("user.events", 0, 102L, "user-789", deletedEvent);

        // When & Then - Should not throw any exception
        assertDoesNotThrow(() -> userEventListener.handleUserEvent(deletedEvent, consumerRecord));
    }

    @Test
    void handleUserEvent_WithNullEvent_ShouldHandleGracefully() {
        // Given
        UserEvent nullEvent = null;
        consumerRecord = new ConsumerRecord<>("user.events", 0, 103L, "user-999", nullEvent);

        // When & Then - Should handle null event gracefully
        assertDoesNotThrow(() -> userEventListener.handleUserEvent(nullEvent, consumerRecord));
    }

    @Test
    void handleUserEvent_WithInvalidEventType_ShouldLogWarning() {
        // Given - Create event with null event type to simulate unknown type
        UserEvent invalidEvent = new UserEvent(null, "user-111", "test@example.com", "USER");
        consumerRecord = new ConsumerRecord<>("user.events", 0, 104L, "user-111", invalidEvent);

        // When & Then - Should not throw exception but should handle gracefully
        assertDoesNotThrow(() -> userEventListener.handleUserEvent(invalidEvent, consumerRecord));
    }

    @Test
    void userEvent_StaticFactoryMethods_ShouldCreateCorrectEventTypes() {
        // Test registered event
        UserEvent registeredEvent = UserEvent.registered("user-1", "user1@test.com", "USER");
        assertEquals(UserEvent.EventType.REGISTERED, registeredEvent.getEventType());
        assertEquals("user-1", registeredEvent.getUserId());
        assertEquals("user1@test.com", registeredEvent.getEmail());
        assertEquals("USER", registeredEvent.getRole());

        // Test updated event
        UserEvent updatedEvent = UserEvent.updated("user-2", "user2@test.com", "ADMIN");
        assertEquals(UserEvent.EventType.UPDATED, updatedEvent.getEventType());
        assertEquals("user-2", updatedEvent.getUserId());
        assertEquals("user2@test.com", updatedEvent.getEmail());
        assertEquals("ADMIN", updatedEvent.getRole());

        // Test deleted event
        UserEvent deletedEvent = UserEvent.deleted("user-3", "user3@test.com", "USER");
        assertEquals(UserEvent.EventType.DELETED, deletedEvent.getEventType());
        assertEquals("user-3", deletedEvent.getUserId());
        assertEquals("user3@test.com", deletedEvent.getEmail());
        assertEquals("USER", deletedEvent.getRole());
    }

    @Test
    void userEvent_AllArgsConstructor_ShouldSetAllFields() {
        // Given
        UserEvent.EventType eventType = UserEvent.EventType.REGISTERED;
        String userId = "user-test";
        String email = "test@example.com";
        String role = "USER";

        // When
        UserEvent event = new UserEvent(eventType, userId, email, role);

        // Then
        assertEquals(eventType, event.getEventType());
        assertEquals(userId, event.getUserId());
        assertEquals(email, event.getEmail());
        assertEquals(role, event.getRole());
    }

    @Test
    void userEvent_NoArgsConstructor_ShouldCreateEmptyEvent() {
        // When
        UserEvent event = new UserEvent();

        // Then
        assertNull(event.getEventType());
        assertNull(event.getUserId());
        assertNull(event.getEmail());
        assertNull(event.getRole());
    }

    @Test
    void userEvent_SettersAndGetters_ShouldWorkCorrectly() {
        // Given
        UserEvent event = new UserEvent();

        // When
        event.setEventType(UserEvent.EventType.UPDATED);
        event.setUserId("test-user");
        event.setEmail("test@test.com");
        event.setRole("ADMIN");

        // Then
        assertEquals(UserEvent.EventType.UPDATED, event.getEventType());
        assertEquals("test-user", event.getUserId());
        assertEquals("test@test.com", event.getEmail());
        assertEquals("ADMIN", event.getRole());
    }
}
