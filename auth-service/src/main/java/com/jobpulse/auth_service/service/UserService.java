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

import com.jobpulse.auth_service.events.UserEvent;

import com.jobpulse.auth_service.model.RefreshToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserServiceContract {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtServiceContract jwtService;

    @Autowired
    private UserEventProducer userEventProducer;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public ServiceResult<UserRegistrationResponse> registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ServiceResult.failure("Email already in use", "EMAIL_EXISTS");
        }
        
        try {
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getRole());
            userRepository.save(user);
            
            UserEvent event = UserEvent.created(
                user.getId().toString(), 
                user.getEmail()
            );
            userEventProducer.sendUserEvent(event);

            UserRegistrationResponse response = UserRegistrationResponse.success(user.getId().toString());
            return ServiceResult.success(response);
        } catch (Exception e) {
            // @todo add logging
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
            // Create command objects for JWT operations
            GenerateTokenRequest tokenRequest = new GenerateTokenRequest(user.getId(), user.getRole(), user.getEmail());
            RefreshTokenRequest refreshRequest = new RefreshTokenRequest(user.getId());
            
            String jwt = jwtService.generateToken(tokenRequest);
            jwtService.revokeAllRefreshTokens(refreshRequest);
            RefreshToken refreshToken = jwtService.generateRefreshToken(refreshRequest);
            AuthResponse authResponse = new AuthResponse(jwt, refreshToken.getToken());

            return ServiceResult.success(authResponse);
        } catch (Exception e) {
            // @todo add logging
            return ServiceResult.failure("Authentication failed: " + e.getMessage(), "AUTH_ERROR");
        }
    }
}