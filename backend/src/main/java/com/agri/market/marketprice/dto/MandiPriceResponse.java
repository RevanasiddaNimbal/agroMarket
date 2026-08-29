package com.agri.market.marketprice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MandiPriceResponse {

    private boolean success;

    private List<Price> data;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Price {

        private String commodity;

        private String variety;

        private String grade;

        private String state;

        private String district;

        private String market;

        @JsonProperty("min_price")
        private BigDecimal minimumPrice;

        @JsonProperty("max_price")
        private BigDecimal maximumPrice;

        @JsonProperty("modal_price")
        private BigDecimal modalPrice;

        @JsonProperty("arrival_date")
        private LocalDate arrivalDate;
    }
}