package com.agri.market.order.dto;

import com.agri.market.order.entity.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload used to update order status")
public class OrderStatusUpdateRequestDto {

    @NotNull(message = "VALIDATION.ORDER.STATUS.NOT_NULL")
    @JsonProperty("status")
    @Schema(
            description = "New order status",
            example = "PROCESSING",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private OrderStatus status;
}