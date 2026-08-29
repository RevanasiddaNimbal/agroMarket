package com.agri.market.weather.risk;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Types of weather-related risks assessed for farming activities")
public enum FarmingRiskType {

    RAINFALL,

    WIND,

    HEAT,

    UV,

    SPRAYING,

    IRRIGATION,

    OVERALL
}