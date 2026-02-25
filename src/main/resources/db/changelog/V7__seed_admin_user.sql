--liquibase formatted sql

--changeset liftit:seed-admin-user
-- Seed the admin user (id=2); IDs 1-99 are reserved for system accounts
-- auth0_id and email are placeholders — must be updated with real values after the Auth0 admin account is created
-- created_by and updated_by reference id=1 (system user owns the seed row)
INSERT INTO users (id, auth0_id, email, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (2, 'PLACEHOLDER_ADMIN_AUTH0_ID', 'admin@liftit.internal', now(), 1, now(), 1);
--rollback DELETE FROM users WHERE id = 2;
