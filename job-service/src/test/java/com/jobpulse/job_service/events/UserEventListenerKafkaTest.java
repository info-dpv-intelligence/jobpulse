package com.jobpulse.job_service.events;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserEventListenerKafkaTest {

    @Mock
    private Logger mockLogger;

    @InjectMocks
    private UserEventListener userEventListener;

    private ConsumerRecord<String, UserEvent> consumerRecord;

    @BeforeEach
    void setUp() {
        consumerRecord = new ConsumerRecord<>("user.events", 0, 100L, "test-key", null);
    }

    @Test
    void handleUserEvent_WithValidRegisteredEvent_ShouldProcessSuccessfully() {
        // Given
        UserEvent registeredEvent = UserEvent.registered("user-123", "john.doe@example.com", "USER");
        consumerRecord = new ConsumerRecord<>("user.events", 0, 100L, "user-123", registeredEvent);

        // When
        userEventListener.handleUserEvent(registeredEvent, consumerRecord);

        // Then - Verify the event was processed without exception
        assertNotNull(registeredEvent);
        assertEquals("user-123", registeredEvent.getUserId());
        assertEquals("john.doe@example.com", registeredEvent.getEmail());
        assertEquals("USER", registeredEvent.getRole());
        assertEquals(UserEvent.EventType.REGISTERED, registeredEvent.getEventType());
    }

    @Test
    void handleUserEvent_WithValidUpdatedEvent_ShouldProcessSuccessfully() {
        // Given
        UserEvent updatedEvent = UserEvent.updated("user-456", "jane.doe@example.com", "ADMIN");
        consumerRecord = new ConsumerRecord<>("user.events", 0, 101L, "user-456", updatedEvent);

        // When
        userEventListener.handleUserEvent(updatedEvent, consumerRecord);

        // Then
        assertNotNull(updatedEvent);
        assertEquals("user-456", updatedEvent.getUserId());
        assertEquals("jane.doe@example.com", updatedEvent.getEmail());
        assertEquals("ADMIN", updatedEvent.getRole());
        assertEquals(UserEvent.EventType.UPDATED, updatedEvent.getEventType());
    }

    @Test
    void handleUserEvent_WithValidDeletedEvent_ShouldProcessSuccessfully() {
        // Given
        UserEvent deletedEvent = UserEvent.deleted("user-789", "bob.smith@example.com", "USER");
        consumerRecord = new ConsumerRecord<>("user.events", 0, 102L, "user-789", deletedEvent);

        // When
        userEventListener.handleUserEvent(deletedEvent, consumerRecord);

        // Then
        assertNotNull(deletedEvent);
        assertEquals("user-789", deletedEvent.getUserId());
        assertEquals("bob.smith@example.com", deletedEvent.getEmail());
        assertEquals("USER", deletedEvent.getRole());
        assertEquals(UserEvent.EventType.DELETED, deletedEvent.getEventType());
    }

    @Test
    void handleUserEvent_WithMalformedEvent_ShouldHandleGracefullyWithoutCrashing() {
        // Given - Event with missing or invalid data
        UserEvent malformedEvent = new UserEvent();
        malformedEvent.setEventType(UserEvent.EventType.REGISTERED);
        // Missing userId, email, role
        
        consumerRecord = new ConsumerRecord<>("user.events", 0, 103L, "malformed-key", malformedEvent);

        // When & Then - Should not throw any exception
        assertDoesNotThrow(() -> userEventListener.handleUserEvent(malformedEvent, consumerRecord));
    }

    @Test
    void handleUserEvent_WithHighVolumeOfEvents_ShouldProcessAllEvents() {
        // Given - Simulate processing multiple events in sequence
        UserEvent[] events = {
            UserEvent.registered("user-001", "user1@test.com", "USER"),
            UserEvent.updated("user-002", "user2@test.com", "ADMIN"),
            UserEvent.deleted("user-003", "user3@test.com", "USER"),
            UserEvent.registered("user-004", "user4@test.com", "USER"),
            UserEvent.updated("user-005", "user5@test.com", "USER")
        };

        // When & Then - Process all events without issues
        for (int i = 0; i < events.length; i++) {
            final int index = i;
            ConsumerRecord<String, UserEvent> record = new ConsumerRecord<>(
                "user.events", 0, index, "user-00" + (index + 1), events[index]
            );
            
            assertDoesNotThrow(() -> userEventListener.handleUserEvent(events[index], record),
                "Event processing should not fail for event " + index);
        }
    }

    @Test
    void handleUserEvent_WithConsumerRecordMetadata_ShouldHandleMetadataCorrectly() {
        // Given
        UserEvent event = UserEvent.registered("user-metadata-test", "test@metadata.com", "USER");
        ConsumerRecord<String, UserEvent> recordWithMetadata = new ConsumerRecord<>(
            "user.events",  // topic
            0,              // partition
            12345L,         // offset
            "test-key",     // key
            event           // value
        );

        // When & Then - Should handle record with all metadata without issues
        assertDoesNotThrow(() -> userEventListener.handleUserEvent(event, recordWithMetadata));
        
        // Verify record metadata can be accessed
        assertEquals("user.events", recordWithMetadata.topic());
        assertEquals(0, recordWithMetadata.partition());
        assertEquals(12345L, recordWithMetadata.offset());
        assertEquals("test-key", recordWithMetadata.key());
        assertEquals(event, recordWithMetadata.value());
    }
}
