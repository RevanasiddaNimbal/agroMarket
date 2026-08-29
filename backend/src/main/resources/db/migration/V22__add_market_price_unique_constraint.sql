ALTER TABLE market_prices
    ADD CONSTRAINT uk_market_price_unique
        UNIQUE (
                commodity,
                state,
                district,
                market,
                arrival_date
            );