CREATE TABLE market_prices
(
    id            VARCHAR(36) PRIMARY KEY,

    commodity     VARCHAR(150) NOT NULL,
    variety       VARCHAR(150),
    grade         VARCHAR(100),

    state         VARCHAR(100) NOT NULL,
    district      VARCHAR(100) NOT NULL,
    market        VARCHAR(150) NOT NULL,

    minimum_price NUMERIC(12, 2),
    maximum_price NUMERIC(12, 2),
    modal_price   NUMERIC(12, 2),

    unit          VARCHAR(50)  NOT NULL,
    currency      VARCHAR(10)  NOT NULL,

    arrival_date  DATE         NOT NULL,

    source        VARCHAR(100) NOT NULL,

    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP    NOT NULL
);

CREATE INDEX idx_market_price_commodity
    ON market_prices (commodity);

CREATE INDEX idx_market_price_state
    ON market_prices (state);

CREATE INDEX idx_market_price_district
    ON market_prices (district);

CREATE INDEX idx_market_price_market
    ON market_prices (market);

CREATE INDEX idx_market_price_arrival_date
    ON market_prices (arrival_date);

