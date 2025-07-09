package com.jobpulse.job_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.jobpulse.job_service.dto.*;
import com.jobpulse.job_service.model.JobPost;
import com.jobpulse.job_service.repository.JobPostRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test for JobService using factory pattern and SOLID principles.
 * Tests service behavior with proper dependency injection.
 */
@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobPostRepository jobPostRepository;

    private JobService jobService;

    @BeforeEach
    void setUp() {
        jobService = new JobService(jobPostRepository);
    }

    @Test
    void getJobListings_WithValidPageable_ShouldReturnSuccess() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        JobPost jobPost = createTestJobPost();
        Page<JobPost> page = new PageImpl<>(List.of(jobPost), pageable, 1);

        when(jobPostRepository.findAll(pageable)).thenReturn(page);

        // Act
        ServiceResult<JobListingsResponse> result = jobService.getJobListings(pageable);

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        verify(jobPostRepository).findAll(pageable);
    }

    @Test
    void createJob_WithValidCommand_ShouldReturnSuccess() {
        // Arrange
        CreateJobPostCommand command = new CreateJobPostCommand();
        command.setTitle("Software Engineer");
        command.setDescription("Java developer position");
        command.setJobPosterId(UUID.randomUUID().toString());

        JobPost savedJobPost = createTestJobPost();
        when(jobPostRepository.save(any(JobPost.class))).thenReturn(savedJobPost);

        // Act
        ServiceResult<CreatedResponse> result = jobService.createJob(command);

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(savedJobPost.getId().toString(), result.getData().getId());
        verify(jobPostRepository).save(any(JobPost.class));
    }

    @Test
    void updateJob_WithValidCommand_ShouldReturnSuccess() {
        // Arrange
        UUID jobId = UUID.randomUUID();
        UpdateJobPostCommand command = new UpdateJobPostCommand(
            jobId.toString(),
            "Updated Title",
            "Updated Description",
            true
        );

        JobPost existingJobPost = createTestJobPost();
        existingJobPost.setId(jobId);
        JobPost updatedJobPost = createTestJobPost();
        updatedJobPost.setId(jobId);
        updatedJobPost.setTitle("Updated Title");

        when(jobPostRepository.findById(jobId)).thenReturn(Optional.of(existingJobPost));
        when(jobPostRepository.save(any(JobPost.class))).thenReturn(updatedJobPost);

        // Act
        ServiceResult<JobPostUpdatedResponse> result = jobService.updateJob(command);

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        verify(jobPostRepository).findById(jobId);
        verify(jobPostRepository).save(any(JobPost.class));
    }

    @Test
    void updateJob_WithNonExistentJob_ShouldReturnFailure() {
        // Arrange
        UUID jobId = UUID.randomUUID();
        UpdateJobPostCommand command = new UpdateJobPostCommand(
            jobId.toString(),
            "Updated Title",
            "Updated Description",
            true
        );

        when(jobPostRepository.findById(jobId)).thenReturn(Optional.empty());

        // Act
        ServiceResult<JobPostUpdatedResponse> result = jobService.updateJob(command);

        // Assert
        assertTrue(result.isFailure());
        assertEquals("Job post not found", result.getErrorMessage());
        assertEquals("JOB_NOT_FOUND", result.getErrorCode());
        verify(jobPostRepository, never()).save(any(JobPost.class));
    }

    @Test
    void deleteJob_WithValidCommand_ShouldReturnSuccess() {
        // Arrange
        UUID jobId = UUID.randomUUID();
        DeleteJobPostCommand command = new DeleteJobPostCommand(jobId.toString());

        JobPost existingJobPost = createTestJobPost();
        existingJobPost.setId(jobId);

        when(jobPostRepository.findById(jobId)).thenReturn(Optional.of(existingJobPost));
        when(jobPostRepository.save(any(JobPost.class))).thenReturn(existingJobPost);

        // Act
        ServiceResult<DeletedResponse> result = jobService.deleteJob(command);

        // Assert
        assertTrue(result.isSuccess());
        verify(jobPostRepository).findById(jobId);
        verify(jobPostRepository).save(argThat(job -> !job.isActive())); // Verify soft delete
    }

    @Test
    void deleteJob_WithNonExistentJob_ShouldReturnFailure() {
        // Arrange
        UUID jobId = UUID.randomUUID();
        DeleteJobPostCommand command = new DeleteJobPostCommand(jobId.toString());

        when(jobPostRepository.findById(jobId)).thenReturn(Optional.empty());

        // Act
        ServiceResult<DeletedResponse> result = jobService.deleteJob(command);

        // Assert
        assertTrue(result.isFailure());
        assertEquals("Job post not found", result.getErrorMessage());
        assertEquals("JOB_NOT_FOUND", result.getErrorCode());
        verify(jobPostRepository, never()).save(any(JobPost.class));
    }

    @Test
    void createJob_WithRepositoryException_ShouldReturnFailure() {
        // Arrange
        CreateJobPostCommand command = new CreateJobPostCommand();
        command.setTitle("Software Engineer");
        command.setDescription("Java developer position");
        command.setJobPosterId(UUID.randomUUID().toString());

        when(jobPostRepository.save(any(JobPost.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ServiceResult<CreatedResponse> result = jobService.createJob(command);

        // Assert
        assertTrue(result.isFailure());
        assertTrue(result.getErrorMessage().contains("Failed to create job"));
        assertEquals("JOB_CREATION_ERROR", result.getErrorCode());
    }

    private JobPost createTestJobPost() {
        JobPost jobPost = new JobPost();
        jobPost.setId(UUID.randomUUID());
        jobPost.setTitle("Test Job");
        jobPost.setDescription("Test Description");
        jobPost.setJobPosterId(UUID.randomUUID());
        jobPost.setActive(true);
        jobPost.setCreatedAt(LocalDateTime.now());
        jobPost.setUpdatedAt(LocalDateTime.now());
        return jobPost;
    }
}
