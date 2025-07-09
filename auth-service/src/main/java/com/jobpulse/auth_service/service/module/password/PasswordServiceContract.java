package com.jobpulse.auth_service.service.module.password;

/**
 * Service contract interface for password operations.
 * Defines the contract for password encoding and validation functionality.
 */
public interface PasswordServiceContract {
    
    /**
     * Encodes a raw password using the configured encoding strategy.
     *
     * @param rawPassword the raw password to encode
     * @return the encoded password
     */
    String encode(String rawPassword);
    
    /**
     * Verifies a raw password against an encoded password.
     *
     * @param rawPassword the raw password to verify
     * @param encodedPassword the encoded password to compare against
     * @return true if the passwords match, false otherwise
     */
    boolean matches(String rawPassword, String encodedPassword);
}
