package com.agri.market.payment.controller;

import com.agri.market.payment.dto.PaymentRequestDto;
import com.agri.market.payment.dto.PaymentResponseDto;
import com.agri.market.payment.dto.RefundResponseDto;
import com.agri.market.payment.service.PaymentService;
import com.agri.market.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Payments",
        description = "APIs for payment and refund management"
)
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "Process payment for an order",
            description = "Processes payment for the authenticated user's order using the configured payment provider."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment processed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid payment request or payment cannot be processed"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Payment already exists or order cannot be paid"
            )
    })
    @PostMapping("/orders/{orderId}")
    public ResponseEntity<PaymentResponseDto> processPayment(
            @PathVariable final String orderId,
            @Valid @RequestBody final PaymentRequestDto request,
            @AuthenticationPrincipal final User user
    ) {

        log.info(
                "Payment request received. Order: {}, User: {}, Method: {}",
                orderId,
                user.getId(),
                request.getPaymentMethod()
        );

        final PaymentResponseDto response =
                paymentService.processPayment(
                        orderId,
                        user.getId(),
                        request
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get payment for an order",
            description = "Returns the payment information for an order belonging to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment or order not found"
            )
    })
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<PaymentResponseDto> getPayment(
            @PathVariable final String orderId,
            @AuthenticationPrincipal final User user
    ) {

        log.info(
                "Get payment request received. Order: {}, User: {}",
                orderId,
                user.getId()
        );

        final PaymentResponseDto response =
                paymentService.getPayment(
                        orderId,
                        user.getId()
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Refund payment for a cancelled order",
            description = "Refunds the payment associated with an order when the authenticated user is eligible for cancellation."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment refunded successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payment cannot be refunded"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order or payment not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Payment has already been refunded or cannot be refunded"
            )
    })
    @PostMapping("/orders/{orderId}/refund")
    public ResponseEntity<RefundResponseDto> refundPayment(
            @PathVariable final String orderId,
            @AuthenticationPrincipal final User user
    ) {

        log.info(
                "Refund request received. Order: {}, User: {}",
                orderId,
                user.getId()
        );

        final RefundResponseDto response =
                paymentService.refundPayment(
                        orderId,
                        user.getId()
                );

        return ResponseEntity.ok(response);
    }
}