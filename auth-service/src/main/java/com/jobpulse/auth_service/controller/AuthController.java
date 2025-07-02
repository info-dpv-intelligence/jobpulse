package com.jobpulse.auth_service.controller;

import com.jobpulse.auth_service.dto.RegisterRequest;
import com.jobpulse.auth_service.service.UserService;
import jakarta.validation.Valid;
import com.jobpulse.auth_service.dto.LoginRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }
}