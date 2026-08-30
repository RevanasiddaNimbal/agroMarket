ALTER TABLE orders
    ALTER COLUMN status SET DEFAULT 'PENDING_PAYMENT';

ALTER TABLE orders
DROP CONSTRAINT chk_orders_status;

ALTER TABLE orders
    ADD CONSTRAINT chk_orders_status
        CHECK (
            status IN (
                       'PENDING_PAYMENT',
                       'CONFIRMED',
                       'PROCESSING',
                       'SHIPPED',
                       'OUT_FOR_DELIVERY',
                       'DELIVERED',
                       'CANCELLED'
                )
            );