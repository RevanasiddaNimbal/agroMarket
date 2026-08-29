package com.agri.market.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Weekly farming risk breakdown calculated from the seven-day weather forecast")
public class WeeklyRiskBreakdownDto {

    @Schema(
            description = "Weekly rainfall risk score ranging from 0 to 100",
            example = "68"
    )
    private int rainfallRiskScore;

    @Schema(
            description = "Weekly rainfall risk level",
            example = "HIGH"
    )
    private String rainfallRiskLevel;

    @Schema(
            description = "Weekly wind risk score ranging from 0 to 100",
            example = "52"
    )
    private int windRiskScore;

    @Schema(
            description = "Weekly wind risk level",
            example = "HIGH"
    )
    private String windRiskLevel;

    @Schema(
            description = "Weekly heat risk score ranging from 0 to 100",
            example = "31"
    )
    private int heatRiskScore;

    @Schema(
            description = "Weekly heat risk level",
            example = "MODERATE"
    )
    private String heatRiskLevel;

    @Schema(
            description = "Weekly ultraviolet radiation risk score ranging from 0 to 100",
            example = "56"
    )
    private int uvRiskScore;

    @Schema(
            description = "Weekly ultraviolet radiation risk level",
            example = "HIGH"
    )
    private String uvRiskLevel;

    @Schema(
            description = "Weekly pesticide and fertilizer spraying risk score ranging from 0 to 100",
            example = "73"
    )
    private int sprayingRiskScore;

    @Schema(
            description = "Weekly pesticide and fertilizer spraying risk level",
            example = "HIGH"
    )
    private String sprayingRiskLevel;

    @Schema(
            description = "Weekly irrigation attention score ranging from 0 to 100",
            example = "44"
    )
    private int irrigationRiskScore;

    @Schema(
            description = "Weekly irrigation attention risk level",
            example = "MODERATE"
    )
    private String irrigationRiskLevel;
}