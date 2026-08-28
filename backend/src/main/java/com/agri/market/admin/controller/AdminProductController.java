package com.agri.market.admin.controller;

import com.agri.market.admin.dto.AdminProductStatusUpdateRequestDto;
import com.agri.market.admin.service.AdminProductService;
import com.agri.market.product.dto.ProductResponseDto;
import com.agri.market.product.dto.ProductSearchRequestDto;
import com.agri.market.product.dto.ProductSearchResponseDto;
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
        name = "Admin Product Management",
        description = "APIs for administrators to manage all products"
)
public class AdminProductController {

    private final AdminProductService adminProductService;

    @Operation(
            summary = "Search all products",
            description = "Searches and filters all products available to the administrator."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid search or filter parameters"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Administrator access required"
            )
    })
    @GetMapping
    public ResponseEntity<ProductSearchResponseDto> getProducts(
            @Valid @ModelAttribute final ProductSearchRequestDto request
    ) {
        log.info("Admin product search request received");

        final ProductSearchResponseDto response =
                adminProductService.searchProducts(request);

        log.info(
                "Admin product search completed. Total products: {}",
                response.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get product",
            description = "Retrieves any product by its identifier."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Administrator access required"
            )
    })
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(
            @PathVariable final String productId
    ) {
        log.info(
                "Admin requested product: {}",
                productId
        );

        final ProductResponseDto response =
                adminProductService.getProductById(productId);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update product status",
            description = "Activates or deactivates any product."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product status updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product status"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Administrator access required"
            )
    })
    @PatchMapping("/{productId}/status")
    public ResponseEntity<ProductResponseDto> updateProductStatus(
            @PathVariable final String productId,
            @Valid @RequestBody final AdminProductStatusUpdateRequestDto request
    ) {
        log.info(
                "Admin status update request for product: {}",
                productId
        );

        final ProductResponseDto response =
                adminProductService.updateProductStatus(
                        productId,
                        request.getStatus()
                );

        log.info(
                "Product status updated successfully: {}",
                productId
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete product",
            description = "Deletes any product from the marketplace."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Product deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Administrator access required"
            )
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable final String productId
    ) {
        log.info(
                "Admin delete request for product: {}",
                productId
        );

        adminProductService.deleteProduct(productId);

        log.info(
                "Product deleted successfully by admin: {}",
                productId
        );

        return ResponseEntity.noContent().build();
    }
}