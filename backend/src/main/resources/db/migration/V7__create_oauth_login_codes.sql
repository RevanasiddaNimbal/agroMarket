CREATE TABLE oauth_login_codes
(
    id         VARCHAR(255) NOT NULL,
    code       VARCHAR(128) NOT NULL,
    user_id    VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP    NOT NULL,
    used       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL,

    CONSTRAINT pk_oauth_login_codes
        PRIMARY KEY (id),

    CONSTRAINT uk_oauth_login_code
        UNIQUE (code),

    CONSTRAINT fk_oauth_login_code_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
);