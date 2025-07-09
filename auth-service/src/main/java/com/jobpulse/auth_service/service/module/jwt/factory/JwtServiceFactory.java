package com.jobpulse.auth_service.service.module.jwt.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.jobpulse.auth_service.repository.RefreshTokenRepository;
import com.jobpulse.auth_service.repository.UserRepository;
import com.jobpulse.auth_service.service.module.jwt.JwtService;
import com.jobpulse.auth_service.service.module.jwt.JwtServiceContract;
import com.jobpulse.auth_service.service.module.jwt.dto.JwtConfig;

public class JwtServiceFactory {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${refresh.expiration-ms:2592000000}")
    private long refreshExpirationMs;

    // TODO: check if using autowiring is utilising the container injectino
    // aka is this stadnard and ensuring DI
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

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
