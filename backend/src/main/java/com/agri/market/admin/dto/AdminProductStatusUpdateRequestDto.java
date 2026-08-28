package com.agri.market.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request used by administrators to update product status")
public class AdminProductStatusUpdateRequestDto {

    @NotBlank(
            message = "VALIDATION.PRODUCT.STATUS.NOT_BLANK"
    )
    @Schema(
            description = "New product status",
            example = "ACTIVE",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String status;
}