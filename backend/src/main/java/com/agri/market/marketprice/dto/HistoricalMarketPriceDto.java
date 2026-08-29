package com.agri.market.marketprice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Historical agricultural market price information")
public class HistoricalMarketPriceDto {

    @JsonProperty("date")
    @Schema(
            description = "Date of the market price",
            example = "2026-08-29"
    )
    private LocalDate date;

    @JsonProperty("commodity")
    @Schema(
            description = "Agricultural commodity",
            example = "Tomato"
    )
    private String commodity;

    @JsonProperty("state")
    @Schema(
            description = "State of the market",
            example = "Karnataka"
    )
    private String state;

    @JsonProperty("district")
    @Schema(
            description = "District of the market",
            example = "Kolar"
    )
    private String district;

    @JsonProperty("market")
    @Schema(
            description = "Agricultural market or mandi",
            example = "Kolar"
    )
    private String market;

    @JsonProperty("modal_price")
    @Schema(
            description = "Modal market price for the date",
            example = "2100.00"
    )
    private BigDecimal modalPrice;

    @JsonProperty("unit")
    @Schema(
            description = "Unit used for the market price",
            example = "Quintal"
    )
    private String unit;

    @JsonProperty("currency")
    @Schema(
            description = "Currency of the market price",
            example = "INR"
    )
    private String currency;
}