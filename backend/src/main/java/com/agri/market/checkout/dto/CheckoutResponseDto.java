package com.agri.market.checkout.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Checkout response")
public class CheckoutResponseDto {

    @Schema(
            description = "Product identifier",
            example = "6e0b9171-970f-4cd4-832f-e2b345d797d0"
    )
    private String productId;

    @Schema(
            description = "Product name",
            example = "Tomato"
    )
    private String productName;

    @Schema(
            description = "Requested purchase quantity",
            example = "10.000"
    )
    private BigDecimal quantity;

    @Schema(
            description = "Product price per unit",
            example = "50.00"
    )
    private BigDecimal unitPrice;

    @Schema(
            description = "Total checkout amount",
            example = "500.00"
    )
    private BigDecimal totalAmount;

    @Schema(
            description = "Delivery address identifier",
            example = "6e0b9171-970f-4cd4-832f-e2b345d797d0"
    )
    private String addressId;

    @Schema(
            description = "Checkout status",
            example = "PENDING_PAYMENT"
    )
    private String status;
}