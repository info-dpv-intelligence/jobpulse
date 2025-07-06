package com.jobpulse.auth_service.service;

import com.jobpulse.auth_service.dto.RegisterRequest;
import com.jobpulse.auth_service.dto.LoginRequest;
import com.jobpulse.auth_service.dto.AuthResponse;
import com.jobpulse.auth_service.dto.GenerateTokenRequest;
import com.jobpulse.auth_service.dto.RefreshTokenRequest;
import com.jobpulse.auth_service.dto.ServiceResult;
import com.jobpulse.auth_service.dto.UserRegistrationResponse;
import com.jobpulse.auth_service.model.User;
import com.jobpulse.auth_service.repository.UserRepository;
import com.jobpulse.auth_service.model.RefreshToken;
import com.jobpulse.auth_service.domain.PublishDomainEvents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService implements UserServiceContract {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtServiceContract jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    @PublishDomainEvents(async = true)
    public ServiceResult<UserRegistrationResponse> registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ServiceResult.failure("Email already in use", "EMAIL_EXISTS");
        }
        
        try {
            User user = User.register(request.getEmail(), 
                                    passwordEncoder.encode(request.getPassword()), 
                                    request.getRole());
            user = userRepository.save(user);
            user.confirmRegistration();

            UserRegistrationResponse response = UserRegistrationResponse.success(user.getId().toString());
            // Domain events will be published asynchronously by the aspect
            
            return ServiceResult.success(response);
        } catch (Exception e) {
            return ServiceResult.failure("Registration failed: " + e.getMessage(), "REGISTRATION_ERROR");
        }
    }

    @Override
    public ServiceResult<AuthResponse> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ServiceResult.failure("Invalid credentials", "INVALID_CREDENTIALS");
        }
        
        try {
            GenerateTokenRequest tokenRequest = new GenerateTokenRequest(user.getId(), user.getRole(), user.getEmail());
            RefreshTokenRequest refreshRequest = new RefreshTokenRequest(user.getId());
            
            String jwt = jwtService.generateToken(tokenRequest);
            jwtService.revokeAllRefreshTokens(refreshRequest);
            RefreshToken refreshToken = jwtService.generateRefreshToken(refreshRequest);
            AuthResponse authResponse = new AuthResponse(jwt, refreshToken.getToken());

            return ServiceResult.success(authResponse);
        } catch (Exception e) {
            return ServiceResult.failure("Authentication failed: " + e.getMessage(), "AUTH_ERROR");
        }
    }
    
    @Transactional
    @PublishDomainEvents
    public ServiceResult<Void> updateUserProfile(String userId, String newEmail) {
        try {
            User user = userRepository.findById(UUID.fromString(userId)).orElse(null);
            if (user == null) {
                return ServiceResult.failure("User not found", "USER_NOT_FOUND");
            }
            
            user = userRepository.save(user);
            
            return ServiceResult.success(null);
        } catch (Exception e) {
            return ServiceResult.failure("Update failed: " + e.getMessage(), "UPDATE_ERROR");
        }
    }
    
    @Transactional
    @PublishDomainEvents
    public ServiceResult<Void> complexBusinessOperation(String userId) {
        try {
            User user = userRepository.findById(UUID.fromString(userId)).orElse(null);
            if (user == null) {
                return ServiceResult.failure("User not found", "USER_NOT_FOUND");
            }
            
            user = userRepository.save(user);
            
            return ServiceResult.success(null);
        } catch (Exception e) {
            return ServiceResult.failure("Operation failed: " + e.getMessage(), "OPERATION_ERROR");
        }
    }
}