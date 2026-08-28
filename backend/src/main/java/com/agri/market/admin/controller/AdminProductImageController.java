package com.agri.market.admin.controller;

import com.agri.market.admin.service.AdminProductImageService;
import com.agri.market.product.dto.ProductImageRequestDto;
import com.agri.market.product.dto.ProductImageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Admin Product Images",
        description = "Admin APIs for managing product images"
)
public class AdminProductImageController {

    private final AdminProductImageService adminProductImageService;

    @Operation(
            summary = "Upload product image",
            description = "Allows an administrator to upload an image for any product."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Product image uploaded successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid image"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            )
    })
    @PostMapping("/{productId}/images")
    public ResponseEntity<ProductImageResponseDto> uploadImage(
            @PathVariable final String productId,
            @Valid @ModelAttribute final ProductImageRequestDto request
    ) {

        log.info(
                "Admin product image upload request received. Product: {}",
                productId
        );

        final ProductImageResponseDto response =
                adminProductImageService.uploadImage(
                        productId,
                        request
                );

        log.info(
                "Admin product image uploaded successfully. Product: {}",
                productId
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get product images",
            description = "Returns all images belonging to a product."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product images retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            )
    })
    @GetMapping("/{productId}/images")
    public ResponseEntity<List<ProductImageResponseDto>> getProductImages(
            @PathVariable final String productId
    ) {

        log.info(
                "Admin request received to fetch product images. Product: {}",
                productId
        );

        final List<ProductImageResponseDto> response =
                adminProductImageService.getProductImages(
                        productId
                );

        log.info(
                "Admin fetched {} product images. Product: {}",
                response.size(),
                productId
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update product image",
            description = "Allows an administrator to replace or update a product image."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product image updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid image"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product or image not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            )
    })
    @PatchMapping("/{productId}/images/{imageId}")
    public ResponseEntity<ProductImageResponseDto> updateImage(
            @PathVariable final String productId,
            @PathVariable final String imageId,
            @Valid @ModelAttribute final ProductImageRequestDto request
    ) {

        log.info(
                "Admin product image update request received. Product: {}, Image: {}",
                productId,
                imageId
        );

        final ProductImageResponseDto response =
                adminProductImageService.updateImage(
                        productId,
                        imageId,
                        request
                );

        log.info(
                "Admin product image updated successfully. Product: {}, Image: {}",
                productId,
                imageId
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete product image",
            description = "Allows an administrator to delete any product image."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Product image deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product or image not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            )
    })
    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable final String productId,
            @PathVariable final String imageId
    ) {

        log.info(
                "Admin product image deletion request received. Product: {}, Image: {}",
                productId,
                imageId
        );

        adminProductImageService.deleteImage(
                productId,
                imageId
        );

        log.info(
                "Admin product image deleted successfully. Product: {}, Image: {}",
                productId,
                imageId
        );

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Set primary product image",
            description = "Sets an image as the primary image of a product."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Primary image updated successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product or image not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            )
    })
    @PatchMapping("/{productId}/images/{imageId}/primary")
    public ResponseEntity<ProductImageResponseDto> setPrimaryImage(
            @PathVariable final String productId,
            @PathVariable final String imageId
    ) {

        log.info(
                "Admin set-primary-image request received. Product: {}, Image: {}",
                productId,
                imageId
        );

        final ProductImageResponseDto response =
                adminProductImageService.setPrimaryImage(
                        productId,
                        imageId
                );

        log.info(
                "Admin primary image updated successfully. Product: {}, Image: {}",
                productId,
                imageId
        );

        return ResponseEntity.ok(response);
    }
}