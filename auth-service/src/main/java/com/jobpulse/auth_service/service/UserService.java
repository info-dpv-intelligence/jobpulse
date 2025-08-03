package com.jobpulse.auth_service.service;

import com.jobpulse.auth_service.dto.request.RegisteringUserAction;
import com.jobpulse.auth_service.dto.request.GenerateTokenRequest;
import com.jobpulse.auth_service.dto.request.LoginRequest;
import com.jobpulse.auth_service.dto.request.RegisterRequest;
import com.jobpulse.auth_service.dto.response.LoginResponse;
import com.jobpulse.auth_service.dto.response.TokenResponse;
import com.jobpulse.auth_service.dto.response.UserRegistrationResponse;
import com.jobpulse.auth_service.exception.InvalidCredentialsException;
import com.jobpulse.auth_service.exception.UserAlreadyExistsException;
import com.jobpulse.auth_service.model.User;
import com.jobpulse.auth_service.repository.UserRepository;
import com.jobpulse.auth_service.service.module.event.UserRegistrationDomainLayerContract;
import com.jobpulse.auth_service.service.module.jwt.JwtServiceContract;
import com.jobpulse.auth_service.service.module.password.PasswordEncryptDecryptServiceContract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserServiceContract {
    private final UserRepository userRepository;
    private final JwtServiceContract jwtService;
    private final PasswordEncryptDecryptServiceContract passwordService;
    private final UserRegistrationDomainLayerContract userRegistrationDomainLayer;

    @Autowired
    public UserService(
        UserRepository userRepository,
        JwtServiceContract jwtService,
        PasswordEncryptDecryptServiceContract passwordService,
        UserRegistrationDomainLayerContract userRegistrationDomainLayer
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordService = passwordService;
        this.userRegistrationDomainLayer = userRegistrationDomainLayer;
    }

    @Override
    @Transactional
    public UserRegistrationResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail());
        }
        
        try {
            RegisteringUserAction action = RegisteringUserAction.builder()
                .email(request.getEmail())
                .encodedPassword(passwordService.encode(request.getPassword()))
                .role(request.getRole())
                .build();

            User user = userRegistrationDomainLayer.touchUserForRegistration(action);
            user = userRepository.save(user);

            return UserRegistrationResponse.builder().userId(user.getId().toString()).build();
        } catch (Exception e) {
            userRegistrationDomainLayer.rollback();
            throw e;
        }
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null || !passwordService.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        
        try {
            TokenResponse tokenResponse = jwtService.generateToken(
                GenerateTokenRequest
                    .builder()
                        .email(user.getEmail())
                        .role(user.getRole())
                        .userId(user.getId())
                    .build()
                );

            return LoginResponse.builder().tokens(tokenResponse).build();
        } catch (Exception e) {
            throw new InvalidCredentialsException("Authentication failed: " + e.getMessage());
        }
    }
}