package com.agri.market.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User buying activity summary")
public class UserDashboardBuyingDto {

    @JsonProperty("total_orders")
    @Schema(
            description = "Total number of orders placed by the user",
            example = "12"
    )
    private long totalOrders;

    @JsonProperty("pending_payment_orders")
    @Schema(
            description = "Number of orders waiting for payment",
            example = "2"
    )
    private long pendingPaymentOrders;

    @JsonProperty("active_orders")
    @Schema(
            description = "Number of orders currently being processed or delivered",
            example = "4"
    )
    private long activeOrders;

    @JsonProperty("delivered_orders")
    @Schema(
            description = "Number of successfully delivered orders",
            example = "6"
    )
    private long deliveredOrders;
}