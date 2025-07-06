package com.jobpulse.job_service.events;

import com.jobpulse.job_service.logging.LoggingConfig;
import com.jobpulse.job_service.logging.LoggingConstants;
import com.jobpulse.job_service.logging.StructuredLogger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserEventListener {

    private final StructuredLogger logger;

    public UserEventListener(LoggingConfig.LoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(UserEventListener.class);
    }

    @KafkaListener(
        topics = "${user.events.topic:user.events}",
        groupId = "${spring.kafka.consumer.group-id:job-service-group}"
    )
    public void handleUserEvent(UserEvent event, ConsumerRecord<String, UserEvent> record) {
        try {
            Map<String, Object> context = new HashMap<>();
            context.put(LoggingConstants.USER_ID, event.getUserId());
            logger.logKafkaMessage(
                LoggingConstants.EVENT_KAFKA_MESSAGE_RECEIVED,
                "Received user event: " + event.getEventType() + " for user: " + event.getUserId(),
                record.topic(),
                record.partition(),
                record.offset(),
                context
            );
            
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
                    context.put("unknownEventType", event.getEventType());
                    logger.warn(LoggingConstants.EVENT_ERROR, "Unknown user event type: " + event.getEventType(), context);
            }

        } catch (Exception ex) {
            Map<String, Object> errorContext = new HashMap<>();
            errorContext.put(LoggingConstants.KAFKA_OFFSET, record.offset());
            logger.error(
                LoggingConstants.EVENT_ERROR,
                LoggingConstants.ERROR_KAFKA,
                "Error processing user event at offset " + record.offset() + ": " + ex.getMessage(),
                ex,
                errorContext
            );
        }
    }
    
    private void handleUserRegistered(UserEvent event) {
        Map<String, Object> context = new HashMap<>();
        context.put(LoggingConstants.USER_ID, event.getUserId());
        context.put("email", event.getEmail());
        context.put("role", event.getRole());
        logger.info(
            LoggingConstants.EVENT_USER_REGISTERED,
            "Processing user registration: userId=" + event.getUserId() + ", email=" + event.getEmail() + ", role=" + event.getRole(),
            context
        );
    }
    
    private void handleUserUpdated(UserEvent event) {
        Map<String, Object> context = new HashMap<>();
        context.put(LoggingConstants.USER_ID, event.getUserId());
        context.put("email", event.getEmail());
        context.put("role", event.getRole());
        logger.info(
            LoggingConstants.EVENT_USER_UPDATED,
            "Processing user update: userId=" + event.getUserId() + ", email=" + event.getEmail() + ", role=" + event.getRole(),
            context
        );
    }
    
    private void handleUserDeleted(UserEvent event) {
        Map<String, Object> context = new HashMap<>();
        context.put(LoggingConstants.USER_ID, event.getUserId());
        logger.info(
            LoggingConstants.EVENT_USER_DELETED,
            "Processing user deletion: userId=" + event.getUserId(),
            context
        );
    }
}
