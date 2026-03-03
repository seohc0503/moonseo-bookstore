-- =========================================================
-- Flyway V5: Email verification (pre-signup)
-- =========================================================

CREATE TABLE email_verifications (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  email       VARCHAR(255) NOT NULL,
  code        VARCHAR(20) NOT NULL,
  expires_at  DATETIME(6) NOT NULL,
  used_at     DATETIME(6) NULL,
  created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  UNIQUE KEY uk_email_verifications_email (email)
);