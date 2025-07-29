package com.jobpulse.auth_service.factory;

import com.jobpulse.auth_service.service.module.password.PasswordServiceContract;
import com.jobpulse.auth_service.service.module.jwt.JwtServiceContract;

public interface ServiceFactory {
    PasswordServiceContract createPasswordService();
    JwtServiceContract createJwtService();
}
