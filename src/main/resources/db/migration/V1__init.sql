-- V1__init.sql (Flyway)
-- Schema: Moonseo Book Store (MVP)

-- 세션 문자셋 (클라이언트/연결용)
SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 세션 기본 스토리지 엔진(InnoDB) 명시
SET SESSION default_storage_engine = InnoDB;

-- =========================================================
-- 0) 룩업/코드 테이블 (UNIQUE(code) 즉시 부여)
-- =========================================================
create TABLE user_status (
  id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(20) NOT NULL,

  UNIQUE KEY uk_user_status_code (code)
);

create TABLE book_status (
  id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(20) NOT NULL,

  UNIQUE KEY uk_book_status_code (code)
);

create TABLE order_status (
  id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(20) NOT NULL,

  UNIQUE KEY uk_order_status_code (code)
);

create TABLE shipment_status (
  id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(20) NOT NULL,

  UNIQUE KEY uk_shipment_status_code (code)
);

create TABLE inquiry_status (
  id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(20) NOT NULL,

  UNIQUE KEY uk_inquiry_status_code (code)
);

create TABLE payment_methods (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  code       VARCHAR(20)  NOT NULL,
  name       VARCHAR(100) NOT NULL,
  active     TINYINT(1) NOT NULL DEFAULT 1,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  UNIQUE KEY uk_payment_methods_code (code)
);

-- =========================================================
-- 1) 사용자/프로필
-- =========================================================
create TABLE users (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  status_id     BIGINT NOT NULL,
  email         VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NULL,
  name          VARCHAR(100) NOT NULL,
  phone         VARCHAR(20) NOT NULL,

  last_login_at DATETIME(6) NULL,
  created_at    DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at    DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  UNIQUE KEY uk_users_email (email),

  CONSTRAINT fk_users_user_status
    FOREIGN KEY (status_id)
    REFERENCES user_status(id)
    ON delete RESTRICT
    ON update RESTRICT
);

create TABLE user_social (
  id               BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id          BIGINT NOT NULL,
  provider         VARCHAR(50) NOT NULL,
  provider_user_id VARCHAR(100) NOT NULL,
  linked_at        DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  UNIQUE KEY uk_user_social_provider_pair (provider, provider_user_id),
  UNIQUE KEY uk_user_social_provider (user_id, provider),

  CONSTRAINT fk_user_social_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON delete RESTRICT
    ON update RESTRICT
);

create TABLE user_address (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id     BIGINT NOT NULL,
  label       VARCHAR(50)  NOT NULL,
  recipient   VARCHAR(100) NOT NULL,
  phone       VARCHAR(20)  NOT NULL,
  zip         VARCHAR(10)  NOT NULL,
  addr1       VARCHAR(255) NOT NULL,
  addr2       VARCHAR(255) NULL,

  is_default_shipping TINYINT(1) NOT NULL DEFAULT 0,
  is_default_shipping_flag TINYINT GENERATED ALWAYS AS (
    CASE WHEN is_default_shipping = 1 THEN 1 ELSE NULL END
	) STORED,

  created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  UNIQUE KEY uk_user_address_default_ship (user_id, is_default_shipping_flag),

  CONSTRAINT fk_user_address_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON delete RESTRICT
    ON update RESTRICT
);

-- =========================================================
-- 2) 카테고리/도서
-- =========================================================
create TABLE categories (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  parent_id     BIGINT NULL,
  name          VARCHAR(100) NOT NULL,
  display_order INT NOT NULL DEFAULT 0,
  created_at    DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at    DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  CONSTRAINT fk_categories_parent
    FOREIGN KEY (parent_id)
    REFERENCES categories(id)
    ON delete RESTRICT
    ON update RESTRICT
);

create TABLE books (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_id   BIGINT NOT NULL,
  status_id     BIGINT NOT NULL,

  isbn13        CHAR(13) NOT NULL,
  title         VARCHAR(255) NOT NULL,
  subtitle      VARCHAR(255) NULL,
  author        VARCHAR(255) NOT NULL,
  publisher     VARCHAR(255) NOT NULL,
  published_at  DATE NULL,

  price         INT NOT NULL,
  sale_price    INT NOT NULL,
  currency      CHAR(3) NOT NULL DEFAULT 'KRW',
  stock_qty     INT NOT NULL DEFAULT 0,

  thumbnail_url VARCHAR(1024) NULL,
  ext_source    VARCHAR(50) NULL,
  ext_payload   JSON NULL,

  created_at    DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at    DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  UNIQUE KEY uk_books_isbn13 (isbn13),

  CONSTRAINT fk_books_category
    FOREIGN KEY (category_id)
    REFERENCES categories(id)
    ON delete RESTRICT
    ON update RESTRICT,
  CONSTRAINT fk_books_book_status
    FOREIGN KEY (status_id)
    REFERENCES book_status(id)
    ON delete RESTRICT
    ON update RESTRICT
);

-- =========================================================
-- 3) 장바구니
-- =========================================================
create TABLE carts (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id    BIGINT NOT NULL,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  UNIQUE KEY uk_carts_user (user_id),

  CONSTRAINT fk_carts_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON delete RESTRICT
    ON update RESTRICT
);

create TABLE cart_items (
  id             BIGINT PRIMARY KEY AUTO_INCREMENT,
  cart_id        BIGINT NOT NULL,
  book_id        BIGINT NOT NULL,
  qty            INT NOT NULL DEFAULT 1,
  price_snapshot INT NOT NULL,
  created_at     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  UNIQUE KEY uk_cart_items_cart_book (cart_id, book_id),

  CONSTRAINT fk_cart_items_cart
    FOREIGN KEY (cart_id)
    REFERENCES carts(id)
    ON delete RESTRICT
    ON update RESTRICT,
  CONSTRAINT fk_cart_items_book
    FOREIGN KEY (book_id)
    REFERENCES books(id)
    ON delete RESTRICT
    ON update RESTRICT
);

-- =========================================================
-- 4) 주문/주문아이템
-- =========================================================
create TABLE orders (
  id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id            BIGINT NOT NULL,
  status_id          BIGINT NOT NULL,
  payment_method_id  BIGINT NOT NULL,
  order_no           VARCHAR(50) NOT NULL,

  items_amount       INT NOT NULL,
  shipping_fee       INT NOT NULL DEFAULT 0,
  discount_amount    INT NOT NULL DEFAULT 0,
  pay_amount         INT GENERATED ALWAYS AS (
    items_amount + shipping_fee - discount_amount
  ) STORED,

  receiver_name      VARCHAR(100) NOT NULL,
  receiver_phone     VARCHAR(20)  NOT NULL,
  addr_zip           VARCHAR(10)  NOT NULL,
  addr1              VARCHAR(255) NOT NULL,
  addr2              VARCHAR(255) NULL,
  memo               VARCHAR(255) NULL,
  currency           CHAR(3) NOT NULL DEFAULT 'KRW',

  canceled_at        DATETIME(6) NULL,
  created_at         DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at         DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  UNIQUE KEY uk_orders_order_no (order_no),

  CONSTRAINT fk_orders_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON delete RESTRICT
    ON update RESTRICT,
  CONSTRAINT fk_orders_payment_method
    FOREIGN KEY (payment_method_id)
    REFERENCES payment_methods(id)
    ON delete RESTRICT
    ON update RESTRICT,
  CONSTRAINT fk_orders_status
    FOREIGN KEY (status_id)
    REFERENCES order_status(id)
    ON delete RESTRICT
    ON update RESTRICT
);

create TABLE order_items (
  id             BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id       BIGINT NOT NULL,
  book_id        BIGINT NOT NULL,
  title_snapshot VARCHAR(255) NOT NULL,
  unit_price     INT NOT NULL,
  qty            INT NOT NULL DEFAULT 1,
  subtotal       INT NOT NULL,
  currency       CHAR(3) NOT NULL DEFAULT 'KRW',
  created_at     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  UNIQUE KEY uk_order_items_order_book (order_id, book_id),

  CONSTRAINT fk_order_items_order
    FOREIGN KEY (order_id)
    REFERENCES orders(id)
    ON delete RESTRICT
    ON update RESTRICT,
  CONSTRAINT fk_order_items_book
    FOREIGN KEY (book_id)
    REFERENCES books(id)
    ON delete RESTRICT
    ON update RESTRICT
);

-- =========================================================
-- 5) 결제/환불
-- =========================================================
create TABLE payments (
  id              BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id        BIGINT NOT NULL,
  transaction_id  VARCHAR(100) NOT NULL,
  amount          INT NOT NULL,
  currency        CHAR(3) NOT NULL DEFAULT 'KRW',
  status          VARCHAR(20) NOT NULL,
  created_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  paid_at         DATETIME(6) NULL,
  failed_at       DATETIME(6) NULL,
  canceled_at     DATETIME(6) NULL,
  raw_payload     JSON NULL,

 -- 주문당 결제 SUCCESS 1건 보장용 생성 컬럼
  success_flag TINYINT GENERATED ALWAYS AS (
    CASE WHEN status = 'SUCCESS' THEN 1 ELSE NULL END
  ) STORED,

  UNIQUE KEY uk_payments_txid (transaction_id),
  UNIQUE KEY uk_payments_order_success (order_id, success_flag),

  CONSTRAINT fk_payments_order
    FOREIGN KEY (order_id)
    REFERENCES orders(id)
    ON delete RESTRICT
    ON update RESTRICT
);

create TABLE refunds (
  id             BIGINT PRIMARY KEY AUTO_INCREMENT,
  payment_id     BIGINT NOT NULL,
  amount         INT NOT NULL,
  reason         VARCHAR(255) NOT NULL,
  status         VARCHAR(20) NOT NULL,
  requested_at   DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  processed_at   DATETIME(6) NULL,
  raw_payload    JSON NULL,

  CONSTRAINT fk_refunds_payment
    FOREIGN KEY (payment_id)
    REFERENCES payments(id)
    ON delete RESTRICT
    ON update RESTRICT
);

create TABLE refund_items (
  id             BIGINT PRIMARY KEY AUTO_INCREMENT,
  refund_id      BIGINT NOT NULL,
  order_item_id  BIGINT NOT NULL,
  qty            INT    NOT NULL,
  amount         INT    NOT NULL,
  created_at     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  UNIQUE KEY uk_refund_items_refund_order_item (refund_id, order_item_id),

  CONSTRAINT fk_refund_items_refund
    FOREIGN KEY (refund_id)
    REFERENCES refunds(id)
    ON delete RESTRICT
    ON update RESTRICT,
  CONSTRAINT fk_refund_items_order_item
    FOREIGN KEY (order_item_id)
    REFERENCES order_items(id)
    ON delete RESTRICT
    ON update RESTRICT
);

-- =========================================================
-- 6) 배송
-- =========================================================
create TABLE shipments (              -- OrderStatus=PAID 상태에서만 최초 생성 가능
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id      BIGINT NOT NULL,
  status_id     BIGINT NOT NULL,
  carrier       VARCHAR(50) NOT NULL, -- CJ/LOGEN/POST/HANJIN
  tracking_no   VARCHAR(50) NOT NULL, -- 운송장 번호가 확인 되면 테이블 생성
  shipped_at    DATETIME(6) NULL,
  delivered_at  DATETIME(6) NULL,
  created_at    DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at    DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  UNIQUE KEY uk_shipments_order (order_id),  -- 주문당 출고 1건 보장

  CONSTRAINT fk_shipments_order
    FOREIGN KEY (order_id)
    REFERENCES orders(id)
    ON delete RESTRICT
    ON update RESTRICT,
  CONSTRAINT fk_shipments_status
    FOREIGN KEY (status_id)
    REFERENCES shipment_status(id)
    ON delete RESTRICT
    ON update RESTRICT
);

-- =========================================================
-- 7) 재고 원장
-- =========================================================
create TABLE inventory_ledger (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  book_id      BIGINT NOT NULL,
  change_qty   INT NOT NULL,
  reason_code  VARCHAR(20) NOT NULL,   -- ORDER/VOID/RETURN/MANUAL
  ref_type     VARCHAR(50) NOT NULL,   -- 'OrderItem' 등
  ref_id       BIGINT NOT NULL,
  occurred_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  CONSTRAINT fk_inventory_ledger_book
    FOREIGN KEY (book_id)
    REFERENCES books(id)
    ON delete RESTRICT
    ON update RESTRICT
);

-- =========================================================
-- 8) 위시리스트/리뷰
-- =========================================================
create TABLE wishlists (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id    BIGINT NOT NULL,
  book_id    BIGINT NOT NULL,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  UNIQUE KEY uk_wishlists_user_book (user_id, book_id),

  CONSTRAINT fk_wishlists_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON delete RESTRICT
    ON update RESTRICT,
  CONSTRAINT fk_wishlists_book
    FOREIGN KEY (book_id)
    REFERENCES books(id)
    ON delete RESTRICT
    ON update RESTRICT
);

create TABLE reviews (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id     BIGINT NOT NULL,
  book_id     BIGINT NOT NULL,
  rating      INT NOT NULL,
  title       VARCHAR(255) NOT NULL,
  content     TEXT NOT NULL,
  created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  UNIQUE KEY uk_reviews_user_book (user_id, book_id),

  CONSTRAINT fk_reviews_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON delete RESTRICT
    ON update RESTRICT,
  CONSTRAINT fk_reviews_book
    FOREIGN KEY (book_id)
    REFERENCES books(id)
    ON delete RESTRICT
    ON update RESTRICT
);

-- =========================================================
-- 9) 게시판/고객센터
-- =========================================================
create TABLE notices (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  title      VARCHAR(255) NOT NULL,
  content    TEXT NOT NULL,
  pinned     TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);

create TABLE faqs (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  question      VARCHAR(255) NOT NULL,
  answer        TEXT NOT NULL,
  display_order INT NOT NULL DEFAULT 0,
  created_at    DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at    DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);

create TABLE inquiries (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id     BIGINT NOT NULL,
  status_id   BIGINT NOT NULL,
  subject     VARCHAR(255) NOT NULL,
  content     TEXT NOT NULL,
  created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  CONSTRAINT fk_inquiries_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON delete RESTRICT
    ON update RESTRICT,
  CONSTRAINT fk_inquiries_status
    FOREIGN KEY (status_id)
    REFERENCES inquiry_status(id)
    ON delete RESTRICT
    ON update RESTRICT
);

create TABLE inquiry_answers (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  inquiry_id  BIGINT NOT NULL,
  admin_id    BIGINT NOT NULL,
  content     TEXT NOT NULL,
  created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  CONSTRAINT fk_inquiry_answers_inquiry
    FOREIGN KEY (inquiry_id)
    REFERENCES inquiries(id)
    ON delete RESTRICT
    ON update RESTRICT,
  CONSTRAINT fk_inquiry_answers_admin
    FOREIGN KEY (admin_id)
    REFERENCES users(id)
    ON delete RESTRICT
    ON update RESTRICT
);

-- =========================================================
-- 10) 파일/토큰/감사로그
-- =========================================================
create TABLE files (
  id             BIGINT PRIMARY KEY AUTO_INCREMENT,
  owner_type     VARCHAR(50) NOT NULL,
  owner_id       BIGINT NOT NULL,
  original_name  VARCHAR(255) NOT NULL,
  stored_name    VARCHAR(255) NOT NULL,
  mime_type      VARCHAR(100) NOT NULL,
  size           BIGINT NOT NULL,
  path_or_url    VARCHAR(1024) NOT NULL,
  checksum       CHAR(64) NOT NULL,
  created_at     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
);

create TABLE refresh_tokens (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id     BIGINT NOT NULL,
  token_hash  CHAR(64) NOT NULL,
  expires_at  DATETIME(6) NOT NULL,
  revoked_at  DATETIME(6) NULL,
  created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  UNIQUE KEY uk_refresh_tokens_hash (token_hash),

  CONSTRAINT fk_refresh_tokens_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON delete RESTRICT
    ON update RESTRICT
);

create TABLE admin_logs (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  admin_id     BIGINT NOT NULL,
  action       VARCHAR(100) NOT NULL,
  entity_type  VARCHAR(100) NOT NULL,
  entity_id    BIGINT NOT NULL,
  created_at   DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

  CONSTRAINT fk_admin_logs_admin
    FOREIGN KEY (admin_id)
    REFERENCES users(id)
    ON delete RESTRICT
    ON update RESTRICT
);

-- =========================================================
-- 11) 매출 통계 View (순매출 기준)
-- =========================================================
drop view IF EXISTS sales_stats;

create view sales_stats as
select
  t.stat_date,
  t.book_id,
  cast(sum(t.qty_delta) as SIGNED)           as qty,
  cast(round(sum(t.amount_delta)) as SIGNED) as amount
from (
  -- + 매출 (결제 성공 / 결제일 기준)
  select
    date(p.paid_at)        as stat_date,
    oi.book_id             as book_id,
    SUM(oi.qty)            AS qty_delta,
    SUM(oi.subtotal)       AS amount_delta
  FROM payments p
  JOIN orders o       ON o.id = p.order_id
  JOIN order_items oi ON oi.order_id = o.id
  WHERE p.status = 'SUCCESS'
    AND p.paid_at IS NOT NULL
  GROUP BY DATE(p.paid_at), oi.book_id

  UNION ALL

  -- - 환불 (환불 완료 / 처리일 기준, refund_items 기반 정확 차감)
  select
    date(r.processed_at)   AS stat_date,
    oi.book_id             AS book_id,
    - SUM(ri.qty)          AS qty_delta,
    - SUM(ri.amount)       AS amount_delta
  FROM refunds r
  JOIN refund_items ri ON ri.refund_id = r.id
  JOIN order_items oi  ON oi.id = ri.order_item_id
  WHERE r.status = 'COMPLETED'
    AND r.processed_at IS NOT NULL
  GROUP BY DATE(r.processed_at), oi.book_id
) t
GROUP BY t.stat_date, t.book_id;