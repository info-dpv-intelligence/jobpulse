package com.jobpulse.auth_service.domain;

/**
 * Exception thrown when domain event publishing fails.
 */
public class DomainEventPublishingException extends RuntimeException {
    
    public DomainEventPublishingException(String message) {
        super(message);
    }
    
    public DomainEventPublishingException(String message, Throwable cause) {
        super(message, cause);
    }
}
