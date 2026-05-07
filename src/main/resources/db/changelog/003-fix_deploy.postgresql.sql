-- liquibase formatted sql

-- changeset root:1778143837689-1 splitStatements:false
ALTER TABLE "review" ALTER COLUMN "created_date" TYPE TIMESTAMP WITHOUT TIME ZONE USING ("created_date"::TIMESTAMP WITHOUT TIME ZONE);

-- changeset root:1778143837689-2 splitStatements:false
ALTER TABLE "member_follow" ALTER COLUMN "follow_date" TYPE TIMESTAMP WITHOUT TIME ZONE USING ("follow_date"::TIMESTAMP WITHOUT TIME ZONE);

