CREATE TABLE orders
(
    id                 VARCHAR(255)   NOT NULL,

    user_id            VARCHAR(255)   NOT NULL,
    address_id         VARCHAR(255)   NOT NULL,

    status             VARCHAR(30)    NOT NULL DEFAULT 'CONFIRMED',

    total_amount       NUMERIC(19, 2) NOT NULL,
    
    created_date       TIMESTAMP      NOT NULL,
    last_modified_date TIMESTAMP      NOT NULL,
    created_by         VARCHAR(255),
    last_modified_by   VARCHAR(255),

    CONSTRAINT pk_orders
        PRIMARY KEY (id),

    CONSTRAINT fk_order_user
        FOREIGN KEY (user_id)
            REFERENCES users (id),

    CONSTRAINT fk_order_address
        FOREIGN KEY (address_id)
            REFERENCES addresses (id),

    CONSTRAINT chk_orders_status
        CHECK (
            status IN (
                       'CONFIRMED',
                       'PROCESSING',
                       'SHIPPED',
                       'OUT_FOR_DELIVERY',
                       'DELIVERED',
                       'CANCELLED'
                )
            )
);

CREATE INDEX idx_orders_user_id
    ON orders (user_id);

CREATE INDEX idx_orders_status
    ON orders (status);

CREATE INDEX idx_orders_created_date
    ON orders (created_date);