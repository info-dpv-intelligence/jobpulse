package com.jobpulse.auth_service.service.module.jwt;

import com.jobpulse.auth_service.dto.GenerateTokenRequest;
import com.jobpulse.auth_service.dto.RefreshTokenRequest;
import com.jobpulse.auth_service.model.User;
import com.jobpulse.auth_service.model.RefreshToken;
import com.jobpulse.auth_service.repository.RefreshTokenRepository;
import com.jobpulse.auth_service.repository.UserRepository;
import com.jobpulse.auth_service.service.module.jwt.dto.JwtConfig;

import io.jsonwebtoken.Jwts;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.time.Instant;
import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtException;
import java.nio.charset.StandardCharsets;

/**
 * JWT service implementation.
 * Handles JWT token generation, validation, and refresh token management.
 * This service is created via factory pattern rather than Spring annotation.
 */
public class JwtService implements JwtServiceContract {
    private JwtConfig jwtConfig;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(this.jwtConfig.jwtSecret().getBytes(StandardCharsets.UTF_8));
    }

    public JwtService(
        JwtConfig jwtConfig,
        RefreshTokenRepository refreshTokenRepository, 
        UserRepository userRepository
    ) {
        this.jwtConfig = jwtConfig;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String generateToken(GenerateTokenRequest request) {
        Instant now = Instant.now();

        return Jwts.builder()
            .subject(request.getUserId().toString())
            .claim("role", request.getRole().name())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(this.jwtConfig.jwtExpirationMs())))
            .signWith(getSigningKey())
            .compact();
    }

    @Override
    public void revokeAllRefreshTokens(RefreshTokenRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser(user);
        for (RefreshToken token : tokens) {
            if (!token.isRevoked()) {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            }
        }
    }

    @Override
    public RefreshToken generateRefreshToken(RefreshTokenRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        String token = generateSecureToken();
        Instant expiresAt = Instant.now().plusMillis(this.jwtConfig.refreshExpirationMs());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(expiresAt);
        refreshToken.setRevoked(false);
        refreshToken.setUser(user);

        return refreshTokenRepository.save(refreshToken);
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[64];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getSubjectFromToken(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }
}