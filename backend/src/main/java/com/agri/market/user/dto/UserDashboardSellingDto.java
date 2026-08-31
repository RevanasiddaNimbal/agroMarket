package com.agri.market.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User selling activity summary")
public class UserDashboardSellingDto {

    @JsonProperty("total_products")
    @Schema(
            description = "Total number of products owned by the user",
            example = "8"
    )
    private long totalProducts;

    @JsonProperty("active_products")
    @Schema(
            description = "Number of active products owned by the user",
            example = "6"
    )
    private long activeProducts;

    @JsonProperty("total_product_orders")
    @Schema(
            description = "Number of orders containing the user's products",
            example = "15"
    )
    private long totalProductOrders;

    @JsonProperty("inventory_items")
    @Schema(
            description = "Number of inventory records belonging to the user's products",
            example = "8"
    )
    private long inventoryItems;
}