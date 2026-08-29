package com.agri.market.weather.controller;

import com.agri.market.weather.dto.*;
import com.agri.market.weather.service.FarmingRiskService;
import com.agri.market.weather.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Weather and Farming Risk",
        description = "Farmer-focused weather forecast and farming risk APIs"
)
public class WeatherController {

    private final WeatherService weatherService;
    private final FarmingRiskService farmingRiskService;

    @Operation(
            summary = "Get daily weather forecast",
            description = "Retrieves the seven-day farmer-focused weather forecast for the specified location."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Daily weather forecast retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid location coordinates"
            )
    })
    @GetMapping("/daily")
    public DailyWeatherResponseDto getDailyWeather(
            @Parameter(
                    description = "Latitude of the farming location",
                    example = "15.3647",
                    required = true
            )
            @RequestParam
            @Min(-90)
            @Max(90)
            double latitude,

            @Parameter(
                    description = "Longitude of the farming location",
                    example = "75.1240",
                    required = true
            )
            @RequestParam
            @Min(-180)
            @Max(180)
            double longitude
    ) {
        return weatherService.getDailyWeather(
                latitude,
                longitude
        );
    }

    @Operation(
            summary = "Get hourly weather forecast",
            description = "Retrieves farmer-focused hourly weather forecast for the specified location."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Hourly weather forecast retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid location coordinates"
            )
    })
    @GetMapping("/hourly")
    public HourlyWeatherResponseDto getHourlyWeather(
            @Parameter(
                    description = "Latitude of the farming location",
                    example = "15.3647",
                    required = true
            )
            @RequestParam
            @Min(-90)
            @Max(90)
            double latitude,

            @Parameter(
                    description = "Longitude of the farming location",
                    example = "75.1240",
                    required = true
            )
            @RequestParam
            @Min(-180)
            @Max(180)
            double longitude
    ) {
        return weatherService.getHourlyWeather(
                latitude,
                longitude
        );
    }

    @Operation(
            summary = "Calculate daily farming risk",
            description = "Calculates the farming risk for a specific forecast day using weather conditions for the requested location."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Daily farming risk calculated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid location or forecast date"
            )
    })
    @GetMapping("/risk/daily")
    public FarmingRiskDto calculateDailyRisk(
            @Parameter(
                    description = "Latitude of the farming location",
                    example = "15.3647",
                    required = true
            )
            @RequestParam
            @Min(-90)
            @Max(90)
            double latitude,

            @Parameter(
                    description = "Longitude of the farming location",
                    example = "75.1240",
                    required = true
            )
            @RequestParam
            @Min(-180)
            @Max(180)
            double longitude,

            @Parameter(
                    description = "Forecast date for which farming risk should be calculated",
                    example = "2026-08-29"
            )
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        DailyWeatherResponseDto weatherResponse =
                weatherService.getDailyWeather(
                        latitude,
                        longitude
                );

        List<DailyWeatherDto> dailyForecast =
                weatherResponse.getDailyForecast();

        if (dailyForecast == null || dailyForecast.isEmpty()) {
            throw new IllegalArgumentException(
                    "Daily weather forecast is unavailable"
            );
        }

        LocalDate requestedDate =
                date != null
                        ? date
                        : LocalDate.parse(
                        dailyForecast.get(0).getDate()
                );

        DailyWeatherDto selectedWeather =
                dailyForecast.stream()
                        .filter(weather ->
                                requestedDate.toString()
                                        .equals(weather.getDate())
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Weather forecast is unavailable for the requested date"
                                )
                        );

        DailyWeatherRiskDto weatherRiskData =
                DailyWeatherRiskDto.builder()
                        .date(selectedWeather.getDate())
                        .weather(selectedWeather)
                        .build();

        return farmingRiskService.calculateDailyRisk(
                requestedDate,
                weatherRiskData
        );
    }

    @Operation(
            summary = "Calculate weekly farming risk",
            description = "Calculates the overall farming risk, daily risk breakdown, highest-risk day, lowest-risk day, and weekly farming insights for the forecast period."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Weekly farming risk calculated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid location coordinates"
            )
    })
    @GetMapping("/risk/weekly")
    public WeeklyWeatherRiskResponseDto calculateWeeklyRisk(
            @Parameter(
                    description = "Latitude of the farming location",
                    example = "15.3647",
                    required = true
            )
            @RequestParam
            @Min(-90)
            @Max(90)
            double latitude,

            @Parameter(
                    description = "Longitude of the farming location",
                    example = "75.1240",
                    required = true
            )
            @RequestParam
            @Min(-180)
            @Max(180)
            double longitude
    ) {
        DailyWeatherResponseDto weatherResponse =
                weatherService.getDailyWeather(
                        latitude,
                        longitude
                );

        List<DailyWeatherDto> dailyForecast =
                weatherResponse.getDailyForecast();

        if (dailyForecast == null || dailyForecast.isEmpty()) {
            throw new IllegalArgumentException(
                    "Daily weather forecast is unavailable"
            );
        }

        List<DailyWeatherRiskDto> dailyWeatherRiskData =
                dailyForecast.stream()
                        .map(weather ->
                                DailyWeatherRiskDto.builder()
                                        .date(weather.getDate())
                                        .weather(weather)
                                        .build()
                        )
                        .toList();

        return farmingRiskService.calculateWeeklyRisk(
                dailyWeatherRiskData
        );
    }
}