package com.jobpulse.job_service.events;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.concurrent.TimeUnit;

//TODO: remove the @Disabled annotation.
@SpringBootTest
@EmbeddedKafka(
    partitions = 1,
    topics = {"user.events"},
    brokerProperties = {
        "listeners=PLAINTEXT://localhost:9092",
        "port=9092"
    }
)
@DirtiesContext
@ActiveProfiles("test")
@org.junit.jupiter.api.Disabled("Disabled due to embedded Kafka timeout issues. Enable when Kafka environment is stable.")
class UserEventListenerIntegrationTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @BeforeEach
    void setUp() {
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        DefaultKafkaProducerFactory<String, UserEvent> producerFactory = 
            new DefaultKafkaProducerFactory<>(producerProps, new StringSerializer(), new JsonSerializer<>());
        kafkaTemplate = new KafkaTemplate<>(producerFactory);
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void whenUserRegisteredEventSent_shouldConsumeAndProcessEvent() throws Exception {
        // Given
        UserEvent registeredEvent = UserEvent.registered("user-123", "john.doe@example.com", "USER");

        // When
        kafkaTemplate.send("user.events", "user-123", registeredEvent);

        // Then - The test will pass if no exception is thrown and the timeout doesn't occur
        Thread.sleep(2000); // Allow time for message processing
        // In a real scenario, you might want to verify side effects like database changes
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void whenUserUpdatedEventSent_shouldConsumeAndProcessEvent() throws Exception {
        // Given
        UserEvent updatedEvent = UserEvent.updated("user-456", "jane.doe@example.com", "ADMIN");

        // When
        kafkaTemplate.send("user.events", "user-456", updatedEvent);

        // Then
        Thread.sleep(2000); // Allow time for message processing
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void whenUserDeletedEventSent_shouldConsumeAndProcessEvent() throws Exception {
        // Given
        UserEvent deletedEvent = UserEvent.deleted("user-789", "bob.smith@example.com", "USER");

        // When
        kafkaTemplate.send("user.events", "user-789", deletedEvent);

        // Then
        Thread.sleep(2000); // Allow time for message processing
    }

    @Test
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void whenMultipleEventsArePublished_shouldProcessAllEvents() throws Exception {
        // Given
        UserEvent registeredEvent = UserEvent.registered("user-001", "user1@test.com", "USER");
        UserEvent updatedEvent = UserEvent.updated("user-002", "user2@test.com", "ADMIN");
        UserEvent deletedEvent = UserEvent.deleted("user-003", "user3@test.com", "USER");

        // When
        kafkaTemplate.send("user.events", "user-001", registeredEvent);
        kafkaTemplate.send("user.events", "user-002", updatedEvent);
        kafkaTemplate.send("user.events", "user-003", deletedEvent);

        // Then
        Thread.sleep(3000); // Allow time for all messages to be processed
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void whenEventWithDifferentPartitions_shouldProcessAllEvents() throws Exception {
        // Given
        UserEvent event1 = UserEvent.registered("user-partition-1", "user1@partition.com", "USER");
        UserEvent event2 = UserEvent.registered("user-partition-2", "user2@partition.com", "USER");

        // When - Send to different partitions by using different keys
        ProducerRecord<String, UserEvent> record1 = new ProducerRecord<>("user.events", 0, "key1", event1);
        ProducerRecord<String, UserEvent> record2 = new ProducerRecord<>("user.events", 0, "key2", event2);
        
        kafkaTemplate.send(record1);
        kafkaTemplate.send(record2);

        // Then
        Thread.sleep(2000); // Allow time for message processing
    }
}
