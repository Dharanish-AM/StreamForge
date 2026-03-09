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
  errors/           # custom exceptions + JSON error handler
conf/
  routes            # endpoint -> controller mapping
  application.conf  # app and DB configuration
  evolutions/       # database migrations
```

## Request Flow

For `POST /api/events` (same idea for other APIs):

1. `EventController` validates JSON and builds `Event`.
2. `EventService` applies business checks.
3. `EventRepository` runs Slick query.
4. Controller returns JSON response.

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

## API Endpoints

- `GET /api/health`
- `POST /api/events`
- `GET /api/events`
- `GET /api/events/:id`
- `PUT /api/events/:id`
- `DELETE /api/events/:id`

### Example: Create Event

```bash
curl -X POST http://localhost:9000/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "amount": 99.99,
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
  "error": "not_found",
  "message": "Event with id 10 not found"
}
```

## Database Evolutions

- `conf/evolutions/default/1.sql`: creates `events` table + indexes.
- `conf/evolutions/default/2.sql`: converts `created_at` to timezone-aware timestamp.

## Next Improvements (Good Beginner Tasks)

1. Add unit tests for service methods.
2. Add integration tests for controller endpoints.
3. Add input validation rules (e.g., `amount > 0`).
4. Add filtering APIs (by `userId`, `eventType`, date range).