package com.agri.market.weather.service;

import com.agri.market.weather.dto.DailyWeatherRiskDto;
import com.agri.market.weather.dto.FarmingRiskDto;
import com.agri.market.weather.dto.WeeklyWeatherRiskResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface FarmingRiskService {

    FarmingRiskDto calculateDailyRisk(
            DailyWeatherRiskDto weatherData
    );

    FarmingRiskDto calculateDailyRisk(
            LocalDate date,
            DailyWeatherRiskDto weatherData
    );

    WeeklyWeatherRiskResponseDto calculateWeeklyRisk(
            List<DailyWeatherRiskDto> dailyWeatherData
    );
}