package com.jobpulse.auth_service.controller.config;

import com.jobpulse.auth_service.dto.response.ControllerResponse;
import com.jobpulse.auth_service.exception.InvalidCredentialsException;
import com.jobpulse.auth_service.exception.UserAlreadyExistsException;
import com.jobpulse.auth_service.exception.UserNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ControllerResponse<?> handleUserNotFound(UserNotFoundException ex) {
        return ControllerResponse.builder()
            .status(false)
            .code(HttpStatus.NOT_FOUND.value())
            .message(ex.getMessage())
            .build();
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ControllerResponse<?> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ControllerResponse.builder()
            .status(false)
            .code(HttpStatus.UNAUTHORIZED.value())
            .message(ex.getMessage())
            .build();
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ControllerResponse<?> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ControllerResponse.builder()
            .status(false)
            .code(HttpStatus.CONFLICT.value())
            .message(ex.getMessage())
            .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ControllerResponse<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .reduce((a, b) -> a + "; " + b)
            .orElse("Validation failed");

        return ControllerResponse.builder()
            .status(false)
            .code(HttpStatus.BAD_REQUEST.value())
            .message(message)
            .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ControllerResponse<?> handleConstraintViolation(ConstraintViolationException ex) {
        return ControllerResponse.builder()
            .status(false)
            .code(HttpStatus.BAD_REQUEST.value())
            .message("Validation failed: " + ex.getMessage())
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
