CREATE TABLE payment_transactions
(
    id                      VARCHAR(36) PRIMARY KEY,
    payment_id              VARCHAR(36)    NOT NULL,
    order_id                VARCHAR(36)    NOT NULL,
    transaction_type        VARCHAR(30)    NOT NULL,
    amount                  NUMERIC(19, 2) NOT NULL,
    status                  VARCHAR(30)    NOT NULL,
    provider                VARCHAR(50)    NOT NULL,
    provider_transaction_id VARCHAR(100),
    created_date            TIMESTAMP      NOT NULL,
    last_modified_date      TIMESTAMP      NOT NULL,
    created_by              VARCHAR(255),
    last_modified_by        VARCHAR(255),

    CONSTRAINT uk_payment_transactions_provider_transaction_id
        UNIQUE (provider_transaction_id),

    CONSTRAINT fk_payment_transaction_payment
        FOREIGN KEY (payment_id)
            REFERENCES payments (id),

    CONSTRAINT fk_payment_transaction_order
        FOREIGN KEY (order_id)
            REFERENCES orders (id)
);

CREATE INDEX idx_payment_transactions_payment_id
    ON payment_transactions (payment_id);

CREATE INDEX idx_payment_transactions_order_id
    ON payment_transactions (order_id);

CREATE INDEX idx_payment_transactions_type
    ON payment_transactions (transaction_type);

CREATE INDEX idx_payment_transactions_status
    ON payment_transactions (status);

CREATE INDEX idx_payment_transactions_provider_transaction_id
    ON payment_transactions (provider_transaction_id);