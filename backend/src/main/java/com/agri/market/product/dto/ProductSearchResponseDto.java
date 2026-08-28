package com.agri.market.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Paginated product search response")
public class ProductSearchResponseDto {

    @Schema(
            description = "Products matching the search and filter criteria"
    )
    private List<ProductResponseDto> products;

    @Schema(
            description = "Current page number",
            example = "0"
    )
    private int page;

    @Schema(
            description = "Number of products per page",
            example = "20"
    )
    private int size;

    @Schema(
            description = "Total number of matching products",
            example = "125"
    )
    private long totalElements;

    @Schema(
            description = "Total number of available pages",
            example = "7"
    )
    private int totalPages;

    @Schema(
            description = "Whether another page is available",
            example = "true"
    )
    private boolean hasNext;

    @Schema(
            description = "Whether a previous page is available",
            example = "false"
    )
    private boolean hasPrevious;
}