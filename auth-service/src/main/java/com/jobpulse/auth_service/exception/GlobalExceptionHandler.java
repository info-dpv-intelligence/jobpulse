package com.jobpulse.auth_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataAccessException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        FieldError fieldError = (FieldError) ex.getBindingResult().getAllErrors().get(0);
        String message = "Validation error: " + fieldError.getField() + " - " + fieldError.getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleParseException(HttpMessageNotReadableException ex) {
        // Try to extract the field name from the exception message (works for enums and some parse errors)
        String field = "unknown";
        String msg = ex.getMostSpecificCause().getMessage();
        if (msg != null && msg.contains("through reference chain")) {
            int idx = msg.lastIndexOf("[\"");
            if (idx != -1) {
                int endIdx = msg.indexOf("\"]", idx);
                if (endIdx != -1) {
                    field = msg.substring(idx + 2, endIdx);
                }
            }
        }
        String message = "Invalid request: Invalid value for field '" + field + "'.";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDatabaseException(DataAccessException ex) {
        // Optionally log ex.getMessage()
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error: Database operation failed.");
    }
}