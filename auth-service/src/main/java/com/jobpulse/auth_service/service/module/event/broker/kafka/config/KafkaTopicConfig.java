package com.jobpulse.auth_service.service.module.event.broker.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    private KafkaTopicConfigProperties kafkaTopicConfigProperties;

    @Autowired
    public KafkaTopicConfig(
        KafkaTopicConfigProperties kafkaTopicConfigProperties
    ) {
        this.kafkaTopicConfigProperties = kafkaTopicConfigProperties;
    }

    @Bean
    public NewTopic userEventsTopic() {
        return TopicBuilder.name(kafkaTopicConfigProperties.topic())
                .partitions(kafkaTopicConfigProperties.partitionCount())
                .replicas(kafkaTopicConfigProperties.replicaCount())
                .build();
    }
}