package com.jobpulse.auth_service.controller;

import com.jobpulse.auth_service.dto.RegisterRequest;
import com.jobpulse.auth_service.dto.UserRegistrationResponse;
import com.jobpulse.auth_service.dto.AuthResponse;
import com.jobpulse.auth_service.service.UserServiceContract;
import jakarta.validation.Valid;
import com.jobpulse.auth_service.dto.LoginRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;

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
            description = "User registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserRegistrationResponse.class),
                examples = @ExampleObject(
                    value = "{\"userId\": \"123e4567-e89b-12d3-a456-426614174000\", \"message\": \"User registered successfully\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input or user already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"Email already in use\", \"code\": \"EMAIL_EXISTS\"}"
                )
            )
        )
    })
    public ResponseEntity<?> register(
        @Parameter(description = "User registration details", required = true)
        @Valid @RequestBody RegisterRequest request) {
        
        var result = userService.registerUser(request);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getData());
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                "error", result.getErrorMessage(),
                "code", result.getErrorCode()
            ));
        }
    }

    @PostMapping("/login")
    @Operation(
        summary = "User login",
        description = "Authenticate user and return JWT tokens"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class),
                examples = @ExampleObject(
                    value = "{\"accessToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"refreshToken\": \"refresh-token-here\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"Invalid credentials\", \"code\": \"INVALID_CREDENTIALS\"}"
                )
            )
        )
    })
    public ResponseEntity<?> login(
        @Parameter(description = "User login credentials", required = true)
        @Valid @RequestBody LoginRequest request) {
        
        var result = userService.login(request);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getData());
        } else {
            int statusCode = "INVALID_CREDENTIALS".equals(result.getErrorCode()) ? 401 : 400;
            return ResponseEntity.status(statusCode).body(Map.of(
                "error", result.getErrorMessage(),
                "code", result.getErrorCode()
            ));
        }
    }
}