CREATE TABLE deliveries
(
    id             VARCHAR(36) NOT NULL,
    order_id       VARCHAR(36) NOT NULL,
    otp            VARCHAR(6),
    otp_expires_at TIMESTAMP,
    otp_verified   BOOLEAN     NOT NULL DEFAULT FALSE,
    delivered_at   TIMESTAMP,
    failure_reason VARCHAR(500),
    created_at     TIMESTAMP   NOT NULL,
    updated_at     TIMESTAMP   NOT NULL,

    CONSTRAINT pk_deliveries
        PRIMARY KEY (id),

    CONSTRAINT uk_delivery_order
        UNIQUE (order_id),

    CONSTRAINT fk_delivery_order
        FOREIGN KEY (order_id)
            REFERENCES orders (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_deliveries_order_id
    ON deliveries (order_id);

CREATE INDEX idx_deliveries_otp_expires_at
    ON deliveries (otp_expires_at);