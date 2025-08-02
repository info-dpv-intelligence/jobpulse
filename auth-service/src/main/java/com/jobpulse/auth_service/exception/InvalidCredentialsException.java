package com.jobpulse.auth_service.exception;

public class InvalidCredentialsException extends BaseDomainException {
    public InvalidCredentialsException() {
        super("Invalid credentials provided");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
