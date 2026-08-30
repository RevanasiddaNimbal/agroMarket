package com.agri.market.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order item response")
public class OrderItemResponseDto {

    @JsonProperty("id")
    @Schema(
            description = "Order item identifier",
            example = "6e0b9171-970f-4cd4-832f-e2b345d797d0"
    )
    private String id;

    @JsonProperty("product_id")
    @Schema(
            description = "Product identifier",
            example = "7f4e9171-970f-4cd4-832f-e2b345d797d0"
    )
    private String productId;

    @JsonProperty("product_name")
    @Schema(
            description = "Product name",
            example = "Tomato"
    )
    private String productName;

    @JsonProperty("quantity")
    @Schema(
            description = "Quantity purchased",
            example = "5.000"
    )
    private BigDecimal quantity;

    @JsonProperty("unit_price")
    @Schema(
            description = "Product price at the time of purchase",
            example = "100.00"
    )
    private BigDecimal unitPrice;

    @JsonProperty("subtotal")
    @Schema(
            description = "Subtotal for the order item",
            example = "500.00"
    )
    private BigDecimal subtotal;

    @JsonProperty("unit")
    @Schema(
            description = "Product quantity unit",
            example = "KG"
    )
    private String unit;
}