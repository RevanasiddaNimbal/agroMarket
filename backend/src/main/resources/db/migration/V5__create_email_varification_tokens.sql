CREATE TABLE email_verification_tokens
(
    id                 VARCHAR(255) NOT NULL,
    created_date       TIMESTAMP(6) NOT NULL,
    last_modified_date TIMESTAMP(6) NOT NULL,
    created_by         VARCHAR(255),
    last_modified_by   VARCHAR(255),
    user_id            VARCHAR(255) NOT NULL,
    token_hash         VARCHAR(255) NOT NULL,
    expires_at         TIMESTAMP(6) NOT NULL,
    used               BOOLEAN      NOT NULL,
    CONSTRAINT pk_email_verification_tokens PRIMARY KEY (id),
    CONSTRAINT uk_email_verification_tokens_user_id UNIQUE (user_id),
    CONSTRAINT uk_email_verification_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_email_verification_tokens_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
);