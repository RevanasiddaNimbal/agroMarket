CREATE TABLE refresh_token_sessions
(
    id                 VARCHAR(255) NOT NULL,
    created_date       TIMESTAMP(6) NOT NULL,
    last_modified_date TIMESTAMP(6) NOT NULL,
    created_by         VARCHAR(255),
    last_modified_by   VARCHAR(255),
    user_id            VARCHAR(255) NOT NULL,
    token_hash         VARCHAR(64)  NOT NULL,
    device_name        VARCHAR(255) NOT NULL,
    ip_address         VARCHAR(45),
    expires_at         TIMESTAMP(6) NOT NULL,
    revoked            BOOLEAN      NOT NULL,
    revoked_at         TIMESTAMP(6),
    CONSTRAINT pk_refresh_token_sessions PRIMARY KEY (id),
    CONSTRAINT uk_refresh_token_sessions_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_token_sessions_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
);

CREATE INDEX idx_refresh_session_user_id
    ON refresh_token_sessions (user_id);

CREATE INDEX idx_refresh_session_token_hash
    ON refresh_token_sessions (token_hash);