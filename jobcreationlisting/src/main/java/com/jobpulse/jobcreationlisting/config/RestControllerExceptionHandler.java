package com.jobpulse.jobcreationlisting.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jobpulse.jobcreationlisting.dto.response.ControllerResponse;

import com.jobpulse.jobcreationlisting.dto.response.ErrorResponse;
import com.jobpulse.jobcreationlisting.exception.CompanyNotFoundException;

@RestControllerAdvice
public class RestControllerExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ControllerResponse<?> handleIllegalArgumentException(Exception ex) {
        return ControllerResponse.builder()
            .status(false)
            .code(HttpStatus.BAD_REQUEST.value())
            .message(ex.getMessage())
            .build();
    }
    @ExceptionHandler(CompanyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ControllerResponse<?> handleCompanyNotFound(Exception ex) {
        return ControllerResponse.builder()
            .status(false)
            .code(HttpStatus.NOT_FOUND.value())
            .message(ex.getMessage())
            .build();
    }
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ControllerResponse<?> handleGenericException(Exception ex) {
        return ControllerResponse.builder()
            .status(false)
            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message("An unexpected error occurred")
            .build();
    }
}
