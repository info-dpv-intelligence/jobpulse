package com.jobpulse.auth_service.service.module.jwt.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jobpulse.auth_service.repository.RefreshTokenRepository;
import com.jobpulse.auth_service.repository.UserRepository;
import com.jobpulse.auth_service.service.module.jwt.JwtService;
import com.jobpulse.auth_service.service.module.jwt.JwtServiceContract;
import com.jobpulse.auth_service.service.module.jwt.dto.JwtConfig;

@Component
public class JwtServiceFactory {

    private final String jwtSecret;
    private final long jwtExpirationMs;
    private final long refreshExpirationMs;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public JwtServiceFactory(
        @Value("${jwt.secret}") String jwtSecret,
        @Value("${jwt.expiration-ms}") long jwtExpirationMs,
        @Value("${refresh.expiration-ms:2592000000}") long refreshExpirationMs,
        RefreshTokenRepository refreshTokenRepository,
        UserRepository userRepository
    ) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationMs = jwtExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public JwtServiceContract createJwtService() {
        return new JwtService(
            new JwtConfig(
                this.jwtSecret,
                this.jwtExpirationMs,
                this.refreshExpirationMs
            ),
            this.refreshTokenRepository, 
            this.userRepository
        );
    }

}
