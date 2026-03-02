# StreamForge

StreamForge is a Play Framework backend service written in Scala.

Current implementation includes:
- Health check endpoint
- PostgreSQL connectivity via Play-Slick
- Play Evolutions with initial `events` schema

## Tech Stack

- Scala 2.13.18
- Play Framework (Play Scala)
- Slick + Play-Slick + Play-Slick-Evolutions
- PostgreSQL
- SBT

## Current Scope

Implemented now:
- `GET /health` endpoint returning `OK`
- Initial evolution creating `events` table and indexes
- Base `Event` domain model (`app/models/Event.scala`)

Not yet implemented:
- Event ingestion APIs
- Repository/service logic in `app/repositories` and `app/services`
- Analytics endpoints

## Project Layout

```text
app/
  controllers/
    HealthController.scala
  models/
    Event.scala
  repositories/          # currently empty
  services/              # currently empty
conf/
  routes
  application.conf
  evolutions/default/1.sql
build.sbt
```

## Prerequisites

- JDK 17+
- SBT
- PostgreSQL running locally

## Local Development

### 1) Create database

```sql
CREATE DATABASE streamforge;
```

### 2) Configure application settings

Default development values already exist in `conf/application.conf`:
- DB URL: `jdbc:postgresql://localhost:5432/streamforge`
- DB user: `dharanisham`
- DB password: empty
- Play secret key: `dev-only-change-me`
- Evolutions auto-apply: enabled

You can override any value with environment variables (for example in shell or `.env`):

```bash
export STREAMFORGE_DB_URL="jdbc:postgresql://localhost:5432/streamforge"
export STREAMFORGE_DB_USER="dharanisham"
export STREAMFORGE_DB_PASSWORD=""
export PLAY_HTTP_SECRET_KEY="dev-secret-for-local-run"
```

### 3) Run the app

```bash
sbt run
```

Optional custom port:

```bash
sbt -Dhttp.port=9001 run
```

## API

### Health

- Method: `GET`
- Path: `/health`
- Response: `200 OK`
- Body: `OK`

Example:

```bash
curl http://localhost:9000/health
```

## Database Evolution

Initial evolution: `conf/evolutions/default/1.sql`

Creates:
- Table: `events`
  - `id SERIAL PRIMARY KEY`
  - `user_id INTEGER NOT NULL`
  - `amount NUMERIC(18,2) NOT NULL`
  - `event_type VARCHAR(100) NOT NULL`
  - `created_at TIMESTAMP NOT NULL DEFAULT NOW()`
- Indexes:
  - `idx_events_user_id`
  - `idx_events_created_at`
  - `idx_events_event_type`

## Notes

- This repository is currently a foundation backend scaffold.
- `app/repositories` and `app/services` are intentionally empty as the next implementation step.
