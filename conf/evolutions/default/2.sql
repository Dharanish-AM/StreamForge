# --- !Ups

ALTER TABLE events
  ALTER COLUMN created_at TYPE TIMESTAMPTZ
  USING created_at AT TIME ZONE 'UTC';

# --- !Downs

ALTER TABLE events
  ALTER COLUMN created_at TYPE TIMESTAMP
  USING created_at AT TIME ZONE 'UTC';