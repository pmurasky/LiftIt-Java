--liquibase formatted sql

--changeset liftit:create-muscles-table
CREATE TABLE muscles (
    id         BIGINT                   NOT NULL,
    name       VARCHAR(20)              NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by BIGINT                   NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by BIGINT                   NOT NULL,
    CONSTRAINT pk_muscles PRIMARY KEY (id),
    CONSTRAINT uq_muscles_name UNIQUE (name),
    CONSTRAINT fk_muscles_created_by FOREIGN KEY (created_by) REFERENCES users (id),
    CONSTRAINT fk_muscles_updated_by FOREIGN KEY (updated_by) REFERENCES users (id)
);
--rollback DROP TABLE muscles;
