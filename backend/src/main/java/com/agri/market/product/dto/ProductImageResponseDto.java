package com.agri.market.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product image response")
public class ProductImageResponseDto {

    @Schema(
            description = "Product image identifier",
            example = "c94e5e38-8e89-4e1c-8a03-15cbd45b570a"
    )
    private String id;

    @Schema(
            description = "Product identifier",
            example = "6e0b9171-970f-4cd4-832f-e2b345d797d0"
    )
    private String productId;

    @Schema(
            description = "Public URL of the image stored in Cloudinary",
            example = "https://res.cloudinary.com/demo/image/upload/v123/agrimarket/products/image.jpg"
    )
    private String imageUrl;

    @Schema(
            description = "Whether this is the primary product image",
            example = "true"
    )
    private boolean primary;

    @Schema(
            description = "Display order of the image",
            example = "0"
    )
    private int displayOrder;
}