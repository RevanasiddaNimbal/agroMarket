CREATE TABLE inventory
(
    id                VARCHAR(36)    NOT NULL,
    product_id        VARCHAR(36)    NOT NULL,
    reserved_quantity NUMERIC(19, 3) NOT NULL DEFAULT 0,
    version           BIGINT         NOT NULL DEFAULT 0,
    created_at        TIMESTAMP      NOT NULL,
    updated_at        TIMESTAMP      NOT NULL,

    CONSTRAINT pk_inventory
        PRIMARY KEY (id),

    CONSTRAINT uk_inventory_product
        UNIQUE (product_id),

    CONSTRAINT fk_inventory_product
        FOREIGN KEY (product_id)
            REFERENCES products (id)
            ON DELETE CASCADE,

    CONSTRAINT chk_inventory_reserved_quantity
        CHECK (reserved_quantity >= 0)
);

CREATE INDEX idx_inventory_product_id
    ON inventory (product_id);
