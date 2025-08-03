package com.jobpulse.auth_service.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TokenResponse {
    String accessToken;
    String refreshToken;
}
