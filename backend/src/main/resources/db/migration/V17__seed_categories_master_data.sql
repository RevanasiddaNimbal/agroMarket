INSERT INTO categories (
    id,
    name,
    created_at,
    updated_at
)
VALUES
    (gen_random_uuid()::VARCHAR, 'Seeds', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid()::VARCHAR, 'Fertilizers', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid()::VARCHAR, 'Pesticides', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid()::VARCHAR, 'Harvested Products', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid()::VARCHAR, 'Pre-Harvested Products', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid()::VARCHAR, 'Farm Equipment', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);