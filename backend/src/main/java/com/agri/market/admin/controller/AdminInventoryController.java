package com.agri.market.admin.controller;

import com.agri.market.admin.service.AdminInventoryService;
import com.agri.market.inventory.dto.InventoryResponseDto;
import com.agri.market.inventory.dto.InventoryUpdateRequestDto;
import com.agri.market.inventory.dto.StockAdjustmentRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Admin Inventory",
        description = "APIs for administrator inventory and stock management"
)
public class AdminInventoryController {

    private final AdminInventoryService adminInventoryService;

    @Operation(
            summary = "Get product inventory",
            description = "Returns inventory information for any product."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product inventory retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product inventory not found"
            )
    })
    @GetMapping("/{productId}/inventory")
    public ResponseEntity<InventoryResponseDto> getInventory(
            @PathVariable final String productId
    ) {

        log.info(
                "Admin inventory request received for product: {}",
                productId
        );

        final InventoryResponseDto response =
                adminInventoryService.getInventory(productId);

        log.info(
                "Admin inventory retrieved successfully for product: {}",
                productId
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update product inventory",
            description = "Updates the total physical quantity of any product."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product inventory updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid inventory quantity"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product inventory not found"
            )
    })
    @PatchMapping("/{productId}/inventory")
    public ResponseEntity<InventoryResponseDto> updateInventory(
            @PathVariable final String productId,
            @Valid @RequestBody final InventoryUpdateRequestDto request
    ) {

        log.info(
                "Admin inventory update request received for product: {}",
                productId
        );

        final InventoryResponseDto response =
                adminInventoryService.updateInventory(
                        productId,
                        request
                );

        log.info(
                "Admin inventory updated successfully for product: {}",
                productId
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Add product stock",
            description = "Adds stock to any product."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product stock added successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid stock quantity"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product inventory not found"
            )
    })
    @PostMapping("/{productId}/inventory/add")
    public ResponseEntity<InventoryResponseDto> addStock(
            @PathVariable final String productId,
            @Valid @RequestBody final StockAdjustmentRequestDto request
    ) {

        log.info(
                "Admin stock addition request received for product: {}",
                productId
        );

        final InventoryResponseDto response =
                adminInventoryService.addStock(
                        productId,
                        request
                );

        log.info(
                "Admin stock added successfully for product: {}",
                productId
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Remove product stock",
            description = "Removes stock from any product."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product stock removed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid stock quantity or insufficient stock"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product inventory not found"
            )
    })
    @PostMapping("/{productId}/inventory/remove")
    public ResponseEntity<InventoryResponseDto> removeStock(
            @PathVariable final String productId,
            @Valid @RequestBody final StockAdjustmentRequestDto request
    ) {

        log.info(
                "Admin stock removal request received for product: {}",
                productId
        );

        final InventoryResponseDto response =
                adminInventoryService.removeStock(
                        productId,
                        request
                );

        log.info(
                "Admin stock removed successfully for product: {}",
                productId
        );

        return ResponseEntity.ok(response);
    }
}