package com.jobpulse.auth_service.factory;

import org.springframework.stereotype.Component;

import com.jobpulse.auth_service.service.module.password.PasswordServiceContract;
import com.jobpulse.auth_service.service.module.password.BCryptPasswordService;
import com.jobpulse.auth_service.service.module.jwt.JwtServiceContract;
import com.jobpulse.auth_service.service.module.jwt.factory.JwtServiceFactory;

/**
 * Default implementation of ServiceFactory.
 * Creates service instances using Spring dependency injection.
 */
@Component
public class DefaultServiceFactory implements ServiceFactory {
    
    private final JwtServiceFactory jwtServiceFactory;
    
    public DefaultServiceFactory(JwtServiceFactory jwtServiceFactory) {
        this.jwtServiceFactory = jwtServiceFactory;
    }
    
    @Override
    public PasswordServiceContract createPasswordService() {
        return new BCryptPasswordService();
    }
    
    @Override
    public JwtServiceContract createJwtService() {
        return jwtServiceFactory.createJwtService();
    }
}
