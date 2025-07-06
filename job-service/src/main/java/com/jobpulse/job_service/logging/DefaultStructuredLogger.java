package com.jobpulse.job_service.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DefaultStructuredLogger implements StructuredLogger {

    private final Logger logger;
    private final String serviceName;

    public DefaultStructuredLogger(Class<?> clazz, String serviceName) {
        this.logger = LoggerFactory.getLogger(clazz);
        this.serviceName = serviceName;
    }

    @Override
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

    @Override
    public void info(String eventType, String message) {
        info(eventType, message, null);
    }

    @Override
    public void error(String eventType, String errorType, String message, Throwable throwable, Map<String, Object> context) {
        try {
            setCommonMDC(eventType);
            MDC.put(LoggingConstants.ERROR_TYPE, errorType);
            if (context != null) {
                context.forEach((key, value) -> MDC.put(key, String.valueOf(value)));
            }
            if (throwable != null) {
                logger.error(message, throwable);
            } else {
                logger.error(message);
            }
        } finally {
            MDC.clear();
        }
    }

    @Override
    public void error(String eventType, String errorType, String message, Map<String, Object> context) {
        error(eventType, errorType, message, null, context);
    }

    @Override
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

    @Override
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
        MDC.put(LoggingConstants.CORRELATION_ID, correlationId);
    }

    @Override
    public String generateAndSetCorrelationId() {
        String correlationId = UUID.randomUUID().toString();
        setCorrelationId(correlationId);
        return correlationId;
    }

    @Override
    public void setUserContext(String userId, String sessionId) {
        if (userId != null) {
            MDC.put(LoggingConstants.USER_ID, userId);
        }
        if (sessionId != null) {
            MDC.put(LoggingConstants.SESSION_ID, sessionId);
        }
    }

    @Override
    public void clearUserContext() {
        MDC.remove(LoggingConstants.USER_ID);
        MDC.remove(LoggingConstants.SESSION_ID);
    }

    @Override
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
