-- =========================================================
-- Flyway V6: users.role for auth/authorization MVP
-- =========================================================

ALTER TABLE users
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER' AFTER phone;