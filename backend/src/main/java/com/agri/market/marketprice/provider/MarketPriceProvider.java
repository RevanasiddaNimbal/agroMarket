package com.agri.market.marketprice.provider;

import com.agri.market.marketprice.dto.MarketPriceDto;

import java.time.LocalDate;
import java.util.List;

public interface MarketPriceProvider {

    List<MarketPriceDto> getMarketPrices(
            String commodity,
            String state,
            String district,
            String market,
            LocalDate date
    );
}