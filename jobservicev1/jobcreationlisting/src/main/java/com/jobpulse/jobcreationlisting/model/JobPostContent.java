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
@Table(name = "job_post_content")
public class JobPostContent {
    @Id
    @Column(name = "job_post_content_id", nullable = false)
    private UUID jobPostContentId;

    @OneToOne
    @JoinColumn(name = "job_post_skeleton_id", referencedColumnName="job_post_content_id", nullable = false)
    private UUID jobPostSkeletonId;

    @Enumerated(EnumType.STRING)
    @Column(name = "revision_status", nullable = false)
    private RevisionStatus revisionStatus;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;
}