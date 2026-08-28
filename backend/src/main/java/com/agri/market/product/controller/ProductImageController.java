package com.agri.market.product.controller;

import com.agri.market.product.dto.ProductImageRequestDto;
import com.agri.market.product.dto.ProductImageResponseDto;
import com.agri.market.product.service.ProductImageService;
import com.agri.market.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Product Images",
        description = "APIs for managing product images"
)
public class ProductImageController {

    private final ProductImageService productImageService;

    @Operation(
            summary = "Upload product image",
            description = "Uploads an image for a product. A USER can upload images only for their own product. An ADMIN can upload images for any product."
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
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not have access to this product"
            )
    })
    @PostMapping(
            value = "/{productId}/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ProductImageResponseDto> uploadImage(
            @Parameter(
                    description = "Product identifier",
                    required = true
            )
            @PathVariable final String productId,

            @ModelAttribute final ProductImageRequestDto request,

            @AuthenticationPrincipal final User user
    ) {

        log.info(
                "Product image upload request received. Product: {}, User: {}",
                productId,
                user.getId()
        );

        final ProductImageResponseDto response =
                productImageService.uploadImage(
                        productId,
                        request,
                        user
                );

        log.info(
                "Product image uploaded successfully. Product: {}, Image: {}",
                productId,
                response.getId()
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
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    @GetMapping("/{productId}/images")
    public ResponseEntity<List<ProductImageResponseDto>> getProductImages(
            @Parameter(
                    description = "Product identifier",
                    required = true
            )
            @PathVariable final String productId
    ) {

        log.info(
                "Request received to fetch images for product: {}",
                productId
        );

        final List<ProductImageResponseDto> response =
                productImageService.getProductImages(
                        productId
                );

        log.info(
                "Successfully fetched {} images for product: {}",
                response.size(),
                productId
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update product image",
            description = "Updates a product image. A USER can update images only for their own product. An ADMIN can update images for any product."
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
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not have access to this product"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product or image not found"
            )
    })
    @PatchMapping(
            value = "/{productId}/images/{imageId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ProductImageResponseDto> updateImage(
            @Parameter(
                    description = "Product identifier",
                    required = true
            )
            @PathVariable final String productId,

            @Parameter(
                    description = "Product image identifier",
                    required = true
            )
            @PathVariable final String imageId,

            @ModelAttribute final ProductImageRequestDto request,

            @AuthenticationPrincipal final User user
    ) {

        log.info(
                "Product image update request received. Product: {}, Image: {}, User: {}",
                productId,
                imageId,
                user.getId()
        );

        final ProductImageResponseDto response =
                productImageService.updateImage(
                        productId,
                        imageId,
                        request,
                        user
                );

        log.info(
                "Product image updated successfully. Image: {}",
                imageId
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete product image",
            description = "Deletes a product image. A USER can delete images only from their own product. An ADMIN can delete images from any product."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Product image deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not have access to this product"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product or image not found"
            )
    })
    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @Parameter(
                    description = "Product identifier",
                    required = true
            )
            @PathVariable final String productId,

            @Parameter(
                    description = "Product image identifier",
                    required = true
            )
            @PathVariable final String imageId,

            @AuthenticationPrincipal final User user
    ) {

        log.info(
                "Product image deletion request received. Product: {}, Image: {}, User: {}",
                productId,
                imageId,
                user.getId()
        );

        productImageService.deleteImage(
                productId,
                imageId,
                user
        );

        log.info(
                "Product image deleted successfully. Image: {}",
                imageId
        );

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Set primary product image",
            description = "Sets an existing product image as the primary image. A USER can modify only their own product. An ADMIN can modify any product."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Primary image updated successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not have access to this product"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product or image not found"
            )
    })
    @PatchMapping("/{productId}/images/{imageId}/primary")
    public ResponseEntity<ProductImageResponseDto> setPrimaryImage(
            @Parameter(
                    description = "Product identifier",
                    required = true
            )
            @PathVariable final String productId,

            @Parameter(
                    description = "Product image identifier",
                    required = true
            )
            @PathVariable final String imageId,

            @AuthenticationPrincipal final User user
    ) {

        log.info(
                "Set primary image request received. Product: {}, Image: {}, User: {}",
                productId,
                imageId,
                user.getId()
        );

        final ProductImageResponseDto response =
                productImageService.setPrimaryImage(
                        productId,
                        imageId,
                        user
                );

        log.info(
                "Primary product image updated successfully. Product: {}, Image: {}",
                productId,
                imageId
        );

        return ResponseEntity.ok(response);
    }
}


