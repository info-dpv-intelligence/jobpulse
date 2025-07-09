package com.jobpulse.auth_service.service.module.jwt.dto;

public record JwtConfig(
    String jwtSecret,
    long jwtExpirationMs,
    long refreshExpirationMs
) {

}
