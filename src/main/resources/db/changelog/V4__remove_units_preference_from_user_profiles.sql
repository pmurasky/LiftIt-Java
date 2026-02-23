--liquibase formatted sql

--changeset liftit:remove-units-preference
ALTER TABLE user_profiles DROP CONSTRAINT chk_user_profiles_units_preference;
ALTER TABLE user_profiles DROP COLUMN units_preference;
--rollback ALTER TABLE user_profiles ADD COLUMN units_preference VARCHAR(10) NOT NULL DEFAULT 'imperial';
--rollback ALTER TABLE user_profiles ADD CONSTRAINT chk_user_profiles_units_preference CHECK (units_preference IN ('imperial'));
