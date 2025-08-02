package com.jobpulse.auth_service.exception;

public class UserNotFoundException extends BaseDomainException {
    public UserNotFoundException(String email) {
        super(String.format("User not found with email: %s", email));
    }
}
