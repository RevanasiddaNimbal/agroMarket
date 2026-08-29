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
@Schema(description = "Request payload used to adjust product stock")
public class StockAdjustmentRequestDto {

    @NotNull(message = "VALIDATION.INVENTORY.ADJUSTMENT.NOT_NULL")
    @DecimalMin(
            value = "0.001",
            inclusive = true,
            message = "VALIDATION.INVENTORY.ADJUSTMENT.MIN"
    )
    @JsonProperty("quantity")
    @Schema(
            description = "Quantity to add or remove from product stock",
            example = "25.000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal quantity;
}