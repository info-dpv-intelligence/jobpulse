package com.jobpulse.job_service.events;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.jobpulse.common_events.model.UserEvent;

@Service
public class UserEventListener {

    @KafkaListener(
        topics = "${user.events.topic:user.events}",
        groupId = "${spring.kafka.consumer.group-id:job-service-group}"
    )
    public void handleUserEvent(UserEvent event) {
        // Process the event (e.g., send welcome email)
        System.out.println("Received user event: " + event);
        // Add your business logic here
    }
}