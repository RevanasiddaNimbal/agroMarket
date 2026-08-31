package com.agri.market.delivery.controller;

import com.agri.market.delivery.dto.DeliveryOtpRequestDto;
import com.agri.market.delivery.dto.DeliveryOtpVerificationRequestDto;
import com.agri.market.delivery.dto.DeliveryResponseDto;
import com.agri.market.delivery.service.DeliveryService;
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
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Deliveries",
        description = "APIs for delivery management and OTP verification"
)
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Operation(
            summary = "Get delivery information",
            description = "Returns delivery information for an order belonging to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Delivery retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order or delivery not found"
            )
    })
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<DeliveryResponseDto> getDelivery(
            @PathVariable final String orderId,
            @AuthenticationPrincipal final User user
    ) {

        final DeliveryResponseDto response =
                deliveryService.getDelivery(
                        orderId,
                        user.getId()
                );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Generate delivery OTP",
            description = "Generates a delivery OTP and sends it to the authenticated user's email address."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Delivery OTP generated and sent successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Delivery OTP cannot be generated"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    @PostMapping("/otp")
    public ResponseEntity<Void> generateDeliveryOtp(
            @Valid @RequestBody final DeliveryOtpRequestDto request,
            @AuthenticationPrincipal final User user
    ) {

        deliveryService.generateDeliveryOtp(
                request,
                user.getId()
        );

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Verify delivery OTP",
            description = "Verifies the delivery OTP and marks the order as delivered."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Delivery OTP verified and order delivered successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired delivery OTP"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order or delivery not found"
            )
    })
    @PostMapping("/otp/verify")
    public ResponseEntity<DeliveryResponseDto> verifyDeliveryOtp(
            @Valid @RequestBody final DeliveryOtpVerificationRequestDto request,
            @AuthenticationPrincipal final User user
    ) {

        final DeliveryResponseDto response =
                deliveryService.verifyDeliveryOtp(
                        request,
                        user.getId()
                );

        return ResponseEntity.ok(response);
    }
}
