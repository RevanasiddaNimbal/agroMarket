package com.agri.market.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Parameters used to search and filter marketplace products")
public class ProductSearchRequestDto {

    @Size(
            max = 100,
            message = "VALIDATION.PRODUCT.SEARCH.QUERY.SIZE"
    )
    @Schema(
            description = "Search keyword for product name or description",
            example = "wheat seeds"
    )
    private String query;

    @Schema(
            description = "Category identifier",
            example = "550e8400-e29b-41d4-a716-446655440001"
    )
    private String categoryId;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "VALIDATION.PRODUCT.SEARCH.MIN_PRICE"
    )
    @Schema(
            description = "Minimum product price",
            example = "100.00"
    )
    private BigDecimal minPrice;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "VALIDATION.PRODUCT.SEARCH.MAX_PRICE"
    )
    @Schema(
            description = "Maximum product price",
            example = "5000.00"
    )
    private BigDecimal maxPrice;

    @Schema(
            description = "Product unit",
            example = "KG"
    )
    private String unit;

    @Schema(
            description = "Product location",
            example = "Vijayapura"
    )
    private String location;

    @Schema(
            description = "Product status",
            example = "ACTIVE"
    )
    private String status;

    @Min(
            value = 0,
            message = "VALIDATION.PRODUCT.SEARCH.PAGE.MIN"
    )
    @Schema(
            description = "Page number starting from zero",
            example = "0",
            defaultValue = "0"
    )
    @Builder.Default
    private int page = 0;

    @Min(
            value = 1,
            message = "VALIDATION.PRODUCT.SEARCH.SIZE.MIN"
    )
    @Schema(
            description = "Number of products per page",
            example = "20",
            defaultValue = "20"
    )
    @Builder.Default
    private int size = 20;

    @Schema(
            description = "Field used for sorting",
            example = "createdAt",
            defaultValue = "createdAt"
    )
    @Builder.Default
    private String sortBy = "createdAt";

    @Schema(
            description = "Sort direction",
            example = "DESC",
            defaultValue = "DESC"
    )
    @Builder.Default
    private String sortDirection = "DESC";
}
