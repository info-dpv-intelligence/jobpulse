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
import com.jobpulse.auth_service.service.module.jwt.JwtServiceContract;
import com.jobpulse.auth_service.service.module.password.PasswordServiceContract;
import com.jobpulse.auth_service.factory.ServiceFactory;
import com.jobpulse.auth_service.model.RefreshToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserServiceContract {
    private final UserRepository userRepository;
    private final JwtServiceContract jwtService;
    private final PasswordServiceContract passwordService;

    @Autowired
    public UserService(
        UserRepository userRepository,
        ServiceFactory serviceFactory
    ) {
        this.userRepository = userRepository;
        this.jwtService = serviceFactory.createJwtService();
        this.passwordService = serviceFactory.createPasswordService();
    }

    @Override
    @Transactional
    public ServiceResult<UserRegistrationResponse> registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ServiceResult.failure("Email already in use", "EMAIL_EXISTS");
        }
        
        try {
            User user = User.register(request.getEmail(), 
                                    passwordService.encode(request.getPassword()), 
                                    request.getRole());
            user = userRepository.save(user);
            user.confirmRegistration();
            userRepository.save(user);

            UserRegistrationResponse response = UserRegistrationResponse.success(user.getId().toString());
            
            return ServiceResult.success(response);
        } catch (Exception e) {
            return ServiceResult.failure("Registration failed: " + e.getMessage(), "REGISTRATION_ERROR");
        }
    }

    @Override
    public ServiceResult<AuthResponse> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null || !passwordService.matches(request.getPassword(), user.getPassword())) {
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
}