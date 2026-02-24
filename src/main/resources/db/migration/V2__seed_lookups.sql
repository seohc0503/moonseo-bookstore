-- =========================================================
-- Flyway V2__seed_lookups.sql
-- Lookup / Code Seed
-- MySQL 8.0.20+ compatible (VALUES() 미사용)
-- =========================================================

-- =========================================================
-- 1) 사용자 상태 코드
-- =========================================================
INSERT INTO user_status (code) VALUES
  ('ACTIVE'),
  ('WITHDRAWN')
ON DUPLICATE KEY UPDATE
  code = code;

-- =========================================================
-- 2) 도서 상태 코드
-- =========================================================
INSERT INTO book_status (code) VALUES
  ('ON_SALE'),
  ('OUT_OF_STOCK'),
  ('HIDDEN')
ON DUPLICATE KEY UPDATE
  code = code;

-- =========================================================
-- 3) 주문 상태 코드
-- =========================================================
INSERT INTO order_status (code) VALUES
  ('PLACED'),
  ('PAID'),
  ('CANCELED')
ON DUPLICATE KEY UPDATE
  code = code;

-- =========================================================
-- 4) 배송 상태 코드
-- =========================================================
INSERT INTO shipment_status (code) VALUES
  ('READY'),
  ('SHIPPED'),
  ('DELIVERED'),
  ('RETURNED')
ON DUPLICATE KEY UPDATE
  code = code;

-- =========================================================
-- 5) 문의 상태 코드
-- =========================================================
INSERT INTO inquiry_status (code) VALUES
  ('PENDING'),
  ('ANSWERED')
ON DUPLICATE KEY UPDATE
  code = code;

-- =========================================================
-- 6) 결제 수단 (MVP 기준)
-- =========================================================
-- 정책:
-- - MVP에서는 KAKAOPAY만 활성화
-- - 나머지는 확장 대비 비활성 상태로 유지
INSERT INTO payment_methods (code, name, active) VALUES
  ('KAKAOPAY', 'KakaoPay', TRUE),
  ('CARD',     '신용/체크카드', FALSE),
  ('NAVERPAY', '네이버페이',   FALSE),
  ('TOSS',     '토스페이',     FALSE)
AS new
ON DUPLICATE KEY UPDATE
  name   = new.name,
  active = new.active;

-- =========================================================
-- 7) Code-only enum (application-managed)
-- =========================================================

-- payments.status (code-only enum)
-- allowed values:
--   READY     : 결제 요청 생성
--   SUCCESS   : 결제 승인 완료
--   FAILED    : 결제 실패
--   CANCELED  : 전액 환불 완료

-- refunds.status (code-only enum)
-- allowed values:
--   REQUESTED   : 환불 요청됨
--   PROCESSING  : 환불 처리 중
--   COMPLETED   : 환불 완료
--   FAILED      : 환불 실패

-- inventory_ledger.reason_code (code-only enum)
-- ORDER   : 주문으로 인한 재고 차감
-- VOID    : 결제 SUCCESS 이후, 출고 전 주문 무효화 복구
-- RETURN  : 반품으로 인한 재고 복원
-- MANUAL  : 관리자 수동 재고 조정

-- shipments.carrier (code-only enum)
-- CJ / LOGEN / POST / HANJIN

-- files.owner_type (code-only enum)
-- NOTICE / INQUIRY / REVIEW

-- =========================================================
-- End of Flyway V2
-- =========================================================