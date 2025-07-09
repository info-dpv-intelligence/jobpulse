package com.jobpulse.auth_service.service.module.password;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * BCrypt implementation of password service.
 * Uses BCrypt algorithm for password encoding and verification.
 */
@Service
public class BCryptPasswordService implements PasswordServiceContract {
    
    private final BCryptPasswordEncoder passwordEncoder;
    
    public BCryptPasswordService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
