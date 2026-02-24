-- Flyway V4: 업무 조회 최적화 인덱스

-- Users
CREATE INDEX idx_users_phone             ON users(phone);

-- Orders
CREATE INDEX idx_orders_user_created     ON orders(user_id, created_at DESC);
CREATE INDEX idx_orders_status           ON orders(status_id, created_at DESC);

-- OrderItems
CREATE INDEX idx_order_items_book_id     ON order_items(book_id);

-- Books
CREATE INDEX idx_books_category          ON books(category_id);
CREATE INDEX idx_books_status            ON books(status_id);
CREATE INDEX idx_books_created_at        ON books(created_at);

-- Review
CREATE INDEX idx_reviews_book_id         ON reviews(book_id);

-- Shipments
CREATE INDEX idx_shipments_carrier_track ON shipments(carrier, tracking_no);
CREATE INDEX idx_shipments_status        ON shipments(status_id);

-- Inventory ledger
CREATE INDEX idx_inventory_book_time     ON inventory_ledger(book_id, occurred_at DESC);

-- Admin logs
CREATE INDEX idx_admin_logs_entity       ON admin_logs(entity_type, entity_id);
CREATE INDEX idx_admin_logs_created      ON admin_logs(created_at DESC);

-- Files
CREATE INDEX idx_files_owner             ON files(owner_type, owner_id);

-- Refresh tokens
CREATE INDEX idx_refresh_user            ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_expires         ON refresh_tokens(expires_at);

-- Payments
CREATE INDEX idx_payments_status_paid    ON payments(status, paid_at);

-- Refunds
CREATE INDEX idx_refunds_payment         ON refunds(payment_id);
CREATE INDEX idx_refunds_status_req      ON refunds(status, requested_at);
CREATE INDEX idx_refunds_status_proc     ON refunds(status, processed_at);
CREATE INDEX idx_refund_items_order_item_id ON refund_items(order_item_id);