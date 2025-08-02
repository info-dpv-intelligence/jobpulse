package com.jobpulse.auth_service.service.module.jwt;

import com.jobpulse.auth_service.dto.request.GenerateTokenRequest;
import com.jobpulse.auth_service.dto.request.RefreshTokenRequest;
import com.jobpulse.auth_service.dto.response.TokenResponse;


public interface JwtServiceContract {
    TokenResponse generateToken(GenerateTokenRequest request);

    void revokeAllRefreshTokens(RefreshTokenRequest request);

    boolean validateToken(String token);

    String getSubjectFromToken(String token);
}
