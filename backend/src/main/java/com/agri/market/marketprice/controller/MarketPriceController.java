package com.agri.market.marketprice.controller;

import com.agri.market.marketprice.dto.MarketPriceResponseDto;
import com.agri.market.marketprice.dto.MarketPriceTrendDto;
import com.agri.market.marketprice.service.MarketPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/market-prices")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Market Prices",
        description = "Agricultural market price APIs"
)
public class MarketPriceController {

    private final MarketPriceService marketPriceService;

    @Operation(
            summary = "Get current market prices",
            description = "Returns current agricultural market prices with optional commodity, state, district, market and date filters."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Market prices retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @GetMapping
    public MarketPriceResponseDto getMarketPrices(
            @Parameter(
                    description = "Commodity name",
                    example = "Tomato"
            )
            @RequestParam(required = false) final String commodity,

            @Parameter(
                    description = "State name",
                    example = "Karnataka"
            )
            @RequestParam(required = false) final String state,

            @Parameter(
                    description = "District name",
                    example = "Bengaluru"
            )
            @RequestParam(required = false) final String district,

            @Parameter(
                    description = "Market or mandi name",
                    example = "Yeshwanthpur"
            )
            @RequestParam(required = false) final String market,

            @Parameter(
                    description = "Market price date",
                    example = "2026-08-29"
            )
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate date
    ) {
        return marketPriceService.getMarketPrices(
                commodity,
                state,
                district,
                market,
                date
        );
    }

    @Operation(
            summary = "Get historical market prices",
            description = "Returns historical market price data for a commodity within the specified date range."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Historical market prices retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid commodity or date range"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @GetMapping("/history")
    public MarketPriceTrendDto getHistoricalPrices(
            @Parameter(
                    description = "Commodity name",
                    example = "Tomato",
                    required = true
            )
            @RequestParam final String commodity,

            @Parameter(
                    description = "State name",
                    example = "Karnataka"
            )
            @RequestParam(required = false) final String state,

            @Parameter(
                    description = "District name",
                    example = "Bengaluru"
            )
            @RequestParam(required = false) final String district,

            @Parameter(
                    description = "Market or mandi name",
                    example = "Yeshwanthpur"
            )
            @RequestParam(required = false) final String market,

            @Parameter(
                    description = "Start date of historical period",
                    example = "2026-08-01",
                    required = true
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate fromDate,

            @Parameter(
                    description = "End date of historical period",
                    example = "2026-08-29",
                    required = true
            )
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate toDate
    ) {
        return marketPriceService.getHistoricalPrices(
                commodity,
                state,
                district,
                market,
                fromDate,
                toDate
        );
    }
}
