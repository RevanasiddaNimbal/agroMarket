package com.agri.market.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload used to place an order")
public class PlaceOrderRequestDto {

    @NotBlank(message = "VALIDATION.ORDER.PRODUCT_ID.NOT_BLANK")
    @JsonProperty("product_id")
    @Schema(
            description = "Product identifier",
            example = "6e0b9171-970f-4cd4-832f-e2b345d797d0",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String productId;

    @NotNull(message = "VALIDATION.ORDER.QUANTITY.NOT_NULL")
    @DecimalMin(
            value = "0.001",
            message = "VALIDATION.ORDER.QUANTITY.POSITIVE"
    )
    @JsonProperty("quantity")
    @Schema(
            description = "Quantity to order",
            example = "10.000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal quantity;

    @NotBlank(message = "VALIDATION.ORDER.ADDRESS_ID.NOT_BLANK")
    @JsonProperty("address_id")
    @Schema(
            description = "Delivery address identifier",
            example = "6e0b9171-970f-4cd4-832f-e2b345d797d0",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String addressId;
}