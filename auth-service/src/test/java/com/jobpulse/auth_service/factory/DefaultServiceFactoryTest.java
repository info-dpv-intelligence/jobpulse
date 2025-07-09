package com.jobpulse.auth_service.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jobpulse.auth_service.service.module.jwt.factory.JwtServiceFactory;
import com.jobpulse.auth_service.service.module.jwt.JwtServiceContract;
import com.jobpulse.auth_service.service.module.password.PasswordServiceContract;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test for DefaultServiceFactory to ensure proper service creation
 * following factory pattern and dependency injection principles.
 */
@ExtendWith(MockitoExtension.class)
class DefaultServiceFactoryTest {

    @Mock
    private JwtServiceFactory jwtServiceFactory;

    @Mock
    private JwtServiceContract mockJwtService;

    @Test
    void createPasswordService_ShouldReturnBCryptPasswordService() {
        // Arrange
        DefaultServiceFactory factory = new DefaultServiceFactory(jwtServiceFactory);

        // Act
        PasswordServiceContract passwordService = factory.createPasswordService();

        // Assert
        assertNotNull(passwordService);
        assertTrue(passwordService.encode("test").startsWith("$2a$")); // BCrypt hash starts with $2a$
        assertTrue(passwordService.matches("test", passwordService.encode("test")));
    }

    @Test
    void createJwtService_ShouldDelegateToJwtServiceFactory() {
        // Arrange
        when(jwtServiceFactory.createJwtService()).thenReturn(mockJwtService);
        DefaultServiceFactory factory = new DefaultServiceFactory(jwtServiceFactory);

        // Act
        JwtServiceContract jwtService = factory.createJwtService();

        // Assert
        assertNotNull(jwtService);
        assertSame(mockJwtService, jwtService);
        verify(jwtServiceFactory).createJwtService();
    }

    @Test
    void constructor_WithNullJwtServiceFactory_ShouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            new DefaultServiceFactory(null);
        });
    }
}
