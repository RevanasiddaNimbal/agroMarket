package com.agri.market.admin.controller;

import com.agri.market.admin.service.AdminOrderService;
import com.agri.market.order.dto.OrderResponseDto;
import com.agri.market.order.dto.OrderStatusUpdateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Admin Orders",
        description = "APIs for administrator order management"
)
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @Operation(
            summary = "Get all orders",
            description = "Returns all orders for administrator monitoring."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Orders retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Administrator access required"
            )
    })
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {

        log.info("Admin get all orders request received");

        final List<OrderResponseDto> response =
                adminOrderService.getAllOrders();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get order",
            description = "Returns a specific order for administrator monitoring."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Administrator access required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(
            @PathVariable final String orderId
    ) {

        log.info(
                "Admin get order request received. Order: {}",
                orderId
        );

        final OrderResponseDto response =
                adminOrderService.getOrder(orderId);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update order status",
            description = "Allows an administrator to update an order status."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order status updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid order status transition"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Administrator access required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable final String orderId,
            @Valid @RequestBody final OrderStatusUpdateRequestDto request
    ) {

        log.info(
                "Admin order status update request received. Order: {}, Status: {}",
                orderId,
                request.getStatus()
        );

        final OrderResponseDto response =
                adminOrderService.updateOrderStatus(
                        orderId,
                        request
                );

        return ResponseEntity.ok(response);
    }
}