package com.agri.market.inventory.controller;

import com.agri.market.inventory.dto.InventoryResponseDto;
import com.agri.market.inventory.dto.InventoryUpdateRequestDto;
import com.agri.market.inventory.dto.StockAdjustmentRequestDto;
import com.agri.market.inventory.service.InventoryService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Inventory",
        description = "APIs for product inventory and stock availability management"
)
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(
            summary = "Get product availability",
            description = "Returns the current available and reserved quantity of a product."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product availability retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product inventory not found"
            )
    })
    @GetMapping("/products/{productId}/availability")
    public ResponseEntity<InventoryResponseDto> getAvailability(
            @PathVariable final String productId
    ) {

        log.info(
                "Product availability request received for product: {}",
                productId
        );

        final InventoryResponseDto response =
                inventoryService.getAvailability(productId);

        log.info(
                "Product availability retrieved successfully for product: {}",
                productId
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get current user's inventory",
            description = "Returns inventory information for products owned by the authenticated farmer."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Farmer inventory retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @GetMapping("/products/me/inventory")
    public ResponseEntity<List<InventoryResponseDto>> getMyInventory(
            @AuthenticationPrincipal final User user
    ) {

        log.info(
                "Inventory request received for farmer: {}",
                user.getId()
        );

        final List<InventoryResponseDto> response =
                inventoryService.getMyInventory(user.getId());

        log.info(
                "Farmer inventory retrieved successfully for farmer: {}",
                user.getId()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get product inventory",
            description = "Returns inventory information for a specific product."
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
    @GetMapping("/products/{productId}/inventory")
    public ResponseEntity<InventoryResponseDto> getInventory(
            @PathVariable final String productId
    ) {

        log.info(
                "Product inventory request received for product: {}",
                productId
        );

        final InventoryResponseDto response =
                inventoryService.getInventory(productId);

        log.info(
                "Product inventory retrieved successfully for product: {}",
                productId
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update product inventory quantity",
            description = "Updates the total physical quantity of a product owned by the authenticated farmer."
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
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not own the product"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product inventory not found"
            )
    })
    @PatchMapping("/products/{productId}/inventory")
    public ResponseEntity<InventoryResponseDto> updateInventory(
            @PathVariable final String productId,
            @AuthenticationPrincipal final User user,
            @Valid @RequestBody final InventoryUpdateRequestDto request
    ) {

        log.info(
                "Inventory update request received for product: {} by farmer: {}",
                productId,
                user.getId()
        );

        final InventoryResponseDto response =
                inventoryService.updateInventory(
                        productId,
                        user.getId(),
                        request
                );

        log.info(
                "Inventory updated successfully for product: {} by farmer: {}",
                productId,
                user.getId()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Add product stock",
            description = "Adds stock to a product owned by the authenticated farmer."
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
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not own the product"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product inventory not found"
            )
    })
    @PostMapping("/products/{productId}/inventory/add")
    public ResponseEntity<InventoryResponseDto> addStock(
            @PathVariable final String productId,
            @AuthenticationPrincipal final User user,
            @Valid @RequestBody final StockAdjustmentRequestDto request
    ) {

        log.info(
                "Stock addition request received for product: {} by farmer: {}",
                productId,
                user.getId()
        );

        final InventoryResponseDto response =
                inventoryService.addStock(
                        productId,
                        user.getId(),
                        request
                );

        log.info(
                "Stock added successfully for product: {} by farmer: {}",
                productId,
                user.getId()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Remove product stock",
            description = "Removes stock from a product owned by the authenticated farmer."
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
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not own the product"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product inventory not found"
            )
    })
    @PostMapping("/products/{productId}/inventory/remove")
    public ResponseEntity<InventoryResponseDto> removeStock(
            @PathVariable final String productId,
            @AuthenticationPrincipal final User user,
            @Valid @RequestBody final StockAdjustmentRequestDto request
    ) {

        log.info(
                "Stock removal request received for product: {} by farmer: {}",
                productId,
                user.getId()
        );

        final InventoryResponseDto response =
                inventoryService.removeStock(
                        productId,
                        user.getId(),
                        request
                );

        log.info(
                "Stock removed successfully for product: {} by farmer: {}",
                productId,
                user.getId()
        );

        return ResponseEntity.ok(response);
    }
}