package com.jobpulse.job_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.oauth2.jwt.Jwt;

@Getter
@AllArgsConstructor
public class UserContext {
    private final String id;

    public static UserContext fromJwt(Jwt jwt) {
        return new UserContext(
            jwt.getSubject()
        );
    }
}