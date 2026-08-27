CREATE TABLE states
(
    id           UUID PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    code         VARCHAR(20)  NOT NULL,
    country_code VARCHAR(2)   NOT NULL DEFAULT 'IN',
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL,
    CONSTRAINT uk_states_country_name UNIQUE (country_code, name),
    CONSTRAINT uk_states_country_code UNIQUE (country_code, code)
);

CREATE INDEX idx_states_name ON states (name);
CREATE INDEX idx_states_active ON states (is_active);

CREATE TABLE districts
(
    id         UUID PRIMARY KEY,
    state_id   UUID         NOT NULL,
    name       VARCHAR(100) NOT NULL,
    code       VARCHAR(20)  NOT NULL,
    is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    CONSTRAINT fk_districts_state
        FOREIGN KEY (state_id)
            REFERENCES states (id),
    CONSTRAINT uk_districts_state_name UNIQUE (state_id, name),
    CONSTRAINT uk_districts_state_code UNIQUE (state_id, code)
);

CREATE INDEX idx_districts_state_id ON districts (state_id);
CREATE INDEX idx_districts_name ON districts (name);
CREATE INDEX idx_districts_active ON districts (is_active);

CREATE TABLE taluks
(
    id          UUID PRIMARY KEY,
    district_id UUID         NOT NULL,
    name        VARCHAR(100) NOT NULL,
    code        VARCHAR(20)  NOT NULL,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    CONSTRAINT fk_taluks_district
        FOREIGN KEY (district_id)
            REFERENCES districts (id),
    CONSTRAINT uk_taluks_district_name UNIQUE (district_id, name),
    CONSTRAINT uk_taluks_district_code UNIQUE (district_id, code)
);

CREATE INDEX idx_taluks_district_id ON taluks (district_id);
CREATE INDEX idx_taluks_name ON taluks (name);
CREATE INDEX idx_taluks_active ON taluks (is_active);