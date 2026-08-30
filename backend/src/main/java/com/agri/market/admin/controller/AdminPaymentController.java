package com.agri.market.admin.controller;

import com.agri.market.admin.service.AdminPaymentService;
import com.agri.market.payment.dto.PaymentResponseDto;
import com.agri.market.payment.entity.PaymentStatus;
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
@RequestMapping("/api/v1/admin/payments")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@Tag(
        name = "Admin Payments",
        description = "Admin APIs for payment management"
)
public class AdminPaymentController {

    private final AdminPaymentService adminPaymentService;

    @Operation(
            summary = "Get all payments",
            description = "Returns all payments in the system."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payments retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not have admin access"
            )
    })
    @GetMapping
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {

        log.info("Admin request received to fetch all payments");

        return ResponseEntity.ok(
                adminPaymentService.getAllPayments()
        );
    }

    @Operation(
            summary = "Get payment by ID",
            description = "Returns a specific payment from the system."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment retrieved successfully"
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
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDto> getPayment(
            @PathVariable final String paymentId
    ) {

        log.info(
                "Admin request received for payment: {}",
                paymentId
        );

        return ResponseEntity.ok(
                adminPaymentService.getPayment(paymentId)
        );
    }

    @Operation(
            summary = "Get payments by status",
            description = "Returns payments filtered by payment status."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payments retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not have admin access"
            )
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByStatus(
            @PathVariable final PaymentStatus status
    ) {

        log.info(
                "Admin request received for payments with status: {}",
                status
        );

        return ResponseEntity.ok(
                adminPaymentService.getPaymentsByStatus(status)
        );
    }
}