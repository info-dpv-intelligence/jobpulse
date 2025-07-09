package com.jobpulse.auth_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jobpulse.auth_service.dto.RegisterRequest;
import com.jobpulse.auth_service.dto.LoginRequest;
import com.jobpulse.auth_service.dto.AuthResponse;
import com.jobpulse.auth_service.dto.GenerateTokenRequest;
import com.jobpulse.auth_service.dto.RefreshTokenRequest;
import com.jobpulse.auth_service.dto.ServiceResult;
import com.jobpulse.auth_service.dto.UserRegistrationResponse;
import com.jobpulse.auth_service.model.User;
import com.jobpulse.auth_service.model.RefreshToken;
import com.jobpulse.auth_service.model.UserRole;
import com.jobpulse.auth_service.repository.UserRepository;
import com.jobpulse.auth_service.factory.ServiceFactory;
import com.jobpulse.auth_service.service.module.jwt.JwtServiceContract;
import com.jobpulse.auth_service.service.module.password.PasswordServiceContract;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Contract tests for UserService focusing on business operations.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceContractTest {

    @Mock private UserRepository userRepository;
    @Mock private ServiceFactory serviceFactory;
    @Mock private PasswordServiceContract passwordService;
    @Mock private JwtServiceContract jwtService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        when(serviceFactory.createPasswordService()).thenReturn(passwordService);
        when(serviceFactory.createJwtService()).thenReturn(jwtService);
        
        userService = new UserService(userRepository, serviceFactory);
    }

    @Test
    void registerUser_ValidInput_ReturnsSuccess() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordService.encode("password123")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(createTestUser());

        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setRole(UserRole.JOB_APPLICANT);

        ServiceResult<UserRegistrationResponse> result = userService.registerUser(request);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        verify(passwordService).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_ExistingEmail_ReturnsFailure() {
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password123");
        request.setRole(UserRole.JOB_APPLICANT);

        ServiceResult<UserRegistrationResponse> result = userService.registerUser(request);

        assertFalse(result.isSuccess());
        assertEquals("EMAIL_EXISTS", result.getErrorCode());
        assertEquals("Email already in use", result.getErrorMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_RepositoryException_ReturnsFailure() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordService.encode("password123")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setRole(UserRole.JOB_APPLICANT);

        ServiceResult<UserRegistrationResponse> result = userService.registerUser(request);

        assertFalse(result.isSuccess());
        assertEquals("REGISTRATION_ERROR", result.getErrorCode());
        assertTrue(result.getErrorMessage().contains("Registration failed"));
    }

    @Test
    void login_ValidCredentials_ReturnsTokens() {
        User user = createTestUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(passwordService.matches("password123", user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(any(GenerateTokenRequest.class))).thenReturn("jwt_token");
        when(jwtService.generateRefreshToken(any(RefreshTokenRequest.class))).thenReturn(createTestRefreshToken());

        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        ServiceResult<AuthResponse> result = userService.login(request);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("jwt_token", result.getData().getAccessToken());
        assertNotNull(result.getData().getRefreshToken());
        verify(jwtService).generateToken(any(GenerateTokenRequest.class));
        verify(jwtService).revokeAllRefreshTokens(any(RefreshTokenRequest.class));
        verify(jwtService).generateRefreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    void login_InvalidCredentials_ReturnsFailure() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);

        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrong_password");

        ServiceResult<AuthResponse> result = userService.login(request);

        assertFalse(result.isSuccess());
        assertEquals("INVALID_CREDENTIALS", result.getErrorCode());
        assertEquals("Invalid credentials", result.getErrorMessage());
        verify(jwtService, never()).generateToken(any(GenerateTokenRequest.class));
    }

    @Test
    void login_WrongPassword_ReturnsFailure() {
        User user = createTestUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(passwordService.matches("wrong_password", user.getPassword())).thenReturn(false);

        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrong_password");

        ServiceResult<AuthResponse> result = userService.login(request);

        assertFalse(result.isSuccess());
        assertEquals("INVALID_CREDENTIALS", result.getErrorCode());
        verify(jwtService, never()).generateToken(any(GenerateTokenRequest.class));
    }

    @Test
    void login_TokenGenerationException_ReturnsFailure() {
        User user = createTestUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(passwordService.matches("password123", user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(any(GenerateTokenRequest.class))).thenThrow(new RuntimeException("Token generation failed"));

        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        ServiceResult<AuthResponse> result = userService.login(request);

        assertFalse(result.isSuccess());
        assertEquals("AUTH_ERROR", result.getErrorCode());
        assertTrue(result.getErrorMessage().contains("Authentication failed"));
    }

    private User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setPassword("hashed_password");
        user.setRole(UserRole.JOB_APPLICANT);
        return user;
    }

    private RefreshToken createTestRefreshToken() {
        RefreshToken token = new RefreshToken();
        token.setToken("refresh_token_123");
        token.setExpiresAt(Instant.now().plusSeconds(86400));
        token.setRevoked(false);
        return token;
    }
}
