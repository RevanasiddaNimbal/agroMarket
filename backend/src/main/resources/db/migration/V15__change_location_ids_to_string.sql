ALTER TABLE districts
DROP CONSTRAINT fk_districts_state;

ALTER TABLE taluks
DROP CONSTRAINT fk_taluks_district;

ALTER TABLE states
ALTER COLUMN id TYPE VARCHAR(36)
USING id::text;

ALTER TABLE districts
ALTER COLUMN id TYPE VARCHAR(36)
USING id::text;

ALTER TABLE districts
ALTER COLUMN state_id TYPE VARCHAR(36)
USING state_id::text;

ALTER TABLE taluks
ALTER COLUMN id TYPE VARCHAR(36)
USING id::text;

ALTER TABLE taluks
ALTER COLUMN district_id TYPE VARCHAR(36)
USING district_id::text;

ALTER TABLE districts
    ADD CONSTRAINT fk_districts_state
        FOREIGN KEY (state_id)
            REFERENCES states(id);

ALTER TABLE taluks
    ADD CONSTRAINT fk_taluks_district
        FOREIGN KEY (district_id)
            REFERENCES districts(id);