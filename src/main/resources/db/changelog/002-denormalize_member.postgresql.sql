-- changeset root:1778140738313-1 splitStatements:false
ALTER TABLE "review" DROP CONSTRAINT IF EXISTS "fkjgdueyhtoh57bqbb14v7n339c";

-- changeset root:1778140738313-3 splitStatements:false
ALTER TABLE "episode" DROP CONSTRAINT IF EXISTS "fkne5crolail6seo3t87d3qr64d";

-- changeset root:1778140738313-15 splitStatements:false
DROP INDEX IF EXISTS "fki_F";

-- changeset root:1778140738313-16 splitStatements:false
DROP INDEX IF EXISTS "fki_foreign_key_series_id";

-- changeset root:1778140738313-2 splitStatements:false
ALTER TABLE "review" ADD CONSTRAINT "fkjgdueyhtoh57bqbb14v7n339c" FOREIGN KEY ("series_tvdb_id") REFERENCES "series" ("tvdb_id") ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset root:1778140738313-4 splitStatements:false
ALTER TABLE "episode" ADD CONSTRAINT "fkne5crolail6seo3t87d3qr64d" FOREIGN KEY ("series_tvdb_id") REFERENCES "series" ("tvdb_id") ON UPDATE NO ACTION ON DELETE NO ACTION;

-- changeset root:1778140738313-5 splitStatements:false
ALTER TABLE "review" ALTER COLUMN "created_date" TYPE TIMESTAMP WITHOUT TIME ZONE USING ("created_date"::TIMESTAMP WITHOUT TIME ZONE);

-- changeset root:1778140738313-6 splitStatements:false
ALTER TABLE "member" ALTER COLUMN  "email" SET NOT NULL;

-- changeset root:1778140738313-7 splitStatements:false
ALTER TABLE "member_follow" ALTER COLUMN "follow_date" TYPE TIMESTAMP WITHOUT TIME ZONE USING ("follow_date"::TIMESTAMP WITHOUT TIME ZONE);

-- changeset root:1778140738313-8 splitStatements:false
ALTER TABLE "review" ALTER COLUMN  "is_generated" DROP NOT NULL;

-- changeset root:1778140738313-9 splitStatements:false
ALTER TABLE "member" ALTER COLUMN  "username" SET NOT NULL;

-- changeset root:1778140738313-10 splitStatements:false
DO $$ DECLARE constraint_name varchar;
BEGIN
  SELECT tc.CONSTRAINT_NAME into strict constraint_name
    FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc
    WHERE CONSTRAINT_TYPE = 'PRIMARY KEY'
      AND TABLE_NAME = 'review_comment_like' AND TABLE_SCHEMA = 'public';
    EXECUTE 'alter table "public"."review_comment_like" drop constraint "' || constraint_name || '"';
END $$;

-- changeset root:1778140738313-11 splitStatements:false
ALTER TABLE "review_comment_like" ADD CONSTRAINT "review_comment_like_pkey" PRIMARY KEY ("member_id", "review_comment_id");

-- changeset root:1778140738313-12 splitStatements:false
DO $$ DECLARE constraint_name varchar;
BEGIN
  SELECT tc.CONSTRAINT_NAME into strict constraint_name
    FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc
    WHERE CONSTRAINT_TYPE = 'PRIMARY KEY'
      AND TABLE_NAME = 'review_like' AND TABLE_SCHEMA = 'public';
    EXECUTE 'alter table "public"."review_like" drop constraint "' || constraint_name || '"';
END $$;

-- changeset root:1778140738313-13 splitStatements:false
ALTER TABLE "review_like" ADD CONSTRAINT "review_like_pkey" PRIMARY KEY ("member_id", "review_id");

-- changeset kwsni:1 splitStatements:false
UPDATE "member"
  SET "email" = (
    SELECT "email"
    FROM "users"
    WHERE "users"."id" = "member"."id"
  ),
  "username" = (
    SELECT "username"
    FROM "users"
    WHERE "users"."id" = "member"."id"
  ),
  "password" = (
    SELECT "password"
    FROM "users"
    WHERE "users"."id" = "member"."id"
  ),
  "role" = (
    SELECT "role"
    FROM "users"
    WHERE "users"."id" = "member"."id"
  ),
  "is_generated" = (
    SELECT "is_generated"
    FROM "users"
    WHERE "users"."id" = "member"."id"
  );

-- changeset root:1778140738313-14 splitStatements:false
DROP TABLE "users";

