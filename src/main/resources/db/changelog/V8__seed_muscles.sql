--liquibase formatted sql

--changeset liftit:seed-muscles
-- Seed standard muscle groups; IDs 1-99 are reserved for system-defined muscles
-- created_by and updated_by reference id=1 (system user owns all reference data)
INSERT INTO muscles (id, name, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES
    (1,  'Abdominals', now(), 1, now(), 1),
    (2,  'Back',       now(), 1, now(), 1),
    (3,  'Biceps',     now(), 1, now(), 1),
    (4,  'Calves',     now(), 1, now(), 1),
    (5,  'Chest',      now(), 1, now(), 1),
    (6,  'Forearms',   now(), 1, now(), 1),
    (7,  'Neck',       now(), 1, now(), 1),
    (8,  'Shoulders',  now(), 1, now(), 1),
    (9,  'Thighs',     now(), 1, now(), 1),
    (10, 'Triceps',    now(), 1, now(), 1);
--rollback DELETE FROM muscles WHERE id BETWEEN 1 AND 10;
