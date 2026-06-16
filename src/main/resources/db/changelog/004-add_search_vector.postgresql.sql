-- liquibase formatted sql

-- changeset kwsni:2 splitStatements:false
ALTER TABLE "series"
    ADD COLUMN "search_vector" tsvector GENERATED ALWAYS AS (
        to_tsvector('english', coalesce("name", ''))
    ) STORED;

-- rollback ALTER TABLE "series" DROP COLUMN "search_vector";

-- changeset kwsni:3 splitStatements:false
CREATE INDEX "series_fts" ON "series" USING GIN ("search_vector");
-- rollback DROP INDEX "series_fts";

-- changeset kwsni:4 splitStatements:false
ALTER TABLE "member"
    ADD COLUMN "search_vector" tsvector GENERATED ALWAYS AS (
        setweight(to_tsvector('english', coalesce("username", '')), 'A') ||
        setweight(to_tsvector('english', coalesce("first_name", '')), 'B') ||
        setweight(to_tsvector('english', coalesce("last_name", '')), 'B')
    ) STORED;
-- rollback ALTER TABLE "member" DROP COLUMN "search_vector";

-- changeset kwsni:5 splitStatements:false
CREATE INDEX "member_fts" ON "member" USING GIN ("search_vector");
-- rollback DROP INDEX "member_fts";

