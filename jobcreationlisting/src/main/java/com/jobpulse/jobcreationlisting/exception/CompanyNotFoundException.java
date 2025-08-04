package com.jobpulse.jobcreationlisting.exception;

public class CompanyNotFoundException extends RuntimeException {
    protected CompanyNotFoundException(String message) {
        super(message);
    }
    public CompanyNotFoundException() {
        super("Company not found");
    }
}

