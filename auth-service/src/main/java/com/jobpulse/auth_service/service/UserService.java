package com.jobpulse.auth_service.service;

import com.jobpulse.auth_service.dto.RegisterRequest;
import com.jobpulse.auth_service.dto.LoginRequest;
import com.jobpulse.auth_service.dto.AuthResponse;
import com.jobpulse.auth_service.dto.GenerateTokenRequest;
import com.jobpulse.auth_service.dto.RefreshTokenRequest;
import com.jobpulse.auth_service.model.User;
import com.jobpulse.auth_service.repository.UserRepository;

import com.jobpulse.common_events.model.UserEvent;

import com.jobpulse.auth_service.model.RefreshToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
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

            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            // @todo add logging
            return ResponseEntity.internalServerError().body("Registration failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        
        // Create command objects for JWT operations
        GenerateTokenRequest tokenRequest = new GenerateTokenRequest(user.getId(), user.getRole(), user.getEmail());
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest(user.getId());
        
        String jwt = jwtService.generateToken(tokenRequest);
        jwtService.revokeAllRefreshTokens(refreshRequest);
        RefreshToken refreshToken = jwtService.generateRefreshToken(refreshRequest);
        AuthResponse authResponse = new AuthResponse(jwt, refreshToken.getToken());

        return ResponseEntity.ok(authResponse);
    }
}