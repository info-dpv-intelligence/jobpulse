package com.jobpulse.auth_service.service.module.password;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for BCryptPasswordService to validate password encoding and matching.
 * Tests the concrete implementation of PasswordServiceContract.
 */
class BCryptPasswordServiceTest {

    private BCryptPasswordService passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new BCryptPasswordService();
    }

    @Test
    void encode_WithValidPassword_ShouldReturnBCryptHash() {
        // Arrange
        String rawPassword = "testPassword123";

        // Act
        String encodedPassword = passwordService.encode(rawPassword);

        // Assert
        assertNotNull(encodedPassword);
        assertTrue(encodedPassword.startsWith("$2a$")); // BCrypt format
        assertNotEquals(rawPassword, encodedPassword); // Should be encoded
    }

    @Test
    void matches_WithMatchingPasswords_ShouldReturnTrue() {
        // Arrange
        String rawPassword = "testPassword123";
        String encodedPassword = passwordService.encode(rawPassword);

        // Act
        boolean matches = passwordService.matches(rawPassword, encodedPassword);

        // Assert
        assertTrue(matches);
    }

    @Test
    void matches_WithNonMatchingPasswords_ShouldReturnFalse() {
        // Arrange
        String rawPassword = "testPassword123";
        String wrongPassword = "wrongPassword456";
        String encodedPassword = passwordService.encode(rawPassword);

        // Act
        boolean matches = passwordService.matches(wrongPassword, encodedPassword);

        // Assert
        assertFalse(matches);
    }

    @Test
    void encode_WithEmptyPassword_ShouldReturnEncodedHash() {
        // Arrange
        String emptyPassword = "";

        // Act
        String encodedPassword = passwordService.encode(emptyPassword);

        // Assert
        assertNotNull(encodedPassword);
        assertTrue(encodedPassword.startsWith("$2a$"));
    }

    @Test
    void encode_WithSamePasswordTwice_ShouldReturnDifferentHashes() {
        // Arrange
        String password = "samePassword";

        // Act
        String hash1 = passwordService.encode(password);
        String hash2 = passwordService.encode(password);

        // Assert
        assertNotEquals(hash1, hash2); // BCrypt uses salt, so hashes should differ
        assertTrue(passwordService.matches(password, hash1));
        assertTrue(passwordService.matches(password, hash2));
    }
}
