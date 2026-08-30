package com.agri.market.order.dto;

import com.agri.market.order.entity.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order response")
public class OrderResponseDto {

    @JsonProperty("id")
    @Schema(
            description = "Order identifier",
            example = "6e0b9171-970f-4cd4-832f-e2b345d797d0"
    )
    private String id;

    @JsonProperty("status")
    @Schema(
            description = "Current order status",
            example = "CONFIRMED"
    )
    private OrderStatus status;

    @JsonProperty("total_amount")
    @Schema(
            description = "Total order amount",
            example = "1500.00"
    )
    private BigDecimal totalAmount;

    @JsonProperty("address_id")
    @Schema(
            description = "Delivery address identifier",
            example = "7f4e9171-970f-4cd4-832f-e2b345d797d0"
    )
    private String addressId;

    @JsonProperty("items")
    @Schema(description = "Items included in the order")
    private List<OrderItemResponseDto> items;

    @JsonProperty("created_date")
    @Schema(
            description = "Order creation date and time",
            example = "2026-08-30T10:30:00"
    )
    private LocalDateTime createdDate;

    @JsonProperty("last_modified_date")
    @Schema(
            description = "Last order update date and time",
            example = "2026-08-30T11:00:00"
    )
    private LocalDateTime lastModifiedDate;
}