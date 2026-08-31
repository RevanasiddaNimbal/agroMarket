package com.agri.market.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Admin dashboard summary")
public class AdminDashboardResponseDto {

    @JsonProperty("total_users")
    @Schema(
            description = "Total number of registered users",
            example = "150"
    )
    private long totalUsers;

    @JsonProperty("total_products")
    @Schema(
            description = "Total number of products",
            example = "75"
    )
    private long totalProducts;

    @JsonProperty("total_orders")
    @Schema(
            description = "Total number of orders",
            example = "320"
    )
    private long totalOrders;

    @JsonProperty("total_payments")
    @Schema(
            description = "Total number of payments",
            example = "300"
    )
    private long totalPayments;

    @JsonProperty("total_payment_transactions")
    @Schema(
            description = "Total number of payment transactions",
            example = "350"
    )
    private long totalPaymentTransactions;

    @JsonProperty("total_inventory")
    @Schema(
            description = "Total number of inventory records",
            example = "75"
    )
    private long totalInventory;

    @JsonProperty("total_deliveries")
    @Schema(
            description = "Total number of deliveries",
            example = "280"
    )
    private long totalDeliveries;
}