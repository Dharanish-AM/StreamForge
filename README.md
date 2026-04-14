# StreamForge

StreamForge is a simple event-tracking backend built with Play Framework + Scala.

This README is written for beginners and focuses on:
- what each layer does,
- how request flow works,
- and how to run and test the API quickly.

## What This App Does

The app stores events in PostgreSQL and exposes CRUD APIs:
- create an event,
- list all events,
- get one event by id,
- update an event,
- delete an event.

## Tech Stack

- Scala `2.13.18`
- Play Framework
- Slick + Play-Slick
- PostgreSQL
- SBT

## Project Layout (Beginner View)

```text
app/
  controllers/      # HTTP layer: request/response handling
  services/         # business rules and validation logic
  repositories/     # database queries (Slick)
  tables/           # Slick table mapping
  models/           # domain model (Event)
  dto/              # input/output API payloads
  modules/          # startup wiring (Flyway runner module)
conf/
  routes            # endpoint -> controller mapping
  application.conf  # app and DB configuration
  db/migration/     # Flyway database migrations
public/
  index.html        # frontend console UI
  app.js            # frontend behavior (fetch, filters, dashboard)
  style.css         # UI styling
```

## Request Flow

For `POST /api/events` (same idea for other APIs):

1. `EventController` validates JSON and builds `Event`.
2. `EventService` applies business checks.
3. `EventRepository` runs Slick query.
4. Controller returns JSON response.

Frontend flow (`GET /`) uses `public/app.js` to call `/api/events`, render cards, apply client-side filters/sorting, and compute dashboard counters.

## Prerequisites

- JDK 17+
- SBT
- PostgreSQL

## Run Locally

1. Create DB:

```sql
CREATE DATABASE streamforge;
```

2. Check DB config in `conf/application.conf`.

3. Start app:

```bash
sbt run
```

4. App runs on:
- `http://localhost:9000`

If port `9000` is already in use, run on another port:

```bash
sbt -Dhttp.port=9001 run
```

## API Endpoints

- `GET /api/health`
- `POST /api/events`
- `GET /api/events`
- `GET /api/events/:id`
- `PUT /api/events/:id`
- `DELETE /api/events/:id`

### Example: Create Event

Amount values in examples below are in INR.

```bash
curl -X POST http://localhost:9000/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "amount": 450.00,
    "eventType": "purchase"
  }'
```

### Example: Get All Events

```bash
curl http://localhost:9000/api/events
```

## Error Format

Errors are returned as JSON:

```json
{
  "message": "Event with id 10 not found"
}
```

## Database Migrations (Flyway)

- `conf/db/migration/V1__create_events_table.sql`: creates `events` table + indexes.
- `conf/db/migration/V2__events_created_at_to_timestamptz.sql`: converts `created_at` to timezone-aware timestamp.

## Frontend Notes

- Search filters by event type, user ID, and event ID.
- Type/user filters apply live in the event feed.
- Sorting supports newest, oldest, amount high/low, and type A-Z.
- Dashboard counters are computed from fetched events:
  - total events,
  - total volume,
  - average amount,
  - unique users.
- Feed info shows visible event count versus total event count.

## Next Improvements (Good Beginner Tasks)

1. Add unit tests for service methods.
2. Add integration tests for controller endpoints.
3. Add input validation rules (e.g., `amount > 0`).
4. Add filtering APIs (by `userId`, `eventType`, date range).