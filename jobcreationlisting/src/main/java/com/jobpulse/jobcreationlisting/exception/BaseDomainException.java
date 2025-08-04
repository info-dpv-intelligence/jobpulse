package com.jobpulse.jobcreationlisting.exception;

public abstract class BaseDomainException extends RuntimeException {
    protected BaseDomainException(String message) {
        super(message);
    }
}
