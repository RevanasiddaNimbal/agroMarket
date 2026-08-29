package com.agri.market.marketprice.provider;

import com.agri.market.marketprice.config.MarketPriceProperties;
import com.agri.market.marketprice.dto.MandiPriceResponse;
import com.agri.market.marketprice.dto.MarketPriceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MandiPriceMarketPriceProvider
        implements MarketPriceProvider {

    private final MarketPriceProperties marketPriceProperties;

    private final RestClient.Builder restClientBuilder;

    @Override
    public List<MarketPriceDto> getMarketPrices(
            final String commodity,
            final String state,
            final String district,
            final String market,
            final LocalDate date
    ) {

        log.info(
                "Fetching market prices from Mandi Price API: " +
                        "commodity={}, state={}, district={}, market={}, date={}",
                commodity,
                state,
                district,
                market,
                date
        );

        try {

            final RestClient restClient =
                    restClientBuilder
                            .baseUrl(
                                    marketPriceProperties
                                            .getMandiPrice()
                                            .getBaseUrl()
                            )
                            .build();

            final MandiPriceResponse response =
                    restClient.get()
                            .uri(uriBuilder -> {

                                uriBuilder.path("/v1/prices");

                                if (hasText(commodity)) {
                                    uriBuilder.queryParam(
                                            "commodity",
                                            commodity
                                    );
                                }

                                if (hasText(state)) {
                                    uriBuilder.queryParam(
                                            "state",
                                            state
                                    );
                                }

                                if (hasText(district)) {
                                    uriBuilder.queryParam(
                                            "district",
                                            district
                                    );
                                }

                                if (hasText(market)) {
                                    uriBuilder.queryParam(
                                            "market",
                                            market
                                    );
                                }

                                if (date != null) {
                                    uriBuilder.queryParam(
                                            "date",
                                            date
                                    );
                                }

                                return uriBuilder.build();
                            })
                            .retrieve()
                            .body(MandiPriceResponse.class);

            if (response == null) {

                log.warn(
                        "Mandi Price API returned null response"
                );

                return Collections.emptyList();
            }

            if (!response.isSuccess()) {

                log.warn(
                        "Mandi Price API returned success=false"
                );

                return Collections.emptyList();
            }

            if (response.getData() == null
                    || response.getData().isEmpty()) {

                log.warn(
                        "Mandi Price API returned no market price records"
                );

                return Collections.emptyList();
            }
            

            final List<MarketPriceDto> prices =
                    response.getData()
                            .stream()
                            .filter(price ->
                                    matchesValue(
                                            price.getCommodity(),
                                            commodity
                                    )
                            )
                            .filter(price ->
                                    matchesValue(
                                            price.getState(),
                                            state
                                    )
                            )
                            .filter(price ->
                                    matchesValue(
                                            price.getDistrict(),
                                            district
                                    )
                            )
                            .filter(price ->
                                    matchesValue(
                                            price.getMarket(),
                                            market
                                    )
                            )
                            .filter(price ->
                                    matchesDate(
                                            price.getArrivalDate(),
                                            date
                                    )
                            )
                            .map(this::toDto)
                            .toList();

            log.info(
                    "Mandi Price API returned {} matching market price records",
                    prices.size()
            );

            return prices;

        } catch (Exception exception) {

            log.error(
                    "Failed to fetch market prices from Mandi Price API",
                    exception
            );

            return Collections.emptyList();
        }
    }

    private boolean matchesValue(
            final String actual,
            final String expected
    ) {

        if (!hasText(expected)) {
            return true;
        }

        return actual != null
                && actual.trim()
                .equalsIgnoreCase(expected.trim());
    }

    private boolean matchesDate(
            final LocalDate actual,
            final LocalDate expected
    ) {

        if (expected == null) {
            return true;
        }

        return expected.equals(actual);
    }

    private boolean hasText(
            final String value
    ) {

        return value != null
                && !value.isBlank();
    }

    private MarketPriceDto toDto(
            final MandiPriceResponse.Price price
    ) {

        return MarketPriceDto.builder()
                .commodity(price.getCommodity())
                .variety(price.getVariety())
                .grade(price.getGrade())
                .state(price.getState())
                .district(price.getDistrict())
                .market(price.getMarket())
                .minimumPrice(price.getMinimumPrice())
                .maximumPrice(price.getMaximumPrice())
                .modalPrice(price.getModalPrice())
                .unit("quintal")
                .currency("INR")
                .arrivalDate(price.getArrivalDate())
                .source("Mandi Price API")
                .build();
    }
}