CREATE TABLE payments
(
    id                  VARCHAR(36) PRIMARY KEY,
    order_id            VARCHAR(36)    NOT NULL,
    amount              NUMERIC(19, 2) NOT NULL,
    payment_method      VARCHAR(30)    NOT NULL,
    status              VARCHAR(30)    NOT NULL,
    provider            VARCHAR(50)    NOT NULL,
    provider_payment_id VARCHAR(100),
    paid_at             TIMESTAMP,
    refunded_at         TIMESTAMP,
    created_date        TIMESTAMP      NOT NULL,
    last_modified_date  TIMESTAMP      NOT NULL,
    created_by          VARCHAR(255),
    last_modified_by    VARCHAR(255),

    CONSTRAINT uk_payments_order_id
        UNIQUE (order_id),

    CONSTRAINT uk_payments_provider_payment_id
        UNIQUE (provider_payment_id),

    CONSTRAINT fk_payment_order
        FOREIGN KEY (order_id)
            REFERENCES orders (id)
);

CREATE INDEX idx_payments_order_id
    ON payments (order_id);

CREATE INDEX idx_payments_status
    ON payments (status);

CREATE INDEX idx_payments_provider_payment_id
    ON payments (provider_payment_id);