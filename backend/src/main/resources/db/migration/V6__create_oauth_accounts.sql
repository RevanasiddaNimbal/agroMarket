CREATE TABLE oauth_accounts
(
    id               VARCHAR(255) NOT NULL,
    provider         VARCHAR(50)  NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    user_id          VARCHAR(255) NOT NULL,
    created_at       TIMESTAMP    NOT NULL,
    updated_at       TIMESTAMP    NOT NULL,

    CONSTRAINT pk_oauth_accounts
        PRIMARY KEY (id),

    CONSTRAINT uk_oauth_provider_user
        UNIQUE (provider, provider_user_id),

    CONSTRAINT fk_oauth_account_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
);