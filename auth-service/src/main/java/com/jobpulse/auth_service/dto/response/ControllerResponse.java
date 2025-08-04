package com.jobpulse.auth_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Schema(description = "Standard API response wrapper")
@Value
@Builder
public class ControllerResponse<T> {
    @Schema(description = "Indicates if the operation was successful")
    boolean status;

    @Schema(description = "HTTP status code")
    int code;

    @Schema(description = "Response message")
    String message;

    T data;

    public static <T> ControllerResponse<T> success(T data) {
        return ControllerResponse.<T>builder()
            .status(true)
            .code(200)
            .message("Success")
            .data(data)
            .build();
    }

    public static <T> ControllerResponse<T> error(int code, String message) {
        return ControllerResponse.<T>builder()
            .status(false)
            .code(code)
            .message(message)
            .build();
    }
}
