CREATE TABLE addresses (
                           id VARCHAR(36) NOT NULL,

                           user_id VARCHAR(36) NOT NULL,

                           address_line1 VARCHAR(200) NOT NULL,
                           address_line2 VARCHAR(200),

                           village VARCHAR(100),

                           city VARCHAR(100) NOT NULL,
                           district VARCHAR(100) NOT NULL,
                           state VARCHAR(100) NOT NULL,
                           pincode VARCHAR(10) NOT NULL,
                           country VARCHAR(100) NOT NULL DEFAULT 'India',

                           latitude DECIMAL(10, 7),
                           longitude DECIMAL(10, 7),

                           location_type VARCHAR(20) NOT NULL DEFAULT 'MANUAL',

                           address_type VARCHAR(20) NOT NULL DEFAULT 'HOME',
                           is_default BOOLEAN NOT NULL DEFAULT FALSE,

                           created_date TIMESTAMP NOT NULL,
                           last_modified_date TIMESTAMP NOT NULL,

                           created_by VARCHAR(255),
                           last_modified_by VARCHAR(255),

                           CONSTRAINT pk_addresses
                               PRIMARY KEY (id),

                           CONSTRAINT fk_address_user
                               FOREIGN KEY (user_id)
                                   REFERENCES users(id)
                                   ON DELETE CASCADE,

                           CONSTRAINT chk_location_type
                               CHECK (
                                   location_type IN ('MANUAL', 'MAP')
                                   ),

                           CONSTRAINT chk_address_type
                               CHECK (
                                   address_type IN ('HOME', 'FARM', 'OTHER')
                                   ),

                           CONSTRAINT chk_address_latitude
                               CHECK (
                                   latitude IS NULL
                                       OR latitude BETWEEN -90.0000000 AND 90.0000000
                                   ),

                           CONSTRAINT chk_address_longitude
                               CHECK (
                                   longitude IS NULL
                                       OR longitude BETWEEN -180.0000000 AND 180.0000000
                                   ),

                           CONSTRAINT chk_location_coordinates
                               CHECK (
                                   (
                                       location_type = 'MANUAL'
                                           AND latitude IS NULL
                                           AND longitude IS NULL
                                       )
                                       OR
                                   (
                                       location_type = 'MAP'
                                           AND latitude IS NOT NULL
                                           AND longitude IS NOT NULL
                                       )
                                   )
);

CREATE INDEX idx_addresses_user_id
    ON addresses(user_id);

CREATE INDEX idx_addresses_user_default
    ON addresses(user_id, is_default);

CREATE UNIQUE INDEX ux_addresses_one_default_per_user
    ON addresses(user_id)
    WHERE is_default = TRUE;