package com.agri.market.inventory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product inventory availability response")
public class InventoryResponseDto {

    @JsonProperty("product_id")
    @Schema(
            description = "Product identifier",
            example = "6e0b9171-970f-4cd4-832f-e2b345d797d0"
    )
    private String productId;

    @JsonProperty("available_quantity")
    @Schema(
            description = "Quantity currently available for purchase",
            example = "80.000"
    )
    private BigDecimal availableQuantity;

    @JsonProperty("reserved_quantity")
    @Schema(
            description = "Quantity currently reserved",
            example = "20.000"
    )
    private BigDecimal reservedQuantity;

    @JsonProperty("unit")
    @Schema(
            description = "Product quantity unit",
            example = "KG"
    )
    private String unit;

    @JsonProperty("available")
    @Schema(
            description = "Indicates whether the product is currently available for purchase",
            example = "true"
    )
    private boolean available;
}