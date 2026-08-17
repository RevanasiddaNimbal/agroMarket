CREATE TABLE roles
(
    id                 VARCHAR(255) NOT NULL,
    name               VARCHAR(50)  NOT NULL,
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP    NOT NULL,
    created_by         VARCHAR(255),
    last_modified_by   VARCHAR(255),
    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uk_roles_name UNIQUE (name)
);