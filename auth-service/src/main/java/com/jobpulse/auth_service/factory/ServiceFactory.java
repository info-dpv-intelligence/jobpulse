package com.jobpulse.auth_service.factory;

import com.jobpulse.auth_service.service.module.password.PasswordServiceContract;
import com.jobpulse.auth_service.service.module.jwt.JwtServiceContract;

/**
 * Abstract factory for creating service instances.
 * Provides a centralized way to create and configure services
 * while maintaining dependency injection principles.
 */
public interface ServiceFactory {
    
    /**
     * Creates a password service instance.
     *
     * @return configured password service
     */
    PasswordServiceContract createPasswordService();
    
    /**
     * Creates a JWT service instance.
     *
     * @return configured JWT service
     */
    JwtServiceContract createJwtService();
}
