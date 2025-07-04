package com.jobpulse.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Generic service result wrapper that provides type-safe responses
 * with success/failure status and optional error details.
 *
 * @param <T> the type of data returned on success
 */
@Getter
@AllArgsConstructor
public class ServiceResult<T> {
    private final boolean success;
    private final T data;
    private final String errorMessage;
    private final String errorCode;

    /**
     * Creates a successful result with data.
     */
    public static <T> ServiceResult<T> success(T data) {
        return new ServiceResult<>(true, data, null, null);
    }

    /**
     * Creates a successful result without data.
     */
    public static <T> ServiceResult<T> success() {
        return new ServiceResult<>(true, null, null, null);
    }

    /**
     * Creates a failed result with error message.
     */
    public static <T> ServiceResult<T> failure(String errorMessage) {
        return new ServiceResult<>(false, null, errorMessage, null);
    }

    /**
     * Creates a failed result with error message and code.
     */
    public static <T> ServiceResult<T> failure(String errorMessage, String errorCode) {
        return new ServiceResult<>(false, null, errorMessage, errorCode);
    }

    /**
     * Checks if the operation was successful.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Checks if the operation failed.
     */
    public boolean isFailure() {
        return !success;
    }
}
