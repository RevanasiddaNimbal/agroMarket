package com.agri.market.product.controller;

import com.agri.market.product.dto.ProductSearchRequestDto;
import com.agri.market.product.dto.ProductSearchResponseDto;
import com.agri.market.product.service.ProductSearchService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Product Search",
        description = "APIs for searching, filtering and sorting products"
)
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    @Operation(
            summary = "Search and filter products",
            description = "Searches active marketplace products using supported filters, pagination and sorting."
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
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @GetMapping("/search")
    public ResponseEntity<ProductSearchResponseDto> searchProducts(
            @Valid @ModelAttribute final ProductSearchRequestDto request
    ) {

        log.info(
                "Product search request received. Query: {}, Category: {}",
                request.getQuery(),
                request.getCategoryId()
        );

        final ProductSearchResponseDto response =
                productSearchService.searchProducts(request);

        log.info(
                "Product search completed. Total results: {}",
                response.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Search my products",
            description = "Searches and filters products belonging only to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User products retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid search or filter parameters"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @GetMapping("/me/search")
    public ResponseEntity<ProductSearchResponseDto> searchMyProducts(
            @Valid @ModelAttribute final ProductSearchRequestDto request,
            @AuthenticationPrincipal final User user
    ) {

        log.info(
                "User product search request received for user: {}",
                user.getId()
        );

        final ProductSearchResponseDto response =
                productSearchService.searchMyProducts(
                        request,
                        user.getId()
                );

        log.info(
                "User product search completed for user: {}. Total results: {}",
                user.getId(),
                response.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }
}