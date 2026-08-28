CREATE TABLE products
(
    id          VARCHAR(36)    NOT NULL,
    farmer_id   VARCHAR(36)    NOT NULL,
    category_id VARCHAR(36)    NOT NULL,
    name        VARCHAR(150)   NOT NULL,
    description VARCHAR(2000)  NOT NULL,
    price       NUMERIC(12, 2) NOT NULL,
    unit        VARCHAR(50)    NOT NULL,
    quantity    NUMERIC(12, 2) NOT NULL,
    location    VARCHAR(100)   NOT NULL,
    status      VARCHAR(20)    NOT NULL,
    created_at  TIMESTAMP      NOT NULL,
    updated_at  TIMESTAMP      NOT NULL,

    CONSTRAINT pk_products
        PRIMARY KEY (id),

    CONSTRAINT fk_products_farmer
        FOREIGN KEY (farmer_id)
            REFERENCES users (id),

    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id)
            REFERENCES categories (id)
);

CREATE INDEX idx_products_farmer_id
    ON products (farmer_id);

CREATE INDEX idx_products_category_id
    ON products (category_id);

CREATE INDEX idx_products_status
    ON products (status);