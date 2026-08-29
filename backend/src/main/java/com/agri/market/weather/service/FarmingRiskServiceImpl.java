package com.agri.market.weather.service;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.common.exception.ErrorCode;
import com.agri.market.weather.dto.*;
import com.agri.market.weather.risk.FarmingRiskCalculator;
import com.agri.market.weather.risk.FarmingRiskLevel;
import com.agri.market.weather.risk.FarmingRiskType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class FarmingRiskServiceImpl implements FarmingRiskService {

    private final FarmingRiskCalculator farmingRiskCalculator;

    @Override
    public FarmingRiskDto calculateDailyRisk(
            DailyWeatherRiskDto weatherData
    ) {
        validateWeatherData(weatherData);

        return calculateDailyRisk(
                weatherData.getWeather()
        );
    }

    @Override
    public FarmingRiskDto calculateDailyRisk(
            LocalDate date,
            DailyWeatherRiskDto weatherData
    ) {
        if (date == null) {
            throw new BusinessException(
                    ErrorCode.WEATHER_DATE_INVALID
            );
        }

        validateWeatherData(weatherData);

        DailyWeatherDto weather = weatherData.getWeather();

        if (weather.getDate() == null
                || !date.toString().equals(weather.getDate())) {

            log.warn(
                    "Requested risk date {} does not match weather forecast date {}",
                    date,
                    weather.getDate()
            );

            throw new BusinessException(
                    ErrorCode.WEATHER_DATE_INVALID
            );
        }

        return calculateDailyRisk(weather);
    }

    @Override
    public WeeklyWeatherRiskResponseDto calculateWeeklyRisk(
            List<DailyWeatherRiskDto> dailyWeatherData
    ) {
        validateWeeklyWeatherData(dailyWeatherData);

        log.debug(
                "Starting weekly farming risk calculation for {} days",
                dailyWeatherData.size()
        );

        List<DailyWeatherRiskDto> calculatedDailyForecast =
                dailyWeatherData.stream()
                        .map(this::calculateDailyWeatherRisk)
                        .toList();

        int overallRiskScore =
                calculateWeeklyOverallRisk(
                        calculatedDailyForecast
                );

        FarmingRiskLevel overallRiskLevel =
                farmingRiskCalculator.calculateOverallRiskLevel(
                        overallRiskScore
                );

        WeeklyRiskBreakdownDto riskBreakdown =
                buildWeeklyRiskBreakdown(
                        calculatedDailyForecast
                );

        DailyWeatherRiskDto highestRiskDay =
                calculatedDailyForecast.stream()
                        .max(
                                Comparator.comparingInt(
                                        day -> day.getFarmingRisk()
                                                .getOverallRiskScore()
                                )
                        )
                        .orElseThrow();

        DailyWeatherRiskDto lowestRiskDay =
                calculatedDailyForecast.stream()
                        .min(
                                Comparator.comparingInt(
                                        day -> day.getFarmingRisk()
                                                .getOverallRiskScore()
                                )
                        )
                        .orElseThrow();

        List<String> weeklyInsights =
                buildWeeklyInsights(
                        calculatedDailyForecast,
                        highestRiskDay,
                        lowestRiskDay
                );

        log.info(
                "Weekly farming risk calculation completed: score={}, level={}, highestRiskDate={}, highestRiskScore={}, lowestRiskDate={}, lowestRiskScore={}",
                overallRiskScore,
                overallRiskLevel,
                highestRiskDay.getDate(),
                highestRiskDay.getFarmingRisk().getOverallRiskScore(),
                lowestRiskDay.getDate(),
                lowestRiskDay.getFarmingRisk().getOverallRiskScore()
        );

        return WeeklyWeatherRiskResponseDto.builder()
                .overallRiskScore(overallRiskScore)
                .overallRiskLevel(overallRiskLevel)
                .riskBreakdown(riskBreakdown)
                .dailyForecast(calculatedDailyForecast)
                .highestRiskDate(highestRiskDay.getDate())
                .highestRiskScore(
                        highestRiskDay.getFarmingRisk()
                                .getOverallRiskScore()
                )
                .lowestRiskDate(lowestRiskDay.getDate())
                .lowestRiskScore(
                        lowestRiskDay.getFarmingRisk()
                                .getOverallRiskScore()
                )
                .weeklyInsights(weeklyInsights)
                .build();
    }

    private FarmingRiskDto calculateDailyRisk(
            DailyWeatherDto weather
    ) {
        RiskDetailDto rainfallRisk =
                farmingRiskCalculator.calculateRainfallRisk(
                        weather.getPrecipitationProbabilityPercent(),
                        weather.getPrecipitationMillimeters()
                );

        RiskDetailDto windRisk =
                farmingRiskCalculator.calculateWindRisk(
                        weather.getMaximumWindSpeedKmh(),
                        weather.getMaximumWindGustsKmh()
                );

        RiskDetailDto heatRisk =
                farmingRiskCalculator.calculateHeatRisk(
                        weather.getMaximumTemperatureCelsius(),
                        weather.getApparentTemperatureCelsius()
                );

        RiskDetailDto uvRisk =
                farmingRiskCalculator.calculateUvRisk(
                        weather.getUvIndexMax()
                );

        RiskDetailDto sprayingRisk =
                farmingRiskCalculator.calculateSprayingRisk(
                        weather.getMaximumWindSpeedKmh(),
                        weather.getMaximumWindGustsKmh(),
                        weather.getPrecipitationProbabilityPercent(),
                        weather.getPrecipitationMillimeters(),
                        null
                );

        RiskDetailDto irrigationRisk =
                farmingRiskCalculator.calculateIrrigationRisk(
                        weather.getPrecipitationMillimeters(),
                        weather.getPrecipitationProbabilityPercent(),
                        weather.getMaximumTemperatureCelsius(),
                        weather.getEvapotranspirationMillimeters(),
                        null
                );

        int overallRiskScore =
                farmingRiskCalculator.calculateOverallRisk(
                        rainfallRisk,
                        windRisk,
                        heatRisk,
                        uvRisk,
                        sprayingRisk,
                        irrigationRisk
                );

        FarmingRiskLevel overallRiskLevel =
                farmingRiskCalculator.calculateOverallRiskLevel(
                        overallRiskScore
                );

        String summary =
                farmingRiskCalculator.buildOverallSummary(
                        rainfallRisk,
                        windRisk,
                        heatRisk,
                        uvRisk,
                        sprayingRisk,
                        irrigationRisk
                );

        List<String> recommendations =
                farmingRiskCalculator.buildRecommendations(
                        rainfallRisk,
                        windRisk,
                        heatRisk,
                        uvRisk,
                        sprayingRisk,
                        irrigationRisk
                );

        return FarmingRiskDto.builder()
                .overallRiskScore(overallRiskScore)
                .overallRiskLevel(overallRiskLevel)
                .rainfallRisk(rainfallRisk)
                .windRisk(windRisk)
                .heatRisk(heatRisk)
                .uvRisk(uvRisk)
                .sprayingRisk(sprayingRisk)
                .irrigationRisk(irrigationRisk)
                .summary(summary)
                .recommendations(recommendations)
                .build();
    }

    private DailyWeatherRiskDto calculateDailyWeatherRisk(
            DailyWeatherRiskDto weatherData
    ) {
        validateWeatherData(weatherData);

        FarmingRiskDto farmingRisk =
                calculateDailyRisk(weatherData);

        return DailyWeatherRiskDto.builder()
                .date(weatherData.getWeather().getDate())
                .weather(weatherData.getWeather())
                .farmingRisk(farmingRisk)
                .build();
    }

    private int calculateWeeklyOverallRisk(
            List<DailyWeatherRiskDto> dailyForecast
    ) {
        return (int) Math.round(
                dailyForecast.stream()
                        .map(DailyWeatherRiskDto::getFarmingRisk)
                        .filter(Objects::nonNull)
                        .mapToInt(
                                FarmingRiskDto::getOverallRiskScore
                        )
                        .average()
                        .orElse(0)
        );
    }

    private WeeklyRiskBreakdownDto buildWeeklyRiskBreakdown(
            List<DailyWeatherRiskDto> dailyForecast
    ) {
        int rainfallRiskScore =
                calculateAverageRisk(
                        dailyForecast,
                        FarmingRiskType.RAINFALL
                );

        int windRiskScore =
                calculateAverageRisk(
                        dailyForecast,
                        FarmingRiskType.WIND
                );

        int heatRiskScore =
                calculateAverageRisk(
                        dailyForecast,
                        FarmingRiskType.HEAT
                );

        int uvRiskScore =
                calculateAverageRisk(
                        dailyForecast,
                        FarmingRiskType.UV
                );

        int sprayingRiskScore =
                calculateAverageRisk(
                        dailyForecast,
                        FarmingRiskType.SPRAYING
                );

        int irrigationRiskScore =
                calculateAverageRisk(
                        dailyForecast,
                        FarmingRiskType.IRRIGATION
                );

        return WeeklyRiskBreakdownDto.builder()
                .rainfallRiskScore(rainfallRiskScore)
                .rainfallRiskLevel(
                        farmingRiskCalculator
                                .determineRiskLevel(rainfallRiskScore)
                                .name()
                )
                .windRiskScore(windRiskScore)
                .windRiskLevel(
                        farmingRiskCalculator
                                .determineRiskLevel(windRiskScore)
                                .name()
                )
                .heatRiskScore(heatRiskScore)
                .heatRiskLevel(
                        farmingRiskCalculator
                                .determineRiskLevel(heatRiskScore)
                                .name()
                )
                .uvRiskScore(uvRiskScore)
                .uvRiskLevel(
                        farmingRiskCalculator
                                .determineRiskLevel(uvRiskScore)
                                .name()
                )
                .sprayingRiskScore(sprayingRiskScore)
                .sprayingRiskLevel(
                        farmingRiskCalculator
                                .determineRiskLevel(sprayingRiskScore)
                                .name()
                )
                .irrigationRiskScore(irrigationRiskScore)
                .irrigationRiskLevel(
                        farmingRiskCalculator
                                .determineRiskLevel(irrigationRiskScore)
                                .name()
                )
                .build();
    }

    private int calculateAverageRisk(
            List<DailyWeatherRiskDto> dailyForecast,
            FarmingRiskType riskType
    ) {
        return (int) Math.round(
                dailyForecast.stream()
                        .map(DailyWeatherRiskDto::getFarmingRisk)
                        .filter(Objects::nonNull)
                        .mapToInt(
                                risk -> getRiskScore(
                                        risk,
                                        riskType
                                )
                        )
                        .average()
                        .orElse(0)
        );
    }

    private int getRiskScore(
            FarmingRiskDto farmingRisk,
            FarmingRiskType riskType
    ) {
        return switch (riskType) {
            case RAINFALL -> farmingRisk.getRainfallRisk().getScore();

            case WIND -> farmingRisk.getWindRisk().getScore();

            case HEAT -> farmingRisk.getHeatRisk().getScore();

            case UV -> farmingRisk.getUvRisk().getScore();

            case SPRAYING -> farmingRisk.getSprayingRisk().getScore();

            case IRRIGATION -> farmingRisk.getIrrigationRisk().getScore();

            case OVERALL -> farmingRisk.getOverallRiskScore();
        };
    }

    private List<String> buildWeeklyInsights(
            List<DailyWeatherRiskDto> dailyForecast,
            DailyWeatherRiskDto highestRiskDay,
            DailyWeatherRiskDto lowestRiskDay
    ) {
        List<String> insights = new ArrayList<>();

        insights.add(
                "The highest farming risk during the forecast period is expected on "
                        + highestRiskDay.getDate()
                        + " with a risk score of "
                        + highestRiskDay.getFarmingRisk()
                        .getOverallRiskScore()
                        + "."
        );

        insights.add(
                "The lowest farming risk during the forecast period is expected on "
                        + lowestRiskDay.getDate()
                        + " with a risk score of "
                        + lowestRiskDay.getFarmingRisk()
                        .getOverallRiskScore()
                        + "."
        );

        long highRiskDays =
                dailyForecast.stream()
                        .filter(
                                day ->
                                        day.getFarmingRisk()
                                                .getOverallRiskScore() >= 51
                        )
                        .count();

        if (highRiskDays > 0) {
            insights.add(
                    highRiskDays
                            + " day(s) in the forecast period have elevated farming risk and may require adjustments to planned field activities."
            );
        } else {
            insights.add(
                    "No day in the forecast period has an elevated overall farming risk based on the assessed weather conditions."
            );
        }

        return insights;
    }

    private void validateWeatherData(
            DailyWeatherRiskDto weatherData
    ) {
        if (weatherData == null
                || weatherData.getWeather() == null) {

            throw new BusinessException(
                    ErrorCode.WEATHER_DATA_INVALID
            );
        }
    }

    private void validateWeeklyWeatherData(
            List<DailyWeatherRiskDto> dailyWeatherData
    ) {
        if (dailyWeatherData == null
                || dailyWeatherData.isEmpty()) {

            throw new BusinessException(
                    ErrorCode.WEATHER_WEEKLY_DATA_EMPTY
            );
        }

        if (dailyWeatherData.size() > 7) {
            throw new BusinessException(
                    ErrorCode.WEATHER_WEEKLY_DATA_LIMIT_EXCEEDED
            );
        }

        boolean containsInvalidData =
                dailyWeatherData.stream()
                        .anyMatch(
                                data ->
                                        data == null
                                                || data.getWeather() == null
                        );

        if (containsInvalidData) {
            throw new BusinessException(
                    ErrorCode.WEATHER_WEEKLY_DATA_INVALID
            );
        }
    }
}