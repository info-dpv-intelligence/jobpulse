package com.jobpulse.auth_service.exception;

public abstract class BaseDomainException extends RuntimeException {
    protected BaseDomainException(String message) {
        super(message);
    }
}
