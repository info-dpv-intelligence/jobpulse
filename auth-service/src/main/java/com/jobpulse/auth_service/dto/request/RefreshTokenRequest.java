package com.jobpulse.auth_service.dto.request;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RefreshTokenRequest(
    UUID userId
) {
}
