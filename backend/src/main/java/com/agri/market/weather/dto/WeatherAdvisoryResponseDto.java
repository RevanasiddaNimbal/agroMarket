package com.agri.market.weather.dto;

import com.agri.market.weather.risk.FarmingRiskLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Weather-based farming advisory for the farmer")
public class WeatherAdvisoryResponseDto {

    @Schema(
            description = "Date for which the weather advisory is generated",
            example = "2026-09-01"
    )
    private String date;

    @Schema(
            description = "Overall farming risk score for the selected date",
            example = "76"
    )
    private int riskScore;

    @Schema(
            description = "Overall farming risk level for the selected date",
            example = "VERY_HIGH"
    )
    private FarmingRiskLevel riskLevel;

    @Schema(
            description = "Summary of the weather conditions affecting farming activities",
            example = "Heavy rainfall and strong winds are expected, creating unfavorable conditions for several outdoor farming activities."
    )
    private String summary;

    @Schema(
            description = "Formal weather-based recommendations for farming activities"
    )
    private List<String> recommendations;
}