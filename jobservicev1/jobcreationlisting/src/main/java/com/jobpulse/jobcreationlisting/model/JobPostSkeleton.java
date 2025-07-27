package com.jobpulse.jobcreationlisting.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.UUID;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_post_skeleton")
public class JobPostSkeleton {
    @Id
    @Column(name = "job_post_skeleton_id", nullable = false)
    private UUID jobPostSkeletonId;

    @Enumerated(EnumType.STRING)
    @Column(name = "skeleton_type", nullable = false)
    private SkeletonType skeletonType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RevisionStatus status;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;
}