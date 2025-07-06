package com.jobpulse.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class StructuredLogger {

    private final Logger logger;
    private final String serviceName;

    public StructuredLogger(Class<?> clazz, String serviceName) {
        this.logger = LoggerFactory.getLogger(clazz);
        this.serviceName = serviceName;
    }

    public static StructuredLogger getLogger(Class<?> clazz, String serviceName) {
        return new StructuredLogger(clazz, serviceName);
    }

    public void info(String eventType, String message, Map<String, Object> context) {
        try {
            setCommonMDC(eventType);
            if (context != null) {
                context.forEach((key, value) -> MDC.put(key, String.valueOf(value)));
            }
            logger.info(message);
        } finally {
            MDC.clear();
        }
    }

    public void info(String eventType, String message) {
        info(eventType, message, null);
    }

    public void error(String eventType, String errorType, String message, Throwable throwable, Map<String, Object> context) {
        try {
            setCommonMDC(eventType);
            MDC.put(LoggingConstants.ERROR_TYPE, errorType);
            if (context != null) {
                context.forEach((key, value) -> MDC.put(key, String.valueOf(value)));
            }
            logger.error(message, throwable);
        } finally {
            MDC.clear();
        }
    }

    public void error(String eventType, String errorType, String message, Map<String, Object> context) {
        error(eventType, errorType, message, null, context);
    }

    public void warn(String eventType, String message, Map<String, Object> context) {
        try {
            setCommonMDC(eventType);
            if (context != null) {
                context.forEach((key, value) -> MDC.put(key, String.valueOf(value)));
            }
            logger.warn(message);
        } finally {
            MDC.clear();
        }
    }

    public void debug(String eventType, String message, Map<String, Object> context) {
        if (logger.isDebugEnabled()) {
            try {
                setCommonMDC(eventType);
                if (context != null) {
                    context.forEach((key, value) -> MDC.put(key, String.valueOf(value)));
                }
                logger.debug(message);
            } finally {
                MDC.clear();
            }
        }
    }

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

    public void logHttpRequest(String eventType, String message, String method, String path, int status, long durationMs) {
        Map<String, Object> context = new HashMap<>();
        context.put(LoggingConstants.HTTP_METHOD, method);
        context.put(LoggingConstants.HTTP_PATH, path);
        context.put(LoggingConstants.HTTP_STATUS, status);
        context.put(LoggingConstants.DURATION_MS, durationMs);
        info(eventType, message, context);
    }

    public void logOperation(String eventType, String operation, String message, long durationMs, Map<String, Object> additionalContext) {
        Map<String, Object> context = new HashMap<>();
        context.put(LoggingConstants.OPERATION, operation);
        context.put(LoggingConstants.DURATION_MS, durationMs);
        if (additionalContext != null) {
            context.putAll(additionalContext);
        }
        info(eventType, message, context);
    }

    public void setCorrelationId(String correlationId) {
        MDC.put(LoggingConstants.CORRELATION_ID, correlationId);
    }

    public String generateAndSetCorrelationId() {
        String correlationId = UUID.randomUUID().toString();
        setCorrelationId(correlationId);
        return correlationId;
    }

    public void setUserContext(String userId, String sessionId) {
        if (userId != null) {
            MDC.put(LoggingConstants.USER_ID, userId);
        }
        if (sessionId != null) {
            MDC.put(LoggingConstants.SESSION_ID, sessionId);
        }
    }

    public void clearUserContext() {
        MDC.remove(LoggingConstants.USER_ID);
        MDC.remove(LoggingConstants.SESSION_ID);
    }

    public void clearContext() {
        MDC.clear();
    }

    private void setCommonMDC(String eventType) {
        MDC.put(LoggingConstants.SERVICE_NAME, serviceName);
        MDC.put(LoggingConstants.EVENT_TYPE, eventType);
        if (MDC.get(LoggingConstants.REQUEST_ID) == null) {
            MDC.put(LoggingConstants.REQUEST_ID, UUID.randomUUID().toString());
        }
    }

    public Logger getLogger() {
        return logger;
    }
}
