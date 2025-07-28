package com.jobpulse.jobcreationlisting.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServiceResult<T> {
    private final boolean success;
    private final T data;
    private final String errorMessage;
    private final String errorCode;

    public static <T> ServiceResult<T> success(T data) {
        return new ServiceResult<>(true, data, null, null);
    }

    public static <T> ServiceResult<T> success() {
        return new ServiceResult<>(true, null, null, null);
    }

    public static <T> ServiceResult<T> failure(String errorMessage) {
        return new ServiceResult<>(false, null, errorMessage, null);
    }

    public static <T> ServiceResult<T> failure(String errorMessage, String errorCode) {
        return new ServiceResult<>(false, null, errorMessage, errorCode);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }
}
