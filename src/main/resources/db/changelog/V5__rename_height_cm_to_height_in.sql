--liquibase formatted sql

--changeset liftit:rename-height-cm-to-height-in
ALTER TABLE user_profiles RENAME COLUMN height_cm TO height_in;
--rollback ALTER TABLE user_profiles RENAME COLUMN height_in TO height_cm;
