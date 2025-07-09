package com.jobpulse.job_service.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jobpulse.job_service.repository.JobPostRepository;
import com.jobpulse.job_service.service.JobServiceContract;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for DefaultJobServiceFactory to ensure proper service creation
 * following factory pattern and dependency injection principles.
 */
@ExtendWith(MockitoExtension.class)
class DefaultJobServiceFactoryTest {

    @Mock
    private JobPostRepository jobPostRepository;

    @Test
    void createJobService_ShouldReturnJobServiceInstance() {
        // Arrange
        DefaultJobServiceFactory factory = new DefaultJobServiceFactory(jobPostRepository);

        // Act
        JobServiceContract jobService = factory.createJobService();

        // Assert
        assertNotNull(jobService);
    }

    @Test
    void constructor_WithNullRepository_ShouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            new DefaultJobServiceFactory(null);
        });
    }
}
