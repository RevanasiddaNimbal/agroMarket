CREATE TABLE order_items
(
    id                 VARCHAR(255)   NOT NULL,

    order_id           VARCHAR(255)   NOT NULL,
    product_id         VARCHAR(255)   NOT NULL,

    quantity           NUMERIC(19, 3) NOT NULL,
    unit_price         NUMERIC(19, 2) NOT NULL,
    subtotal           NUMERIC(19, 2) NOT NULL,
    
    created_date       TIMESTAMP      NOT NULL,
    last_modified_date TIMESTAMP      NOT NULL,
    created_by         VARCHAR(255),
    last_modified_by   VARCHAR(255),

    CONSTRAINT pk_order_items
        PRIMARY KEY (id),

    CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id)
            REFERENCES orders (id),

    CONSTRAINT fk_order_item_product
        FOREIGN KEY (product_id)
            REFERENCES products (id)
);

CREATE INDEX idx_order_items_order_id
    ON order_items (order_id);

CREATE INDEX idx_order_items_product_id
    ON order_items (product_id);