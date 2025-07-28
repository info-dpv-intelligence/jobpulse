-- Change 'status' enum column to VARCHAR in job_post
ALTER TABLE job_post
    ALTER COLUMN status DROP DEFAULT,
    ALTER COLUMN status TYPE VARCHAR USING status::VARCHAR,
    ALTER COLUMN status SET NOT NULL;

-- Change 'revision_status' enum column to VARCHAR in job_post_content_v1
ALTER TABLE job_post_content_v1
    ALTER COLUMN revision_status DROP DEFAULT,
    ALTER COLUMN revision_status TYPE VARCHAR USING revision_status::VARCHAR,
    ALTER COLUMN revision_status SET NOT NULL;