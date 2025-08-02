package com.jobpulse.auth_service.service.module.event.broker.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "user.events")
public record KafkaTopicConfigProperties(
    String topic,
    int partitionCount,
    int replicaCount
) {
}