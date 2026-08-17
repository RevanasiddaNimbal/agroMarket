CREATE TABLE password_reset_tokens
(
    id                 VARCHAR(255) NOT NULL,
    created_date       TIMESTAMP(6) NOT NULL,
    last_modified_date TIMESTAMP(6) NOT NULL,
    created_by         VARCHAR(255),
    last_modified_by   VARCHAR(255),
    user_id            VARCHAR(255) NOT NULL,
    token_hash         VARCHAR(64)  NOT NULL,
    expires_at         TIMESTAMP(6) NOT NULL,
    used               BOOLEAN      NOT NULL,
    used_at            TIMESTAMP(6),
    CONSTRAINT pk_password_reset_tokens PRIMARY KEY (id),
    CONSTRAINT uk_password_reset_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_password_reset_token_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
);

CREATE UNIQUE INDEX idx_password_reset_token_hash
    ON password_reset_tokens (token_hash);

CREATE INDEX idx_password_reset_user_id
    ON password_reset_tokens (user_id);