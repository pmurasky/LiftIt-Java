--liquibase formatted sql

--changeset liftit:create-exercise-categories-table
CREATE TABLE exercise_categories (
    id         BIGINT                   NOT NULL,
    name       VARCHAR(50)              NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by BIGINT                   NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by BIGINT                   NOT NULL,
    CONSTRAINT pk_exercise_categories PRIMARY KEY (id),
    CONSTRAINT uq_exercise_categories_name UNIQUE (name),
    CONSTRAINT fk_exercise_categories_created_by FOREIGN KEY (created_by) REFERENCES users (id),
    CONSTRAINT fk_exercise_categories_updated_by FOREIGN KEY (updated_by) REFERENCES users (id)
);
--rollback DROP TABLE exercise_categories;

--changeset liftit:seed-exercise-categories
-- Seed system-defined exercise categories; IDs 1-99 are reserved
-- created_by and updated_by reference id=1 (system user owns all reference data)
INSERT INTO exercise_categories (id, name, created_at, created_by, updated_at, updated_by)
OVERRIDING SYSTEM VALUE
VALUES (1, 'Strength', now(), 1, now(), 1);
--rollback DELETE FROM exercise_categories WHERE id = 1;
