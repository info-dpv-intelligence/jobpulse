package com.jobpulse.job_service.events;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Error handling tests for UserEventListener to ensure robust event processing.
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserEventListenerErrorHandlingTest {

    @InjectMocks
    private UserEventListener userEventListener;

    private ConsumerRecord<String, UserEvent> consumerRecord;

    @BeforeEach
    void setUp() {
        consumerRecord = new ConsumerRecord<>("user.events", 0, 100L, "test-key", null);
    }

    @Test
    void handleUserEvent_WithNullEvent_ShouldHandleGracefully() {
        // Given
        UserEvent nullEvent = null;
        consumerRecord = new ConsumerRecord<>("user.events", 0, 100L, "test-key", nullEvent);

        // When & Then - Should not throw any exception
        assertDoesNotThrow(() -> userEventListener.handleUserEvent(nullEvent, consumerRecord));
    }

    @Test
    void handleUserEvent_WithNullEventType_ShouldHandleGracefully() {
        // Given
        UserEvent eventWithNullType = new UserEvent();
        eventWithNullType.setEventType(null);
        eventWithNullType.setUserId("user-123");
        eventWithNullType.setEmail("test@example.com");
        eventWithNullType.setRole("USER");

        // When & Then - Should not throw any exception
        assertDoesNotThrow(() -> userEventListener.handleUserEvent(eventWithNullType, consumerRecord));
    }

    @Test
    void handleUserEvent_WithNullUserId_ShouldHandleGracefully() {
        // Given
        UserEvent eventWithNullUserId = UserEvent.registered(null, "test@example.com", "USER");

        // When & Then - Should not throw any exception
        assertDoesNotThrow(() -> userEventListener.handleUserEvent(eventWithNullUserId, consumerRecord));
    }

    @Test
    void handleUserEvent_WithNullEmail_ShouldHandleGracefully() {
        // Given
        UserEvent eventWithNullEmail = UserEvent.registered("user-123", null, "USER");

        // When & Then - Should not throw any exception
        assertDoesNotThrow(() -> userEventListener.handleUserEvent(eventWithNullEmail, consumerRecord));
    }

    @Test
    void handleUserEvent_WithNullRole_ShouldHandleGracefully() {
        // Given
        UserEvent eventWithNullRole = UserEvent.registered("user-123", "test@example.com", null);

        // When & Then - Should not throw any exception
        assertDoesNotThrow(() -> userEventListener.handleUserEvent(eventWithNullRole, consumerRecord));
    }

    @Test
    void handleUserEvent_WithEmptyStrings_ShouldHandleGracefully() {
        // Given
        UserEvent eventWithEmptyStrings = new UserEvent(
            UserEvent.EventType.REGISTERED,
            "",
            "",
            ""
        );

        // When & Then - Should not throw any exception
        assertDoesNotThrow(() -> userEventListener.handleUserEvent(eventWithEmptyStrings, consumerRecord));
    }

    @Test
    void handleUserEvent_WithNullConsumerRecord_ShouldHandleGracefully() {
        // Given
        UserEvent validEvent = UserEvent.registered("user-123", "test@example.com", "USER");
        ConsumerRecord<String, UserEvent> nullRecord = null;

        // When & Then - Should not throw any exception (but may result in NPE in logging)
        // This test verifies that the listener can handle unexpected null records
        assertDoesNotThrow(() -> userEventListener.handleUserEvent(validEvent, nullRecord));
    }

    @Test
    void userEvent_EventTypeEnum_ShouldHaveAllExpectedValues() {
        // Verify all expected event types are present
        UserEvent.EventType[] eventTypes = UserEvent.EventType.values();
        
        assertEquals(3, eventTypes.length);
        assertTrue(java.util.Arrays.asList(eventTypes).contains(UserEvent.EventType.REGISTERED));
        assertTrue(java.util.Arrays.asList(eventTypes).contains(UserEvent.EventType.UPDATED));
        assertTrue(java.util.Arrays.asList(eventTypes).contains(UserEvent.EventType.DELETED));
    }

    @Test
    void userEvent_EqualsAndHashCode_ShouldWorkCorrectly() {
        // Given
        UserEvent event1 = UserEvent.registered("user-123", "test@example.com", "USER");
        UserEvent event2 = UserEvent.registered("user-123", "test@example.com", "USER");
        UserEvent event3 = UserEvent.registered("user-456", "other@example.com", "ADMIN");

        // When & Then
        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
        assertNotEquals(event1, event3);
        assertNotEquals(event1.hashCode(), event3.hashCode());
    }

    @Test
    void userEvent_ToString_ShouldContainAllFields() {
        // Given
        UserEvent event = UserEvent.registered("user-123", "test@example.com", "USER");

        // When
        String toString = event.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("user-123"));
        assertTrue(toString.contains("test@example.com"));
        assertTrue(toString.contains("USER"));
        assertTrue(toString.contains("REGISTERED"));
    }
}
