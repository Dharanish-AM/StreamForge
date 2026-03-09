# StreamForge

StreamForge is a backend service built with **Play Framework (Scala)**.  
At this stage, the project is a foundation scaffold with database wiring, schema evolution, and a health endpoint.

---

## Current Status

Implemented:

- ✅ `GET /health` endpoint
- ✅ PostgreSQL integration via **Play-Slick**
- ✅ Play Evolutions enabled
- ✅ Initial `events` schema (`conf/evolutions/default/1.sql`)
- ✅ Base domain model: `app/models/Event.scala`

Planned (not implemented yet):

- ⏳ Event ingestion APIs
- ⏳ Repository implementations (`app/repositories`)
- ⏳ Service layer implementations (`app/services`)
- ⏳ Analytics/aggregation endpoints

---

## Tech Stack

- **Scala**: `2.13.18`
- **Play Framework**: Play Scala
- **Database Access**: Slick + Play-Slick + Play-Slick-Evolutions
- **Database**: PostgreSQL
- **Build Tool**: SBT
- **JDK**: 17+

---

## Repository Structure

```text
app/
  controllers/
    HealthController.scala
  models/
    Event.scala
  repositories/              # currently empty
  services/                  # currently empty
conf/
  evolutions/default/
    1.sql
  application.conf
  routes
build.sbt
```

---

## Prerequisites

Install and ensure availability of:

- JDK 17 or newer
- SBT
- PostgreSQL (local or reachable instance)

---

## Getting Started (Local)

### 1) Create database

```sql
CREATE DATABASE streamforge;
```

### 2) Configure app settings

Default dev values are already set in `conf/application.conf`.

Default local values:

- DB URL: `jdbc:postgresql://localhost:5432/streamforge`
- DB user: `dharanisham`
- DB password: *(empty)*
- Play secret key: `dev-only-change-me`
- Evolutions auto-apply: enabled for development

Optional overrides via environment variables:

```bash
export STREAMFORGE_DB_URL="jdbc:postgresql://localhost:5432/streamforge"
export STREAMFORGE_DB_USER="dharanisham"
export STREAMFORGE_DB_PASSWORD=""
export PLAY_HTTP_SECRET_KEY="dev-secret-for-local-run"
```

> On macOS (zsh), you can add these to `~/.zshrc` and run `source ~/.zshrc`.

### 3) Run the service

```bash
sbt run
```

Run on a custom port:

```bash
sbt -Dhttp.port=9001 run
```

---

## API Reference

### Health Check

- **Method**: `GET`
- **Path**: `/health`
- **Response**: `200 OK`
- **Body**: `OK`

Example:

```bash
curl http://localhost:9000/health
```

---

## Database Evolution

Initial evolution file:

- `conf/evolutions/default/1.sql`

### Schema created

`events` table:

- `id SERIAL PRIMARY KEY`
- `user_id INTEGER NOT NULL`
- `amount NUMERIC(18,2) NOT NULL`
- `event_type VARCHAR(100) NOT NULL`
- `created_at TIMESTAMP NOT NULL DEFAULT NOW()`

Indexes:

- `idx_events_user_id`
- `idx_events_created_at`
- `idx_events_event_type`

---

## Development Notes

- This codebase currently provides the base backend infrastructure.
- `repositories` and `services` directories are intentionally present for next-phase implementation.
- Evolutions are enabled to keep DB schema in sync during development.

---

## Next Suggested Milestones

1. Add event ingestion endpoint(s) (e.g., create/list events)
2. Implement repository layer with Slick queries
3. Add service layer validations/business rules
4. Add tests for controller, repository, and service layers
5. Introduce analytics endpoints (totals, trends, user/event breakdown)

---

## License

Add a license file (`LICENSE`) if this project is intended for public/open-source use.