package com.agri.market.payment.dto;

import com.agri.market.payment.entity.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payment request")
public class PaymentRequestDto {

    @NotNull(
            message = "VALIDATION.PAYMENT.PAYMENT_METHOD.REQUIRED"
    )
    @Schema(
            description = "Payment method selected by the user",
            example = "UPI"
    )
    private PaymentMethod paymentMethod;
}