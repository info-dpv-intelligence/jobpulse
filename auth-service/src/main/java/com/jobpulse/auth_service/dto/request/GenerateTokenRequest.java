package com.jobpulse.auth_service.dto.request;

import com.jobpulse.auth_service.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Builder
@Data
@AllArgsConstructor
public class GenerateTokenRequest {
    private UUID userId;
    private UserRole role;
    private String email;
}
