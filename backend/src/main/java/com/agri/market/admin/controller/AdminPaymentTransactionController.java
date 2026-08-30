package com.agri.market.admin.controller;

import com.agri.market.admin.service.AdminPaymentTransactionService;
import com.agri.market.payment.dto.PaymentTransactionResponseDto;
import com.agri.market.payment.entity.PaymentStatus;
import com.agri.market.payment.entity.TransactionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/payment-transactions")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@Tag(
        name = "Admin Payment Transactions",
        description = "Admin APIs for payment transaction management"
)
public class AdminPaymentTransactionController {

    private final AdminPaymentTransactionService
            adminPaymentTransactionService;

    @Operation(
            summary = "Get all payment transactions",
            description = "Returns all payment transactions recorded in the system."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not have admin access"
            )
    })
    @GetMapping
    public ResponseEntity<List<PaymentTransactionResponseDto>>
    getAllTransactions() {

        log.info(
                "Admin request received to fetch all payment transactions"
        );

        return ResponseEntity.ok(
                adminPaymentTransactionService
                        .getAllTransactions()
        );
    }

    @Operation(
            summary = "Get transactions by type",
            description = "Returns transactions filtered by transaction type."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not have admin access"
            )
    })
    @GetMapping("/type/{transactionType}")
    public ResponseEntity<List<PaymentTransactionResponseDto>>
    getTransactionsByType(
            @PathVariable final TransactionType transactionType
    ) {

        log.info(
                "Admin request received for transactions of type: {}",
                transactionType
        );

        return ResponseEntity.ok(
                adminPaymentTransactionService
                        .getTransactionsByType(
                                transactionType
                        )
        );
    }

    @Operation(
            summary = "Get transactions by status",
            description = "Returns transactions filtered by payment status."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not have admin access"
            )
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentTransactionResponseDto>>
    getTransactionsByStatus(
            @PathVariable final PaymentStatus status
    ) {

        log.info(
                "Admin request received for transactions of status: {}",
                status
        );

        return ResponseEntity.ok(
                adminPaymentTransactionService
                        .getTransactionsByStatus(
                                status
                        )
        );
    }

    @Operation(
            summary = "Get transactions for payment",
            description = "Returns all transactions associated with a specific payment."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not have admin access"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found"
            )
    })
    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<List<PaymentTransactionResponseDto>>
    getPaymentTransactions(
            @PathVariable final String paymentId
    ) {

        log.info(
                "Admin request received for transactions of payment: {}",
                paymentId
        );

        return ResponseEntity.ok(
                adminPaymentTransactionService
                        .getPaymentTransactions(
                                paymentId
                        )
        );
    }
}