DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'job_post_status') THEN
        CREATE TYPE job_post_status AS ENUM ('DRAFT', 'ACTIVE');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'revision_status') THEN
        CREATE TYPE revision_status AS ENUM ('DRAFT', 'ACTIVE');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'skeleton_type') THEN
        CREATE TYPE skeleton_type AS ENUM (
            'DESCRIPTION_ONLY_SKELETON',
            'DESCRIPTION_COMPANY_SKELETON',
            'DESCRIPTION_PREREQUISITES_SKELETON',
            'DESCRIPTION_COMPANY_PREREQUISITES_SKELETON'
        );
    END IF;
END
$$;
