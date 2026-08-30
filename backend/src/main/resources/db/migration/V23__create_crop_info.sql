CREATE TABLE crop_info
(
    id                       VARCHAR(36) PRIMARY KEY,
    crop_name                VARCHAR(100) NOT NULL UNIQUE,
    scientific_name          VARCHAR(150),
    description              TEXT,
    image_url                TEXT,
    life_cycle               VARCHAR(50),
    growth_stages            TEXT,
    sowing_info              TEXT,
    growing_duration         VARCHAR(100),
    harvesting_info          TEXT,
    soil_requirements        TEXT,
    water_requirements       TEXT,
    sunlight_requirements    TEXT,
    temperature_requirements TEXT,
    common_pests             TEXT,
    common_diseases          TEXT,
    uses                     TEXT,
    created_at               TIMESTAMP    NOT NULL,
    updated_at               TIMESTAMP    NOT NULL
);

CREATE INDEX idx_crop_info_name
    ON crop_info (crop_name);

CREATE INDEX idx_crop_info_scientific_name
    ON crop_info (scientific_name);