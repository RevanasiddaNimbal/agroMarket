package com.agri.market.inventory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload used to update product inventory")
public class InventoryUpdateRequestDto {

    @NotNull(message = "VALIDATION.INVENTORY.QUANTITY.NOT_NULL")
    @DecimalMin(
            value = "0.000",
            inclusive = true,
            message = "VALIDATION.INVENTORY.QUANTITY.MIN"
    )
    @JsonProperty("quantity")
    @Schema(
            description = "New total product quantity",
            example = "100.000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal quantity;
}