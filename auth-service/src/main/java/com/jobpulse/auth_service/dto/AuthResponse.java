package com.jobpulse.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "Response containing JWT tokens after successful authentication")
@Data
@AllArgsConstructor
public class AuthResponse {
    
    @Schema(description = "JWT access token for API authentication", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
    private String accessToken;
    
    @Schema(description = "Refresh token for obtaining new access tokens", 
            example = "refresh-token-string-here")
    private String refreshToken;
}