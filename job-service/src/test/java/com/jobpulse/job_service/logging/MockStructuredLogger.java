package com.jobpulse.job_service.logging;

import java.util.HashMap;
import java.util.Map;

public class MockStructuredLogger implements StructuredLogger {

    private final Map<String, Object> lastContext = new HashMap<>();
    private String lastEventType;
    private String lastMessage;
    private String lastErrorType;
    private Throwable lastThrowable;

    @Override
    public void info(String eventType, String message, Map<String, Object> context) {
        this.lastEventType = eventType;
        this.lastMessage = message;
        this.lastContext.clear();
        if (context != null) {
            this.lastContext.putAll(context);
        }
    }

    @Override
    public void info(String eventType, String message) {
        info(eventType, message, null);
    }

    @Override
    public void error(String eventType, String errorType, String message, Throwable throwable, Map<String, Object> context) {
        this.lastEventType = eventType;
        this.lastErrorType = errorType;
        this.lastMessage = message;
        this.lastThrowable = throwable;
        this.lastContext.clear();
        if (context != null) {
            this.lastContext.putAll(context);
        }
    }

    @Override
    public void error(String eventType, String errorType, String message, Map<String, Object> context) {
        error(eventType, errorType, message, null, context);
    }

    @Override
    public void warn(String eventType, String message, Map<String, Object> context) {
        this.lastEventType = eventType;
        this.lastMessage = message;
        this.lastContext.clear();
        if (context != null) {
            this.lastContext.putAll(context);
        }
    }

    @Override
    public void debug(String eventType, String message, Map<String, Object> context) {
        this.lastEventType = eventType;
        this.lastMessage = message;
        this.lastContext.clear();
        if (context != null) {
            this.lastContext.putAll(context);
        }
    }

    @Override
    public void logKafkaMessage(String eventType, String message, String topic, int partition, long offset, Map<String, Object> additionalContext) {
        Map<String, Object> context = new HashMap<>();
        context.put(LoggingConstants.KAFKA_TOPIC, topic);
        context.put(LoggingConstants.KAFKA_PARTITION, partition);
        context.put(LoggingConstants.KAFKA_OFFSET, offset);
        if (additionalContext != null) {
            context.putAll(additionalContext);
        }
        info(eventType, message, context);
    }

    @Override
    public void logHttpRequest(String eventType, String message, String method, String path, int status, long durationMs) {
        Map<String, Object> context = new HashMap<>();
        context.put(LoggingConstants.HTTP_METHOD, method);
        context.put(LoggingConstants.HTTP_PATH, path);
        context.put(LoggingConstants.HTTP_STATUS, status);
        context.put(LoggingConstants.DURATION_MS, durationMs);
        info(eventType, message, context);
    }

    @Override
    public void logOperation(String eventType, String operation, String message, long durationMs, Map<String, Object> additionalContext) {
        Map<String, Object> context = new HashMap<>();
        context.put(LoggingConstants.OPERATION, operation);
        context.put(LoggingConstants.DURATION_MS, durationMs);
        if (additionalContext != null) {
            context.putAll(additionalContext);
        }
        info(eventType, message, context);
    }

    @Override
    public void setCorrelationId(String correlationId) {
    }

    @Override
    public String generateAndSetCorrelationId() {
        return "mock-correlation-id";
    }

    @Override
    public void setUserContext(String userId, String sessionId) {
    }

    @Override
    public void clearUserContext() {
    }

    @Override
    public void clearContext() {
    }

    public String getLastEventType() {
        return lastEventType;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastErrorType() {
        return lastErrorType;
    }

    public Throwable getLastThrowable() {
        return lastThrowable;
    }

    public Map<String, Object> getLastContext() {
        return new HashMap<>(lastContext);
    }

    public void reset() {
        lastEventType = null;
        lastMessage = null;
        lastErrorType = null;
        lastThrowable = null;
        lastContext.clear();
    }
}
