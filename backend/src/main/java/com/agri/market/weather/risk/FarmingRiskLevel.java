package com.agri.market.weather.risk;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Risk levels used for farmer-focused weather risk assessment")
public enum FarmingRiskLevel {

    LOW(0, 25),

    MODERATE(26, 50),

    HIGH(51, 75),

    VERY_HIGH(76, 100);

    private final int minimumScore;
    private final int maximumScore;

    FarmingRiskLevel(
            int minimumScore,
            int maximumScore
    ) {
        this.minimumScore = minimumScore;
        this.maximumScore = maximumScore;
    }

    public static FarmingRiskLevel fromScore(int score) {
        int normalizedScore = Math.max(
                0,
                Math.min(100, score)
        );

        for (FarmingRiskLevel level : values()) {
            if (normalizedScore >= level.minimumScore
                    && normalizedScore <= level.maximumScore) {
                return level;
            }
        }

        return VERY_HIGH;
    }

    public int getMinimumScore() {
        return minimumScore;
    }

    public int getMaximumScore() {
        return maximumScore;
    }
}