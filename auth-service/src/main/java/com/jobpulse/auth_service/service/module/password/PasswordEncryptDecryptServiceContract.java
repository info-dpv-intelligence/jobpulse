package com.jobpulse.auth_service.service.module.password;

public interface PasswordEncryptDecryptServiceContract {
    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
