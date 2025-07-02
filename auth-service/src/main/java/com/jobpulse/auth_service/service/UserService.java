package com.jobpulse.auth_service.service;

import com.jobpulse.auth_service.dto.RegisterRequest;
import com.jobpulse.auth_service.dto.LoginRequest;
import com.jobpulse.auth_service.dto.AuthResponse;
import com.jobpulse.auth_service.model.User;
import com.jobpulse.auth_service.repository.UserRepository;

import com.jobpulse.auth_service.model.RefreshToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
            // @todo: send event 
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            // @todo add logging
            return ResponseEntity.internalServerError().body("Registration failed: " + e.getMessage());
        }
    }

    public ResponseEntity<?> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        String jwt = jwtService.generateToken(user);
        jwtService.revokeAllRefreshTokens(user);
        RefreshToken refreshToken = jwtService.generateRefreshToken(user);
        AuthResponse authResponse = new AuthResponse(jwt, refreshToken.getToken());

        return ResponseEntity.ok(authResponse);
    }
}