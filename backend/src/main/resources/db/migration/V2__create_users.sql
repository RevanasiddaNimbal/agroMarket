CREATE TABLE users
(
    id                  VARCHAR(255) NOT NULL,
    full_name           VARCHAR(100) NOT NULL,
    email               VARCHAR(150) NOT NULL,
    phone_number        VARCHAR(15)  NOT NULL,
    email_verified      BOOLEAN      NOT NULL,
    phone_verified      BOOLEAN      NOT NULL,
    password            VARCHAR(255) NOT NULL,
    credentials_expired BOOLEAN      NOT NULL,
    password_changed_at TIMESTAMP(6),
    enabled             BOOLEAN      NOT NULL,
    account_locked      BOOLEAN      NOT NULL,
    profile_picture_url VARCHAR(255),
    created_at          TIMESTAMP(6) NOT NULL,
    updated_at          TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_phone_number UNIQUE (phone_number)
);

CREATE TABLE user_roles
(
    user_id VARCHAR(255) NOT NULL,
    role_id VARCHAR(255) NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
            REFERENCES users (id),
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id)
            REFERENCES roles (id)
);