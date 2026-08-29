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
@Schema(description = "Complete farming weather risk assessment for a specific day")
public class FarmingRiskDto {

    @Schema(
            description = "Overall farming risk score ranging from 0 to 100",
            example = "72"
    )
    private int overallRiskScore;

    @Schema(
            description = "Overall farming risk level for the assessed day",
            example = "HIGH"
    )
    private FarmingRiskLevel overallRiskLevel;

    @Schema(
            description = "Rainfall-related farming risk"
    )
    private RiskDetailDto rainfallRisk;

    @Schema(
            description = "Wind-related farming risk"
    )
    private RiskDetailDto windRisk;

    @Schema(
            description = "Heat-related farming risk"
    )
    private RiskDetailDto heatRisk;

    @Schema(
            description = "Ultraviolet radiation-related farming risk"
    )
    private RiskDetailDto uvRisk;

    @Schema(
            description = "Risk associated with pesticide and fertilizer spraying activities"
    )
    private RiskDetailDto sprayingRisk;

    @Schema(
            description = "Risk indicating whether irrigation requires attention based on forecast weather conditions"
    )
    private RiskDetailDto irrigationRisk;

    @Schema(
            description = "Overall explanation of the weather conditions affecting farming activities",
            example = "Significant rainfall and strong winds are expected, which may affect outdoor field activities."
    )
    private String summary;

    @Schema(
            description = "Formal recommendations for farming activities based on the assessed weather risks"
    )
    private List<String> recommendations;
}