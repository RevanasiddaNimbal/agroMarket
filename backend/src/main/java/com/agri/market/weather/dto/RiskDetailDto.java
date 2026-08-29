package com.agri.market.weather.dto;

import com.agri.market.weather.risk.FarmingRiskLevel;
import com.agri.market.weather.risk.FarmingRiskType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed weather risk assessment for a specific farming risk")
public class RiskDetailDto {

    @Schema(
            description = "Type of farming risk",
            example = "RAINFALL"
    )
    private FarmingRiskType riskType;

    @Schema(
            description = "Risk score ranging from 0 to 100",
            example = "78"
    )
    private int score;

    @Schema(
            description = "Risk level determined from the calculated risk score",
            example = "VERY_HIGH"
    )
    private FarmingRiskLevel level;

    @Schema(
            description = "Explanation of the weather conditions responsible for the calculated risk",
            example = "A high probability of significant rainfall is expected during the day."
    )
    private String reason;

    @Schema(
            description = "Formal recommendation for the farmer based on the assessed risk",
            example = "It is recommended to postpone non-essential outdoor field activities and monitor field drainage."
    )
    private String recommendation;
}