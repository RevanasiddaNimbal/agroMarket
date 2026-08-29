package com.agri.market.weather.risk;

import com.agri.market.weather.dto.RiskDetailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class FarmingRiskCalculator {

    private static final int MIN_SCORE = 0;
    private static final int MAX_SCORE = 100;

    private static final double RAINFALL_WEIGHT = 0.20;
    private static final double WIND_WEIGHT = 0.15;
    private static final double HEAT_WEIGHT = 0.15;
    private static final double UV_WEIGHT = 0.10;
    private static final double SPRAYING_WEIGHT = 0.20;
    private static final double IRRIGATION_WEIGHT = 0.20;

    public FarmingRiskLevel determineRiskLevel(int score) {
        return FarmingRiskLevel.fromScore(normalizeScore(score));
    }

    public RiskDetailDto calculateRainfallRisk(
            Double precipitationProbability,
            Double precipitation
    ) {
        int score = calculateRainfallScore(
                precipitationProbability,
                precipitation
        );

        FarmingRiskLevel level = determineRiskLevel(score);

        String reason = buildRainfallReason(level);
        String recommendation = buildRainfallRecommendation(level);

        log.debug(
                "Rainfall risk calculated: score={}, level={}, probability={}, precipitation={}",
                score,
                level,
                precipitationProbability,
                precipitation
        );

        return RiskDetailDto.builder()
                .riskType(FarmingRiskType.RAINFALL)
                .score(score)
                .level(level)
                .reason(reason)
                .recommendation(recommendation)
                .build();
    }

    public RiskDetailDto calculateWindRisk(
            Double windSpeed,
            Double windGusts
    ) {
        int score = calculateWindScore(
                windSpeed,
                windGusts
        );

        FarmingRiskLevel level = determineRiskLevel(score);

        String reason = buildWindReason(level);
        String recommendation = buildWindRecommendation(level);

        log.debug(
                "Wind risk calculated: score={}, level={}, windSpeed={}, windGusts={}",
                score,
                level,
                windSpeed,
                windGusts
        );

        return RiskDetailDto.builder()
                .riskType(FarmingRiskType.WIND)
                .score(score)
                .level(level)
                .reason(reason)
                .recommendation(recommendation)
                .build();
    }

    public RiskDetailDto calculateHeatRisk(
            Double maximumTemperature,
            Double maximumApparentTemperature
    ) {
        int score = calculateHeatScore(
                maximumTemperature,
                maximumApparentTemperature
        );

        FarmingRiskLevel level = determineRiskLevel(score);

        String reason = buildHeatReason(level);
        String recommendation = buildHeatRecommendation(level);

        log.debug(
                "Heat risk calculated: score={}, level={}, temperature={}, apparentTemperature={}",
                score,
                level,
                maximumTemperature,
                maximumApparentTemperature
        );

        return RiskDetailDto.builder()
                .riskType(FarmingRiskType.HEAT)
                .score(score)
                .level(level)
                .reason(reason)
                .recommendation(recommendation)
                .build();
    }

    public RiskDetailDto calculateUvRisk(
            Double maximumUvIndex
    ) {
        int score = calculateUvScore(maximumUvIndex);

        FarmingRiskLevel level = determineRiskLevel(score);

        String reason = buildUvReason(level);
        String recommendation = buildUvRecommendation(level);

        log.debug(
                "UV risk calculated: score={}, level={}, uvIndex={}",
                score,
                level,
                maximumUvIndex
        );

        return RiskDetailDto.builder()
                .riskType(FarmingRiskType.UV)
                .score(score)
                .level(level)
                .reason(reason)
                .recommendation(recommendation)
                .build();
    }

    public RiskDetailDto calculateSprayingRisk(
            Double windSpeed,
            Double windGusts,
            Double precipitationProbability,
            Double precipitation,
            Double relativeHumidity
    ) {
        int windSpeedScore = calculateWindSpeedScore(windSpeed);
        int windGustScore = calculateWindGustScore(windGusts);
        int precipitationProbabilityScore =
                calculateSprayingRainProbabilityScore(
                        precipitationProbability
                );
        int precipitationAmountScore =
                calculateSprayingRainAmountScore(
                        precipitation
                );
        int humidityScore =
                calculateSprayingHumidityScore(
                        relativeHumidity
                );

        int score = normalizeScore(
                (int) Math.round(
                        windSpeedScore * 0.25
                                + windGustScore * 0.20
                                + precipitationProbabilityScore * 0.30
                                + precipitationAmountScore * 0.20
                                + humidityScore * 0.05
                )
        );

        FarmingRiskLevel level = determineRiskLevel(score);

        String reason = buildSprayingReason(level);
        String recommendation = buildSprayingRecommendation(level);

        log.debug(
                "Spraying risk calculated: score={}, level={}, windSpeed={}, windGusts={}, precipitationProbability={}, precipitation={}, humidity={}",
                score,
                level,
                windSpeed,
                windGusts,
                precipitationProbability,
                precipitation,
                relativeHumidity
        );

        return RiskDetailDto.builder()
                .riskType(FarmingRiskType.SPRAYING)
                .score(score)
                .level(level)
                .reason(reason)
                .recommendation(recommendation)
                .build();
    }

    public RiskDetailDto calculateIrrigationRisk(
            Double precipitation,
            Double precipitationProbability,
            Double maximumTemperature,
            Double et0,
            Double soilMoisture
    ) {
        int rainfallScore = calculateRainfallScore(
                precipitationProbability,
                precipitation
        );

        int heatScore = calculateHeatScore(
                maximumTemperature,
                maximumTemperature
        );

        int et0Score = calculateEt0Score(et0);
        int soilMoistureScore = calculateSoilMoistureScore(soilMoisture);

        int irrigationDemandFromRainfall =
                100 - rainfallScore;

        int score = normalizeScore(
                (int) Math.round(
                        irrigationDemandFromRainfall * 0.35
                                + et0Score * 0.25
                                + heatScore * 0.15
                                + soilMoistureScore * 0.25
                )
        );

        FarmingRiskLevel level = determineRiskLevel(score);

        String reason = buildIrrigationReason(level);
        String recommendation = buildIrrigationRecommendation(level);

        log.debug(
                "Irrigation risk calculated: score={}, level={}, precipitation={}, probability={}, temperature={}, et0={}, soilMoisture={}",
                score,
                level,
                precipitation,
                precipitationProbability,
                maximumTemperature,
                et0,
                soilMoisture
        );

        return RiskDetailDto.builder()
                .riskType(FarmingRiskType.IRRIGATION)
                .score(score)
                .level(level)
                .reason(reason)
                .recommendation(recommendation)
                .build();
    }

    public int calculateOverallRisk(
            RiskDetailDto rainfallRisk,
            RiskDetailDto windRisk,
            RiskDetailDto heatRisk,
            RiskDetailDto uvRisk,
            RiskDetailDto sprayingRisk,
            RiskDetailDto irrigationRisk
    ) {
        validateRiskDetails(
                rainfallRisk,
                windRisk,
                heatRisk,
                uvRisk,
                sprayingRisk,
                irrigationRisk
        );

        int weightedScore = (int) Math.round(
                rainfallRisk.getScore() * RAINFALL_WEIGHT
                        + windRisk.getScore() * WIND_WEIGHT
                        + heatRisk.getScore() * HEAT_WEIGHT
                        + uvRisk.getScore() * UV_WEIGHT
                        + sprayingRisk.getScore() * SPRAYING_WEIGHT
                        + irrigationRisk.getScore() * IRRIGATION_WEIGHT
        );

        int finalScore = applyCriticalRiskOverrides(
                weightedScore,
                rainfallRisk,
                windRisk,
                heatRisk,
                uvRisk,
                sprayingRisk,
                irrigationRisk
        );

        log.debug(
                "Overall farming risk calculated: weightedScore={}, finalScore={}",
                weightedScore,
                finalScore
        );

        return finalScore;
    }

    public FarmingRiskLevel calculateOverallRiskLevel(
            int overallRiskScore
    ) {
        return determineRiskLevel(overallRiskScore);
    }

    public String buildOverallSummary(
            RiskDetailDto rainfallRisk,
            RiskDetailDto windRisk,
            RiskDetailDto heatRisk,
            RiskDetailDto uvRisk,
            RiskDetailDto sprayingRisk,
            RiskDetailDto irrigationRisk
    ) {
        List<String> reasons = new ArrayList<>();

        addSignificantReason(reasons, rainfallRisk);
        addSignificantReason(reasons, windRisk);
        addSignificantReason(reasons, heatRisk);
        addSignificantReason(reasons, uvRisk);
        addSignificantReason(reasons, sprayingRisk);
        addSignificantReason(reasons, irrigationRisk);

        if (reasons.isEmpty()) {
            return "Weather conditions are generally favorable for routine farming activities.";
        }

        return String.join(" ", reasons);
    }

    public List<String> buildRecommendations(
            RiskDetailDto rainfallRisk,
            RiskDetailDto windRisk,
            RiskDetailDto heatRisk,
            RiskDetailDto uvRisk,
            RiskDetailDto sprayingRisk,
            RiskDetailDto irrigationRisk
    ) {
        List<String> recommendations = new ArrayList<>();

        addRecommendation(recommendations, rainfallRisk);
        addRecommendation(recommendations, windRisk);
        addRecommendation(recommendations, heatRisk);
        addRecommendation(recommendations, uvRisk);
        addRecommendation(recommendations, sprayingRisk);
        addRecommendation(recommendations, irrigationRisk);

        if (recommendations.isEmpty()) {
            recommendations.add(
                    "Weather conditions are generally favorable for routine farming activities. Continue normal field operations while monitoring local conditions."
            );
        }

        return recommendations.stream()
                .distinct()
                .toList();
    }

    private int calculateRainfallScore(
            Double precipitationProbability,
            Double precipitation
    ) {
        int probabilityScore =
                normalizePercentage(precipitationProbability);

        int precipitationScore =
                normalizeRainAmount(precipitation);

        return normalizeScore(
                (int) Math.round(
                        probabilityScore * 0.35
                                + precipitationScore * 0.65
                )
        );
    }

    private int calculateWindScore(
            Double windSpeed,
            Double windGusts
    ) {
        int windSpeedScore =
                calculateWindSpeedScore(windSpeed);

        int gustScore =
                calculateWindGustScore(windGusts);

        return normalizeScore(
                (int) Math.round(
                        windSpeedScore * 0.65
                                + gustScore * 0.35
                )
        );
    }

    private int calculateWindSpeedScore(
            Double windSpeed
    ) {
        if (windSpeed == null || windSpeed <= 0) {
            return 0;
        }

        if (windSpeed < 15) {
            return 10;
        }

        if (windSpeed < 25) {
            return 30;
        }

        if (windSpeed < 35) {
            return 60;
        }

        if (windSpeed < 45) {
            return 80;
        }

        return 100;
    }

    private int calculateWindGustScore(
            Double windGusts
    ) {
        if (windGusts == null || windGusts <= 0) {
            return 0;
        }

        if (windGusts < 20) {
            return 10;
        }

        if (windGusts < 30) {
            return 30;
        }

        if (windGusts < 40) {
            return 60;
        }

        if (windGusts < 50) {
            return 80;
        }

        return 100;
    }

    private int calculateHeatScore(
            Double maximumTemperature,
            Double maximumApparentTemperature
    ) {
        if (maximumTemperature == null) {
            return 0;
        }

        double effectiveTemperature =
                maximumTemperature;

        if (maximumApparentTemperature != null) {
            effectiveTemperature = Math.max(
                    maximumTemperature,
                    maximumApparentTemperature
            );
        }

        if (effectiveTemperature < 30) {
            return 10;
        }

        if (effectiveTemperature < 33) {
            return 25;
        }

        if (effectiveTemperature < 36) {
            return 45;
        }

        if (effectiveTemperature < 40) {
            return 70;
        }

        if (effectiveTemperature < 43) {
            return 85;
        }

        return 100;
    }

    private int calculateUvScore(
            Double maximumUvIndex
    ) {
        if (maximumUvIndex == null || maximumUvIndex <= 0) {
            return 0;
        }

        if (maximumUvIndex < 3) {
            return 10;
        }

        if (maximumUvIndex < 6) {
            return 30;
        }

        if (maximumUvIndex < 8) {
            return 55;
        }

        if (maximumUvIndex < 11) {
            return 75;
        }

        return 100;
    }

    private int calculateHumidityScore(
            Double relativeHumidity
    ) {
        if (relativeHumidity == null) {
            return 0;
        }

        if (relativeHumidity < 40) {
            return 20;
        }

        if (relativeHumidity < 60) {
            return 30;
        }

        if (relativeHumidity < 75) {
            return 45;
        }

        if (relativeHumidity < 90) {
            return 65;
        }

        return 80;
    }

    private int calculateSprayingHumidityScore(
            Double relativeHumidity
    ) {
        if (relativeHumidity == null) {
            return 0;
        }

        if (relativeHumidity < 40) {
            return 15;
        }

        if (relativeHumidity < 60) {
            return 10;
        }

        if (relativeHumidity < 75) {
            return 25;
        }

        if (relativeHumidity < 90) {
            return 55;
        }

        return 75;
    }

    private int calculateEt0Score(
            Double et0
    ) {
        if (et0 == null || et0 <= 0) {
            return 0;
        }

        if (et0 < 2) {
            return 10;
        }

        if (et0 < 4) {
            return 30;
        }

        if (et0 < 6) {
            return 55;
        }

        if (et0 < 8) {
            return 75;
        }

        return 90;
    }

    private int calculateSoilMoistureScore(
            Double soilMoisture
    ) {
        if (soilMoisture == null) {
            return 0;
        }

        double percentage =
                soilMoisture * 100;

        if (percentage >= 60) {
            return 10;
        }

        if (percentage >= 40) {
            return 30;
        }

        if (percentage >= 25) {
            return 55;
        }

        if (percentage >= 15) {
            return 75;
        }

        return 90;
    }

    private int normalizePercentage(
            Double value
    ) {
        if (value == null) {
            return 0;
        }

        return normalizeScore(
                (int) Math.round(value)
        );
    }

    private int normalizeRainAmount(
            Double precipitation
    ) {
        if (precipitation == null
                || precipitation <= 0) {
            return 0;
        }

        if (precipitation < 2) {
            return 10;
        }

        if (precipitation < 5) {
            return 25;
        }

        if (precipitation < 10) {
            return 45;
        }

        if (precipitation < 20) {
            return 65;
        }

        if (precipitation < 30) {
            return 80;
        }

        return 100;
    }

    private int calculateSprayingRainProbabilityScore(
            Double probability
    ) {
        if (probability == null
                || probability <= 0) {
            return 0;
        }

        if (probability < 20) {
            return 10;
        }

        if (probability < 40) {
            return 25;
        }

        if (probability < 60) {
            return 45;
        }

        if (probability < 80) {
            return 70;
        }

        return 90;
    }

    private int calculateSprayingRainAmountScore(
            Double precipitation
    ) {
        if (precipitation == null
                || precipitation <= 0) {
            return 0;
        }

        if (precipitation < 1) {
            return 10;
        }

        if (precipitation < 3) {
            return 30;
        }

        if (precipitation < 5) {
            return 50;
        }

        if (precipitation < 10) {
            return 75;
        }

        return 100;
    }

    private int applyCriticalRiskOverrides(
            int weightedScore,
            RiskDetailDto rainfallRisk,
            RiskDetailDto windRisk,
            RiskDetailDto heatRisk,
            RiskDetailDto uvRisk,
            RiskDetailDto sprayingRisk,
            RiskDetailDto irrigationRisk
    ) {
        int finalScore = weightedScore;

        List<RiskDetailDto> risks = List.of(
                rainfallRisk,
                windRisk,
                heatRisk,
                uvRisk,
                sprayingRisk,
                irrigationRisk
        );

        long veryHighRisks = risks.stream()
                .filter(risk -> risk.getScore() >= 76)
                .count();

        int maximumRisk = risks.stream()
                .mapToInt(RiskDetailDto::getScore)
                .max()
                .orElse(0);

        if (maximumRisk >= 90) {
            finalScore = Math.max(
                    finalScore,
                    76
            );
        }

        if (veryHighRisks >= 2) {
            finalScore = Math.max(
                    finalScore,
                    76
            );
        }

        return normalizeScore(finalScore);
    }

    private void validateRiskDetails(
            RiskDetailDto... risks
    ) {
        for (RiskDetailDto risk : risks) {
            if (risk == null) {
                throw new IllegalArgumentException(
                        "Risk details must not contain null values"
                );
            }

            if (risk.getScore() < MIN_SCORE
                    || risk.getScore() > MAX_SCORE) {
                throw new IllegalArgumentException(
                        "Risk score must be between 0 and 100"
                );
            }
        }
    }

    private int normalizeScore(
            int score
    ) {
        return Math.max(
                MIN_SCORE,
                Math.min(
                        MAX_SCORE,
                        score
                )
        );
    }

    private void addSignificantReason(
            List<String> reasons,
            RiskDetailDto risk
    ) {
        if (risk != null
                && risk.getScore() >= 51
                && risk.getReason() != null
                && !risk.getReason().isBlank()) {
            reasons.add(risk.getReason());
        }
    }

    private void addRecommendation(
            List<String> recommendations,
            RiskDetailDto risk
    ) {
        if (risk != null
                && risk.getScore() >= 51
                && risk.getRecommendation() != null
                && !risk.getRecommendation().isBlank()) {
            recommendations.add(
                    risk.getRecommendation()
            );
        }
    }

    private String buildRainfallReason(
            FarmingRiskLevel level
    ) {
        return switch (level) {
            case VERY_HIGH ->
                    "Very high rainfall activity is expected and may significantly disrupt outdoor farming operations.";
            case HIGH -> "Rainfall conditions may interfere with planned outdoor farming activities.";
            case MODERATE -> "Rainfall is possible and may require adjustments to some farming activities.";
            case LOW -> "Rainfall conditions are generally favorable for routine farming activities.";
        };
    }

    private String buildRainfallRecommendation(
            FarmingRiskLevel level
    ) {
        return switch (level) {
            case VERY_HIGH ->
                    "It is recommended to postpone non-essential outdoor field activities and monitor field drainage conditions.";
            case HIGH ->
                    "It is recommended to monitor rainfall conditions and adjust outdoor field activities accordingly.";
            case MODERATE ->
                    "It is advisable to monitor the forecast before scheduling weather-sensitive field activities.";
            case LOW ->
                    "Routine outdoor farming activities may generally proceed while monitoring local weather conditions.";
        };
    }

    private String buildWindReason(
            FarmingRiskLevel level
    ) {
        return switch (level) {
            case VERY_HIGH ->
                    "Very strong winds and potentially damaging gusts are expected, which may affect outdoor farming operations.";
            case HIGH -> "Elevated wind speeds may affect field operations and weather-sensitive activities.";
            case MODERATE -> "Moderate wind conditions may affect some weather-sensitive farming activities.";
            case LOW -> "Wind conditions are generally favorable for routine farming activities.";
        };
    }

    private String buildWindRecommendation(
            FarmingRiskLevel level
    ) {
        return switch (level) {
            case VERY_HIGH ->
                    "It is recommended to avoid weather-sensitive outdoor operations and secure lightweight agricultural materials.";
            case HIGH ->
                    "It is recommended to exercise caution during outdoor operations and avoid activities that are sensitive to strong winds.";
            case MODERATE ->
                    "It is advisable to monitor wind conditions before conducting weather-sensitive field activities.";
            case LOW -> "Routine outdoor activities may generally proceed under the expected wind conditions.";
        };
    }

    private String buildHeatReason(
            FarmingRiskLevel level
    ) {
        return switch (level) {
            case VERY_HIGH ->
                    "Very high temperatures or apparent temperatures are expected, increasing heat exposure during outdoor activities.";
            case HIGH ->
                    "High temperatures are expected and may increase heat exposure during outdoor farming activities.";
            case MODERATE ->
                    "Moderately high temperatures may increase heat exposure during prolonged outdoor activities.";
            case LOW -> "Temperature conditions are generally favorable for routine outdoor activities.";
        };
    }

    private String buildHeatRecommendation(
            FarmingRiskLevel level
    ) {
        return switch (level) {
            case VERY_HIGH ->
                    "It is recommended to limit prolonged outdoor work during the hottest part of the day and provide adequate rest and hydration.";
            case HIGH ->
                    "It is recommended to schedule demanding outdoor activities during relatively cooler periods and maintain adequate hydration.";
            case MODERATE ->
                    "It is advisable to plan prolonged outdoor activities with appropriate rest periods and hydration.";
            case LOW -> "Temperature conditions are generally suitable for routine outdoor activities.";
        };
    }

    private String buildUvReason(
            FarmingRiskLevel level
    ) {
        return switch (level) {
            case VERY_HIGH -> "Very high ultraviolet radiation is expected during the day.";
            case HIGH -> "High ultraviolet radiation is expected during the day.";
            case MODERATE -> "Moderate ultraviolet radiation is expected during the day.";
            case LOW -> "Ultraviolet radiation conditions are generally favorable for routine outdoor activities.";
        };
    }

    private String buildUvRecommendation(
            FarmingRiskLevel level
    ) {
        return switch (level) {
            case VERY_HIGH ->
                    "It is recommended to reduce prolonged direct sun exposure and schedule outdoor work during periods of lower ultraviolet radiation when practical.";
            case HIGH ->
                    "It is recommended to reduce prolonged direct sun exposure and take appropriate protective measures during outdoor work.";
            case MODERATE -> "It is advisable to consider sun protection during prolonged outdoor activities.";
            case LOW ->
                    "Normal precautions for outdoor exposure are generally sufficient under the expected ultraviolet conditions.";
        };
    }

    private String buildSprayingReason(
            FarmingRiskLevel level
    ) {
        return switch (level) {
            case VERY_HIGH ->
                    "The combination of rainfall and wind conditions is highly unfavorable for spraying activities.";
            case HIGH -> "Weather conditions may reduce the suitability and effectiveness of spraying activities.";
            case MODERATE -> "Some weather conditions may affect the suitability of spraying activities.";
            case LOW -> "Forecast conditions are generally favorable for weather-sensitive spraying activities.";
        };
    }

    private String buildSprayingRecommendation(
            FarmingRiskLevel level
    ) {
        return switch (level) {
            case VERY_HIGH ->
                    "It is strongly recommended to avoid weather-sensitive spraying activities under the forecast conditions.";
            case HIGH ->
                    "It is recommended to postpone weather-sensitive spraying activities until more favorable conditions are expected.";
            case MODERATE ->
                    "It is advisable to verify rainfall and wind conditions before conducting spraying activities.";
            case LOW ->
                    "Weather conditions are generally favorable for weather-sensitive spraying activities; applicable product instructions should be followed.";
        };
    }

    private String buildIrrigationReason(
            FarmingRiskLevel level
    ) {
        return switch (level) {
            case VERY_HIGH ->
                    "Low expected rainfall combined with elevated atmospheric water demand may significantly increase irrigation requirements.";
            case HIGH -> "Forecast conditions indicate increased attention may be required for irrigation planning.";
            case MODERATE -> "Forecast conditions indicate that irrigation requirements may vary during the day.";
            case LOW -> "Expected rainfall and atmospheric conditions indicate relatively low irrigation pressure.";
        };
    }

    private String buildIrrigationRecommendation(
            FarmingRiskLevel level
    ) {
        return switch (level) {
            case VERY_HIGH ->
                    "It is recommended to assess field moisture conditions carefully and plan irrigation according to actual crop and soil requirements.";
            case HIGH ->
                    "It is recommended to monitor field moisture and consider irrigation planning based on local soil conditions.";
            case MODERATE -> "It is advisable to monitor field moisture before making irrigation decisions.";
            case LOW ->
                    "Irrigation demand is expected to be relatively low; actual field moisture should be verified before irrigation.";
        };
    }
}