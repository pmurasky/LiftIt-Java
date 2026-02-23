--liquibase formatted sql

--changeset liftit:create-users-table
CREATE TABLE users (
    id         UUID         NOT NULL DEFAULT gen_random_uuid(),
    username   VARCHAR(20)  NOT NULL,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email UNIQUE (email)
);
--rollback DROP TABLE users;
