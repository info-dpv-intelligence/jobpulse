package com.jobpulse.jobcreationlisting.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import java.util.UUID;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "company_details")
public class CompanyDetails {
    @Id
    @Column(name = "company_details_id", nullable = false)
    private UUID companyDetailsId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "tagline")
    private String tagline;

    @Column(name = "phone")
    private String phone;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;
}