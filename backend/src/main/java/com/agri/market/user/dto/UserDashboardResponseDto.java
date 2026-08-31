package com.agri.market.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User dashboard summary")
public class UserDashboardResponseDto {

    @JsonProperty("buying")
    @Schema(description = "User buying activity")
    private UserDashboardBuyingDto buying;

    @JsonProperty("selling")
    @Schema(description = "User selling activity")
    private UserDashboardSellingDto selling;

    @JsonProperty("total_addresses")
    @Schema(
            description = "Total number of addresses belonging to the user",
            example = "3"
    )
    private long totalAddresses;
}