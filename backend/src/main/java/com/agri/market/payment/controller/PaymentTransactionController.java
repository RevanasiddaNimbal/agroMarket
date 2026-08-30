package com.agri.market.payment.controller;

import com.agri.market.payment.dto.PaymentTransactionResponseDto;
import com.agri.market.payment.service.PaymentTransactionService;
import com.agri.market.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payment-transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Payment Transactions",
        description = "APIs for payment transaction management"
)
public class PaymentTransactionController {

    private final PaymentTransactionService paymentTransactionService;

    @Operation(
            summary = "Get my payment transactions",
            description = "Returns all payment transactions belonging to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment transactions retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @GetMapping
    public ResponseEntity<List<PaymentTransactionResponseDto>> getMyTransactions(
            @AuthenticationPrincipal final User user
    ) {

        log.info(
                "Get payment transactions request received for user: {}",
                user.getId()
        );

        final List<PaymentTransactionResponseDto> response =
                paymentTransactionService.getMyTransactions(
                        user.getId()
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get payment transactions",
            description = "Returns all transactions associated with a payment belonging to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment transactions retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found"
            )
    })
    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<List<PaymentTransactionResponseDto>> getPaymentTransactions(
            @PathVariable final String paymentId,
            @AuthenticationPrincipal final User user
    ) {

        log.info(
                "Get payment transactions request received. Payment: {}, User: {}",
                paymentId,
                user.getId()
        );

        final List<PaymentTransactionResponseDto> response =
                paymentTransactionService.getPaymentTransactions(
                        paymentId,
                        user.getId()
                );

        return ResponseEntity.ok(response);
    }
}