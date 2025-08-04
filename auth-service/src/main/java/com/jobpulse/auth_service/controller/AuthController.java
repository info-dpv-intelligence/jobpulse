package com.jobpulse.auth_service.controller;

import com.jobpulse.auth_service.dto.response.LoginResponse;
import com.jobpulse.auth_service.dto.response.UserRegistrationResponse;
import com.jobpulse.auth_service.service.UserServiceContract;
import jakarta.validation.Valid;

import com.jobpulse.auth_service.dto.request.LoginRequest;
import com.jobpulse.auth_service.dto.request.RegisterRequest;
import com.jobpulse.auth_service.dto.response.ControllerResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Authentication", description = "User registration and login operations")
public class AuthController {

    private UserServiceContract userService;

    @Autowired
    public AuthController(UserServiceContract userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(
        summary = "Register new user",
        description = "Create a new user account with username, email, and password"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User registered successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input or user already exists"
        )
    })
    public ControllerResponse<UserRegistrationResponse> register(
        @Parameter(description = "User registration details", required = true)
        @Valid @RequestBody RegisterRequest request) {
        
        UserRegistrationResponse response = userService.registerUser(request);
        return ControllerResponse.success(response);
    }

    @PostMapping("/login")
    @Operation(
        summary = "User login",
        description = "Authenticate user and return JWT tokens"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful"
        ),
                @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials (InvalidCredentialsException)"
        )
    })

    public ControllerResponse<LoginResponse> login(
        @Parameter(description = "User login credentials", required = true)
        @Valid @RequestBody LoginRequest request) {
        
        LoginResponse response = userService.login(request);
        return ControllerResponse.success(response);
    }
}