package com.jobpulse.job_service.events;

import com.jobpulse.common_events.model.UserEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserEventListener {

    private static final Logger logger = LoggerFactory.getLogger(UserEventListener.class);

    @KafkaListener(
        topics = "${user.events.topic:user.events}",
        groupId = "${spring.kafka.consumer.group-id:job-service-group}"
    )
    public void handleUserEvent(UserEvent event, ConsumerRecord<String, UserEvent> record) {
        try {
            logger.info("Received user event: {}", event);
            // Your business logic here

        } catch (Exception ex) {
            logger.error("Error processing user event at offset {}: {}", record.offset(), ex.getMessage(), ex);
            // Optionally: send to a dead-letter topic or alert
        }
    }
}
