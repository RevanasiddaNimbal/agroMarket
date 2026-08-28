package com.agri.market.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request used to upload a product image")
public class ProductImageRequestDto {

    @Schema(
            description = "Product image file",
            type = "string",
            format = "binary",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private MultipartFile image;

    @Schema(
            description = "Whether this image should be the primary product image",
            example = "true",
            defaultValue = "false"
    )
    @Builder.Default
    private boolean primary = false;

    @Min(
            value = 0,
            message = "VALIDATION.PRODUCT.IMAGE.DISPLAY_ORDER.MIN"
    )
    @Schema(
            description = "Display order of the image",
            example = "0",
            defaultValue = "0"
    )
    @Builder.Default
    private int displayOrder = 0;
}