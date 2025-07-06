package com.jobpulse.job_service.events;

import com.jobpulse.job_service.events.UserEvent;
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
            logger.info("Received user event: {} for user: {}", event.getEventType(), event.getUserId());
            
            switch (event.getEventType()) {
                case REGISTERED:
                    handleUserRegistered(event);
                    break;
                case UPDATED:
                    handleUserUpdated(event);
                    break;
                case DELETED:
                    handleUserDeleted(event);
                    break;
                default:
                    logger.warn("Unknown user event type: {}", event.getEventType());
            }

        } catch (Exception ex) {
            logger.error("Error processing user event at offset {}: {}", record.offset(), ex.getMessage(), ex);
        }
    }
    
    private void handleUserRegistered(UserEvent event) {
        logger.info("Processing user registration: userId={}, email={}, role={}", 
                   event.getUserId(), event.getEmail(), event.getRole());
    }
    
    private void handleUserUpdated(UserEvent event) {
        logger.info("Processing user update: userId={}, email={}, role={}", 
                   event.getUserId(), event.getEmail(), event.getRole());
    }
    
    private void handleUserDeleted(UserEvent event) {
        logger.info("Processing user deletion: userId={}", event.getUserId());
    }
}
