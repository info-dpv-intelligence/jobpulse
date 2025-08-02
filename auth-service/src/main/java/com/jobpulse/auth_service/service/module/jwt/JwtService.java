package com.jobpulse.auth_service.service.module.jwt;

import com.jobpulse.auth_service.dto.request.GenerateTokenRequest;
import com.jobpulse.auth_service.dto.request.RefreshTokenRequest;
import com.jobpulse.auth_service.dto.response.TokenResponse;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtException;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService implements JwtServiceContract {
    private final JwtConfig jwtConfig;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public JwtService(
        JwtConfig jwtConfig,
        RefreshTokenRepository refreshTokenRepository,
        UserRepository userRepository
    ) {
        this.jwtConfig = jwtConfig;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = this.jwtConfig.jwtSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public TokenResponse generateToken(GenerateTokenRequest request) {
        revokeAllRefreshTokens(RefreshTokenRequest.builder()
            .userId(request.getUserId())
            .build());

        Instant now = Instant.now();
        String accessToken = Jwts.builder()
            .subject(request.getUserId().toString())
            .claim("email", request.getEmail())
            .claim("role", request.getRole().name())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(this.jwtConfig.jwtExpirationMs())))
            .signWith(getSigningKey())
            .compact();

        RefreshToken refreshToken = generateRefreshToken(RefreshTokenRequest.builder()
            .userId(request.getUserId())
            .build());

        return TokenResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken.getToken())
            .build();
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

    private RefreshToken generateRefreshToken(RefreshTokenRequest request) {
        User user = userRepository.findById(request.getUserId())
        //TODO: user error placement move it to userservice
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