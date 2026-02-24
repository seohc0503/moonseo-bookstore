-- Flyway V3: 데모 데이터 (로컬/개발 편의용 샘플 데이터)
-- 실제 운영 배포 시에는 비워두거나 조건부로 사용

-- 유저 1명
INSERT INTO users (email, password_hash, name, phone, status_id)
VALUES (
  'demo@moonseo.com',
  '$2a$10$demo-hash-placeholder',   -- bcrypt 예시
  '데모유저',
  '010-0000-0000',
  (SELECT id FROM user_status WHERE code='ACTIVE')
);

-- 카테고리 1개
INSERT INTO categories (name, display_order) VALUES ('개발/프로그래밍', 1);

-- 도서 1권
INSERT INTO books (
  category_id, status_id, isbn13, title, author, publisher, published_at,
  price, sale_price, currency, stock_qty, thumbnail_url, ext_source
) VALUES (
  (SELECT id FROM categories WHERE name='개발/프로그래밍'),
  (SELECT id FROM book_status WHERE code='ON_SALE'),
  '9781234567897',
  '스프링 입문',
  '홍길동',
  '문서출판',
  '2025-01-10',
  30000,
  27000,
  'KRW',
  20,
  NULL,
  'NAVER_BOOKS'
);

-- 장바구니 1개
INSERT INTO carts (user_id)
VALUES (
  (SELECT id FROM users WHERE email='demo@moonseo.com')
);

-- 장바구니 아이템 1개
INSERT INTO cart_items (cart_id, book_id, qty, price_snapshot)
VALUES (
  (SELECT id FROM carts
    WHERE user_id=(SELECT id FROM users WHERE email='demo@moonseo.com')),
  (SELECT id FROM books
    WHERE isbn13='9781234567897'),
  1,
  27000
);

-- 공지/FAQ 예시
INSERT INTO notices (title, content, pinned)
VALUES ('오픈 공지', '문서 쇼핑몰이 오픈했습니다.', TRUE);

INSERT INTO faqs (question, answer, display_order)
VALUES ('주문은 어떻게 하나요?', '장바구니에 담고 결제하세요.', 1);