package com.jobpulse.auth_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "Response for user registration operations")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record UserRegistrationResponse(
    @Schema(description = "Unique identifier of the newly created user", example = "123e4567-e89b-12d3-a456-426614174000")
    String userId
) {}