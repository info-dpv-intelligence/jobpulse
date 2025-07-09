package com.jobpulse.auth_service.service;

import com.jobpulse.auth_service.dto.*;
import com.jobpulse.auth_service.model.User;
import com.jobpulse.auth_service.model.RefreshToken;
import com.jobpulse.auth_service.model.UserRole;
import com.jobpulse.auth_service.repository.UserRepository;
import com.jobpulse.auth_service.service.module.event.publish.UserEventProducer;
import com.jobpulse.auth_service.service.module.jwt.JwtServiceContract;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtServiceContract jwtService;

    @Mock
    private UserEventProducer userEventProducer;

    @InjectMocks
    private UserService userService;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setEmail("test@example.com");
        validRegisterRequest.setPassword("password123");
        validRegisterRequest.setRole(UserRole.JOB_APPLICANT);

        validLoginRequest = new LoginRequest();
        validLoginRequest.setEmail("test@example.com");
        validLoginRequest.setPassword("password123");

        mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        mockUser.setEmail("test@example.com");
        mockUser.setPassword(new BCryptPasswordEncoder().encode("password123"));
        mockUser.setRole(UserRole.JOB_APPLICANT);
    }

    @Test
    void registerUser_WithValidRequest_ShouldReturnSuccess() {
        // Arrange
        when(userRepository.existsByEmail(validRegisterRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(mockUser.getId()); // Set the ID on the saved user
            return savedUser;
        });
        doNothing().when(userEventProducer).sendUserEvent(any());

        // Act
        ServiceResult<UserRegistrationResponse> result = userService.registerUser(validRegisterRequest);

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(mockUser.getId().toString(), result.getData().getUserId());
        assertEquals("User registered successfully", result.getData().getMessage());
        verify(userRepository).save(any(User.class));
        verify(userEventProducer).sendUserEvent(any());
    }

    @Test
    void registerUser_WithExistingEmail_ShouldReturnFailure() {
        // Arrange
        when(userRepository.existsByEmail(validRegisterRequest.getEmail())).thenReturn(true);

        // Act
        ServiceResult<UserRegistrationResponse> result = userService.registerUser(validRegisterRequest);

        // Assert
        assertTrue(result.isFailure());
        assertEquals("Email already in use", result.getErrorMessage());
        assertEquals("EMAIL_EXISTS", result.getErrorCode());
        verify(userRepository, never()).save(any());
        verify(userEventProducer, never()).sendUserEvent(any());
    }

    @Test
    void registerUser_WithRepositoryException_ShouldReturnFailure() {
        // Arrange
        when(userRepository.existsByEmail(validRegisterRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ServiceResult<UserRegistrationResponse> result = userService.registerUser(validRegisterRequest);

        // Assert
        assertTrue(result.isFailure());
        assertTrue(result.getErrorMessage().contains("Registration failed"));
        assertEquals("REGISTRATION_ERROR", result.getErrorCode());
    }

    @Test
    void login_WithValidCredentials_ShouldReturnSuccess() {
        // Arrange
        when(userRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(mockUser);
        when(jwtService.generateToken(any(GenerateTokenRequest.class))).thenReturn("mock-jwt-token");
        doNothing().when(jwtService).revokeAllRefreshTokens(any(RefreshTokenRequest.class));
        
        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken("mock-refresh-token");
        when(jwtService.generateRefreshToken(any(RefreshTokenRequest.class))).thenReturn(mockRefreshToken);

        // Act
        ServiceResult<AuthResponse> result = userService.login(validLoginRequest);

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("mock-jwt-token", result.getData().getAccessToken());
        assertEquals("mock-refresh-token", result.getData().getRefreshToken());
        verify(jwtService).generateToken(any(GenerateTokenRequest.class));
        verify(jwtService).revokeAllRefreshTokens(any(RefreshTokenRequest.class));
        verify(jwtService).generateRefreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    void login_WithInvalidEmail_ShouldReturnFailure() {
        // Arrange
        when(userRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(null);

        // Act
        ServiceResult<AuthResponse> result = userService.login(validLoginRequest);

        // Assert
        assertTrue(result.isFailure());
        assertEquals("Invalid credentials", result.getErrorMessage());
        assertEquals("INVALID_CREDENTIALS", result.getErrorCode());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_WithInvalidPassword_ShouldReturnFailure() {
        // Arrange
        validLoginRequest.setPassword("wrongpassword");
        when(userRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(mockUser);

        // Act
        ServiceResult<AuthResponse> result = userService.login(validLoginRequest);

        // Assert
        assertTrue(result.isFailure());
        assertEquals("Invalid credentials", result.getErrorMessage());
        assertEquals("INVALID_CREDENTIALS", result.getErrorCode());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_WithJwtServiceException_ShouldReturnFailure() {
        // Arrange
        when(userRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(mockUser);
        when(jwtService.generateToken(any(GenerateTokenRequest.class))).thenThrow(new RuntimeException("JWT error"));

        // Act
        ServiceResult<AuthResponse> result = userService.login(validLoginRequest);

        // Assert
        assertTrue(result.isFailure());
        assertTrue(result.getErrorMessage().contains("Authentication failed"));
        assertEquals("AUTH_ERROR", result.getErrorCode());
    }
}
