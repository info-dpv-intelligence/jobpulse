package com.jobpulse.auth_service.exception;

public class UserAlreadyExistsException extends BaseDomainException {
    public UserAlreadyExistsException(String email) {
        super(String.format("User already exists with email: %s", email));
    }
}
