package com.agri.market.marketprice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Historical market price trend for a commodity")
public class MarketPriceTrendDto {

    @JsonProperty("commodity")
    @Schema(
            description = "Agricultural commodity",
            example = "Tomato"
    )
    private String commodity;

    @JsonProperty("state")
    @Schema(
            description = "State used for the trend",
            example = "Karnataka"
    )
    private String state;

    @JsonProperty("district")
    @Schema(
            description = "District used for the trend",
            example = "Kolar"
    )
    private String district;

    @JsonProperty("market")
    @Schema(
            description = "Market used for the trend",
            example = "Kolar"
    )
    private String market;

    @JsonProperty("prices")
    @Schema(description = "Historical price records used to build the trend")
    private List<HistoricalMarketPriceDto> prices;
}