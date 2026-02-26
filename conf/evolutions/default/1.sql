CREATE TABLE "users" (
    "id" BIGSERIAL PRIMARY KEY,
    "name" VARCHAR NOT NULL,
    "email" VARCHAR NOT NULL
);

INSERT INTO "users" ("name", "email") VALUES ('Admin', 'admin@example.com');
