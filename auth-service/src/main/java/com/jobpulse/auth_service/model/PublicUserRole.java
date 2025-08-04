package com.jobpulse.auth_service.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The role of the user in the system.")
public enum PublicUserRole {
    JOB_POSTER,
    JOB_APPLICANT,
}