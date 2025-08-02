package com.jobpulse.auth_service.service.module.jwt.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtConfig(
    String jwtSecret,
    long jwtExpirationMs,
    long refreshExpirationMs
) {
}
