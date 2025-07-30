package com.jobpulse.jobcreationlisting.dto.response;

public record ErrorResponse(int status, String error, String message) { }
