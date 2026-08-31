package com.agri.market.admin.controller;


import com.agri.market.delivery.dto.DeliveryResponseDto;
import com.agri.market.delivery.service.AdminDeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/deliveries")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Admin Delivery",
        description = "APIs for administrators to manage delivery progression"
)
public class AdminDeliveryController {

    private final AdminDeliveryService adminDeliveryService;

    @Operation(
            summary = "Get all deliveries",
            description = "Retrieves all deliveries for administration."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Deliveries retrieved successfully"
            )
    })
    @GetMapping
    public ResponseEntity<List<DeliveryResponseDto>> getAllDeliveries() {

        log.info("Admin requested all deliveries");

        return ResponseEntity.ok(
                adminDeliveryService.getAllDeliveries()
        );
    }

    @Operation(
            summary = "Get delivery",
            description = "Retrieves a delivery by delivery identifier."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Delivery retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Delivery not found"
            )
    })
    @GetMapping("/{deliveryId}")
    public ResponseEntity<DeliveryResponseDto> getDelivery(
            @PathVariable final String deliveryId
    ) {

        log.info(
                "Admin requested delivery: {}",
                deliveryId
        );

        return ResponseEntity.ok(
                adminDeliveryService.getDelivery(
                        deliveryId
                )
        );
    }

    @Operation(
            summary = "Mark delivery as shipped",
            description = "Moves an order from PROCESSING to SHIPPED."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Delivery marked as shipped successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Delivery not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid order status transition"
            )
    })
    @PutMapping("/{deliveryId}/ship")
    public ResponseEntity<DeliveryResponseDto> markAsShipped(
            @PathVariable final String deliveryId
    ) {

        log.info(
                "Admin requested SHIPPED status for delivery: {}",
                deliveryId
        );

        return ResponseEntity.ok(
                adminDeliveryService.markAsShipped(
                        deliveryId
                )
        );
    }

    @Operation(
            summary = "Mark delivery as out for delivery",
            description = "Moves an order from SHIPPED to OUT_FOR_DELIVERY."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Delivery marked as out for delivery successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Delivery not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid order status transition"
            )
    })
    @PutMapping("/{deliveryId}/out-for-delivery")
    public ResponseEntity<DeliveryResponseDto> markAsOutForDelivery(
            @PathVariable final String deliveryId
    ) {

        log.info(
                "Admin requested OUT_FOR_DELIVERY status for delivery: {}",
                deliveryId
        );

        return ResponseEntity.ok(
                adminDeliveryService.markAsOutForDelivery(
                        deliveryId
                )
        );
    }
}
