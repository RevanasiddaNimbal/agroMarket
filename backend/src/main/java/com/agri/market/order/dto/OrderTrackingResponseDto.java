package com.agri.market.order.dto;

import com.agri.market.delivery.dto.DeliveryResponseDto;
import com.agri.market.order.entity.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order tracking response")
public class OrderTrackingResponseDto {

    @JsonProperty("order_id")
    @Schema(
            description = "Order identifier",
            example = "6e0b9171-970f-4cd4-832f-e2b345d797d0"
    )
    private String orderId;

    @JsonProperty("status")
    @Schema(
            description = "Current order status",
            example = "OUT_FOR_DELIVERY"
    )
    private OrderStatus status;

    @JsonProperty("total_amount")
    @Schema(
            description = "Total order amount",
            example = "1500.00"
    )
    private BigDecimal totalAmount;

    @JsonProperty("created_date")
    @Schema(
            description = "Order creation date and time",
            example = "2026-08-30T10:30:00"
    )
    private LocalDateTime createdDate;
    
    @JsonProperty("delivery")
    @Schema(description = "Delivery information associated with the order")
    private DeliveryResponseDto delivery;


}

