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
@Schema(description = "Agricultural market price information")
public class MarketPriceDto {

    @JsonProperty("id")
    @Schema(
            description = "Unique market price record identifier",
            example = "8f7c2e6a-4d31-4c5a-9f21-7b8e2d4a1c90"
    )
    private String id;

    @JsonProperty("commodity")
    @Schema(
            description = "Name of the agricultural commodity",
            example = "Tomato",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String commodity;

    @JsonProperty("variety")
    @Schema(
            description = "Commodity variety",
            example = "Hybrid"
    )
    private String variety;

    @JsonProperty("grade")
    @Schema(
            description = "Commodity grade",
            example = "FAQ"
    )
    private String grade;

    @JsonProperty("state")
    @Schema(
            description = "State where the market is located",
            example = "Karnataka",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String state;

    @JsonProperty("district")
    @Schema(
            description = "District where the market is located",
            example = "Kolar",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String district;

    @JsonProperty("market")
    @Schema(
            description = "Name of the agricultural market or mandi",
            example = "Kolar",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String market;

    @JsonProperty("minimum_price")
    @Schema(
            description = "Minimum recorded market price",
            example = "1800.00"
    )
    private BigDecimal minimumPrice;

    @JsonProperty("maximum_price")
    @Schema(
            description = "Maximum recorded market price",
            example = "2400.00"
    )
    private BigDecimal maximumPrice;

    @JsonProperty("modal_price")
    @Schema(
            description = "Modal or most commonly observed market price",
            example = "2100.00"
    )
    private BigDecimal modalPrice;

    @JsonProperty("unit")
    @Schema(
            description = "Unit in which the commodity price is reported",
            example = "Quintal",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String unit;

    @JsonProperty("currency")
    @Schema(
            description = "Currency of the market price",
            example = "INR",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String currency;

    @JsonProperty("arrival_date")
    @Schema(
            description = "Date on which the market price was recorded",
            example = "2026-08-29",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDate arrivalDate;

    @JsonProperty("source")
    @Schema(
            description = "External source that provided the market price",
            example = "Farmer.in"
    )
    private String source;
}