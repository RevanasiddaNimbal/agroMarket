package com.agri.market.marketprice.service;

import com.agri.market.marketprice.dto.MarketPriceResponseDto;
import com.agri.market.marketprice.dto.MarketPriceTrendDto;

import java.time.LocalDate;

public interface MarketPriceService {

    MarketPriceResponseDto getMarketPrices(
            String commodity,
            String state,
            String district,
            String market,
            LocalDate date
    );

    MarketPriceTrendDto getHistoricalPrices(
            String commodity,
            String state,
            String district,
            String market,
            LocalDate fromDate,
            LocalDate toDate
    );
}