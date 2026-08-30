package com.agri.market.checkout.dto;

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
@Schema(description = "Request payload used to initiate checkout")
public class CheckoutRequestDto {

    @NotBlank(message = "VALIDATION.CHECKOUT.PRODUCT_ID.NOT_BLANK")
    @JsonProperty("product_id")
    @Schema(
            description = "Product identifier",
            example = "6e0b9171-970f-4cd4-832f-e2b345d797d0",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String productId;

    @NotNull(message = "VALIDATION.CHECKOUT.QUANTITY.NOT_NULL")
    @DecimalMin(
            value = "0.001",
            message = "VALIDATION.CHECKOUT.QUANTITY.POSITIVE"
    )
    @JsonProperty("quantity")
    @Schema(
            description = "Quantity of the product to purchase",
            example = "10.000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal quantity;

    @NotBlank(message = "VALIDATION.CHECKOUT.ADDRESS_ID.NOT_BLANK")
    @JsonProperty("address_id")
    @Schema(
            description = "Delivery address identifier",
            example = "6e0b9171-970f-4cd4-832f-e2b345d797d0",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String addressId;
}