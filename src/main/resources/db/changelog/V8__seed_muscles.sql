--liquibase formatted sql

--changeset liftit:seed-muscles
-- Seed standard muscle groups; IDs 1-99 are reserved for system-defined muscles
-- created_by and updated_by reference id=2 (admin user)
INSERT INTO muscles (id, name, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES
    (1,  'Abdominals', now(), 2, now(), 2),
    (2,  'Back',       now(), 2, now(), 2),
    (3,  'Biceps',     now(), 2, now(), 2),
    (4,  'Calves',     now(), 2, now(), 2),
    (5,  'Chest',      now(), 2, now(), 2),
    (6,  'Forearms',   now(), 2, now(), 2),
    (7,  'Neck',       now(), 2, now(), 2),
    (8,  'Shoulders',  now(), 2, now(), 2),
    (9,  'Thighs',     now(), 2, now(), 2),
    (10, 'Triceps',    now(), 2, now(), 2);
--rollback DELETE FROM muscles WHERE id BETWEEN 1 AND 10;
