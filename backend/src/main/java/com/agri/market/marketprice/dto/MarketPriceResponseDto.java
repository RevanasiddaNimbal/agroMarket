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
@Schema(description = "Response containing agricultural market price information")
public class MarketPriceResponseDto {

    @JsonProperty("message")
    @Schema(
            description = "Market price operation result message",
            example = "Market prices retrieved successfully"
    )
    private String message;

    @JsonProperty("prices")
    @Schema(
            description = "List of agricultural market price records"
    )
    private List<MarketPriceDto> prices;
}