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
@Schema(description = "Complete seven-day weather forecast and farming risk assessment")
public class WeeklyWeatherRiskResponseDto {

    @Schema(
            description = "Overall farming risk score for the seven-day forecast period",
            example = "58"
    )
    private int overallRiskScore;

    @Schema(
            description = "Overall farming risk level for the seven-day forecast period",
            example = "HIGH"
    )
    private FarmingRiskLevel overallRiskLevel;

    @Schema(
            description = "Risk breakdown across major farming-related weather conditions for the seven-day period"
    )
    private WeeklyRiskBreakdownDto riskBreakdown;

    @Schema(
            description = "Daily weather forecasts with individual farming risk assessments for each day"
    )
    private List<DailyWeatherRiskDto> dailyForecast;

    @Schema(
            description = "Date with the highest farming risk during the seven-day forecast period",
            example = "2026-09-01"
    )
    private String highestRiskDate;

    @Schema(
            description = "Highest daily farming risk score during the seven-day forecast period",
            example = "84"
    )
    private int highestRiskScore;

    @Schema(
            description = "Date with the lowest farming risk during the seven-day forecast period",
            example = "2026-09-04"
    )
    private String lowestRiskDate;

    @Schema(
            description = "Lowest daily farming risk score during the seven-day forecast period",
            example = "22"
    )
    private int lowestRiskScore;

    @Schema(
            description = "Formal farming insights generated from the seven-day weather forecast"
    )
    private List<String> weeklyInsights;
}