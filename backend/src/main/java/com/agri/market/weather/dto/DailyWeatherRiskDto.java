package com.agri.market.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Daily weather forecast and farming risk assessment for a specific date")
public class DailyWeatherRiskDto {

    @Schema(
            description = "Forecast date",
            example = "2026-09-01"
    )
    private String date;

    @Schema(
            description = "Weather forecast information for the selected date"
    )
    private DailyWeatherDto weather;

    @Schema(
            description = "Complete farming risk assessment for the selected date"
    )
    private FarmingRiskDto farmingRisk;
}