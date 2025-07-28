package com.jobpulse.jobcreationlisting.dto.repository.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OperationResult<T> {
    private final boolean success;
    private final T data;
    private final String errorMessage;
    private final String errorCode;

    public static <T> OperationResult<T> success(T data) {
        return new OperationResult<>(true, data, null, null);
    }

    public static <T> OperationResult<T> success() {
        return new OperationResult<>(true, null, null, null);
    }

    public static <T> OperationResult<T> failure(String errorMessage) {
        return new OperationResult<>(false, null, errorMessage, null);
    }

    public static <T> OperationResult<T> failure(String errorMessage, String errorCode) {
        return new OperationResult<>(false, null, errorMessage, errorCode);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }
}
