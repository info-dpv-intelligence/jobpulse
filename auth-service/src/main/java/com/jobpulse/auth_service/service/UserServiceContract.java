package com.jobpulse.auth_service.service;

import com.jobpulse.auth_service.dto.request.LoginRequest;
import com.jobpulse.auth_service.dto.request.RegisterRequest;
import com.jobpulse.auth_service.dto.response.LoginResponse;
import com.jobpulse.auth_service.dto.response.UserRegistrationResponse;

public interface UserServiceContract {
    UserRegistrationResponse registerUser(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}
