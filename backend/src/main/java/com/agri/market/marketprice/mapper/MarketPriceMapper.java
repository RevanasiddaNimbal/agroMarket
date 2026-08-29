package com.agri.market.marketprice.mapper;

import com.agri.market.marketprice.dto.HistoricalMarketPriceDto;
import com.agri.market.marketprice.dto.MarketPriceDto;
import com.agri.market.marketprice.entity.MarketPrice;
import org.springframework.stereotype.Component;

@Component
public class MarketPriceMapper {

    public MarketPriceDto toDto(
            final MarketPrice price
    ) {

        if (price == null) {
            return null;
        }

        return MarketPriceDto.builder()
                .id(price.getId())
                .commodity(price.getCommodity())
                .variety(price.getVariety())
                .grade(price.getGrade())
                .state(price.getState())
                .district(price.getDistrict())
                .market(price.getMarket())
                .minimumPrice(price.getMinimumPrice())
                .maximumPrice(price.getMaximumPrice())
                .modalPrice(price.getModalPrice())
                .unit(price.getUnit())
                .currency(price.getCurrency())
                .arrivalDate(price.getArrivalDate())
                .source(price.getSource())
                .build();
    }

    public MarketPrice toEntity(
            final MarketPriceDto dto
    ) {

        if (dto == null) {
            return null;
        }

        return MarketPrice.builder()
                .commodity(dto.getCommodity())
                .variety(dto.getVariety())
                .grade(dto.getGrade())
                .state(dto.getState())
                .district(dto.getDistrict())
                .market(dto.getMarket())
                .minimumPrice(dto.getMinimumPrice())
                .maximumPrice(dto.getMaximumPrice())
                .modalPrice(dto.getModalPrice())
                .unit(dto.getUnit())
                .currency(dto.getCurrency())
                .arrivalDate(dto.getArrivalDate())
                .source(dto.getSource())
                .build();
    }

    public HistoricalMarketPriceDto toHistoricalDto(
            final MarketPrice price
    ) {

        if (price == null) {
            return null;
        }

        return HistoricalMarketPriceDto.builder()
                .date(price.getArrivalDate())
                .commodity(price.getCommodity())
                .state(price.getState())
                .district(price.getDistrict())
                .market(price.getMarket())
                .modalPrice(price.getModalPrice())
                .unit(price.getUnit())
                .currency(price.getCurrency())
                .build();
    }

    public HistoricalMarketPriceDto toHistoricalDto(
            final MarketPriceDto price
    ) {

        if (price == null) {
            return null;
        }

        return HistoricalMarketPriceDto.builder()
                .date(price.getArrivalDate())
                .commodity(price.getCommodity())
                .state(price.getState())
                .district(price.getDistrict())
                .market(price.getMarket())
                .modalPrice(price.getModalPrice())
                .unit(price.getUnit())
                .currency(price.getCurrency())
                .build();
    }
}