CREATE TABLE product_images
(
    id            VARCHAR(36)   NOT NULL,
    product_id    VARCHAR(36)   NOT NULL,
    image_url     VARCHAR(1000) NOT NULL,
    is_primary    BOOLEAN       NOT NULL DEFAULT FALSE,
    display_order INTEGER       NOT NULL DEFAULT 0,
    created_at    TIMESTAMP     NOT NULL,
    updated_at    TIMESTAMP     NOT NULL,

    CONSTRAINT pk_product_images
        PRIMARY KEY (id),

    CONSTRAINT fk_product_images_product
        FOREIGN KEY (product_id)
            REFERENCES products (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_product_images_product_id
    ON product_images (product_id);

CREATE INDEX idx_product_images_product_primary
    ON product_images (product_id, is_primary);

CREATE INDEX idx_product_images_product_order
    ON product_images (product_id, display_order);