package com.jobpulse.auth_service.service;

import com.jobpulse.auth_service.dto.GenerateTokenRequest;
import com.jobpulse.auth_service.dto.RefreshTokenRequest;
import com.jobpulse.auth_service.model.RefreshToken;

/**
 * Service contract interface for JWT token operations.
 * Defines the contract for JWT token management including
 * token generation, validation, and refresh token operations.
 */
public interface JwtServiceContract {
    
    /**
     * Generates a JWT access token for the given user request.
     *
     * @param request the token generation request containing user details
     * @return the generated JWT token string
     */
    String generateToken(GenerateTokenRequest request);
    
    /**
     * Revokes all refresh tokens for the given user.
     *
     * @param request the refresh token request containing user ID
     */
    void revokeAllRefreshTokens(RefreshTokenRequest request);
    
    /**
     * Generates a new refresh token for the given user.
     *
     * @param request the refresh token request containing user ID
     * @return the generated refresh token entity
     */
    RefreshToken generateRefreshToken(RefreshTokenRequest request);
    
    /**
     * Validates the given JWT token.
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String token);
    
    /**
     * Extracts the subject (user ID) from the given JWT token.
     *
     * @param token the JWT token from which to extract the subject
     * @return the subject string (typically user ID)
     */
    String getSubjectFromToken(String token);
}
