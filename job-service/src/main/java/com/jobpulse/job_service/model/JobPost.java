package com.jobpulse.job_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "job_post")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPost {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "job_poster_id", nullable = false)
    private UUID jobPosterId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    public static JobPost create(
        String title,
        String description,
        UUID jobPosterId,
        Boolean isActive
    ) {
        JobPost jobPost = new JobPost();
        jobPost.setTitle(title);
        jobPost.setDescription(description);
        jobPost.setJobPosterId(jobPosterId);
        jobPost.setCreatedAt(LocalDateTime.now());
        jobPost.setUpdatedAt(LocalDateTime.now());
        jobPost.setActive(isActive);

        return jobPost;
    }
}