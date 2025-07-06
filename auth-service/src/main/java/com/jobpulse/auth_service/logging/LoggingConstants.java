package com.jobpulse.auth_service.logging;

public final class LoggingConstants {

    public static final String SERVICE_NAME = "service";
    public static final String CORRELATION_ID = "correlationId";
    public static final String USER_ID = "userId";
    public static final String SESSION_ID = "sessionId";
    public static final String REQUEST_ID = "requestId";
    public static final String OPERATION = "operation";
    public static final String EVENT_TYPE = "eventType";
    public static final String DURATION_MS = "durationMs";
    public static final String ERROR_CODE = "errorCode";
    public static final String ERROR_TYPE = "errorType";
    public static final String KAFKA_TOPIC = "kafkaTopic";
    public static final String KAFKA_PARTITION = "kafkaPartition";
    public static final String KAFKA_OFFSET = "kafkaOffset";
    public static final String HTTP_METHOD = "httpMethod";
    public static final String HTTP_STATUS = "httpStatus";
    public static final String HTTP_PATH = "httpPath";
    
    public static final String EVENT_REQUEST_RECEIVED = "REQUEST_RECEIVED";
    public static final String EVENT_REQUEST_COMPLETED = "REQUEST_COMPLETED";
    public static final String EVENT_KAFKA_MESSAGE_RECEIVED = "KAFKA_MESSAGE_RECEIVED";
    public static final String EVENT_KAFKA_MESSAGE_PROCESSED = "KAFKA_MESSAGE_PROCESSED";
    public static final String EVENT_DOMAIN_EVENT_PUBLISHED = "DOMAIN_EVENT_PUBLISHED";
    public static final String EVENT_USER_REGISTERED = "USER_REGISTERED";
    public static final String EVENT_USER_UPDATED = "USER_UPDATED";
    public static final String EVENT_USER_DELETED = "USER_DELETED";
    public static final String EVENT_AUTH_SUCCESS = "AUTH_SUCCESS";
    public static final String EVENT_AUTH_FAILURE = "AUTH_FAILURE";
    public static final String EVENT_ERROR = "ERROR";
    
    public static final String ERROR_VALIDATION = "VALIDATION_ERROR";
    public static final String ERROR_AUTHENTICATION = "AUTHENTICATION_ERROR";
    public static final String ERROR_AUTHORIZATION = "AUTHORIZATION_ERROR";
    public static final String ERROR_KAFKA = "KAFKA_ERROR";
    public static final String ERROR_DATABASE = "DATABASE_ERROR";
    public static final String ERROR_EXTERNAL_SERVICE = "EXTERNAL_SERVICE_ERROR";
    public static final String ERROR_BUSINESS_LOGIC = "BUSINESS_LOGIC_ERROR";
    public static final String ERROR_SYSTEM = "SYSTEM_ERROR";
    
    private LoggingConstants() {
    }
}
