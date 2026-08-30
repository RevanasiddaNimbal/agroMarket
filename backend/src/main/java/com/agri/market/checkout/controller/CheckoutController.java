package com.agri.market.checkout.controller;

import com.agri.market.checkout.dto.CheckoutRequestDto;
import com.agri.market.checkout.dto.CheckoutResponseDto;
import com.agri.market.checkout.service.CheckoutService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Checkout",
        description = "APIs for validating and initiating product checkout"
)
public class CheckoutController {

    private final CheckoutService checkoutService;

    @Operation(
            summary = "Checkout a product",
            description = "Validates the product, delivery address and inventory before proceeding to payment."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Checkout validation completed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid checkout request"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product, address or inventory not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Product is unavailable or insufficient stock"
            )
    })
    @PostMapping
    public ResponseEntity<CheckoutResponseDto> checkout(
            @Valid @RequestBody final CheckoutRequestDto request,
            @AuthenticationPrincipal final User user
    ) {

        log.info(
                "Checkout request received for user: {}",
                user.getId()
        );

        final CheckoutResponseDto response =
                checkoutService.checkout(
                        request,
                        user.getId()
                );

        log.info(
                "Checkout validation completed successfully for user: {}",
                user.getId()
        );

        return ResponseEntity.ok(response);
    }
}