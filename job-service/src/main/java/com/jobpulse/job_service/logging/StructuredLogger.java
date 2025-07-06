package com.jobpulse.job_service.logging;

import java.util.Map;

public interface StructuredLogger {

    void info(String eventType, String message, Map<String, Object> context);

    void info(String eventType, String message);

    void error(String eventType, String errorType, String message, Throwable throwable, Map<String, Object> context);

    void error(String eventType, String errorType, String message, Map<String, Object> context);

    void warn(String eventType, String message, Map<String, Object> context);

    void debug(String eventType, String message, Map<String, Object> context);

    void logKafkaMessage(String eventType, String message, String topic, int partition, long offset, Map<String, Object> additionalContext);

    void logHttpRequest(String eventType, String message, String method, String path, int status, long durationMs);

    void logOperation(String eventType, String operation, String message, long durationMs, Map<String, Object> additionalContext);

    void setCorrelationId(String correlationId);

    String generateAndSetCorrelationId();

    void setUserContext(String userId, String sessionId);

    void clearUserContext();

    void clearContext();
}
