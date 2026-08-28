package com.agri.market.product.mapper;

import com.agri.market.product.dto.ProductImageResponseDto;
import com.agri.market.product.entity.ProductImage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductImageMapper {

    public ProductImageResponseDto toResponseDto(
            final ProductImage productImage
    ) {

        if (productImage == null) {
            return null;
        }

        log.debug(
                "Mapping product image to response DTO: {}",
                productImage.getId()
        );

        return ProductImageResponseDto.builder()
                .id(productImage.getId())
                .productId(
                        productImage.getProduct() != null
                                ? productImage.getProduct().getId()
                                : null
                )
                .imageUrl(productImage.getImageUrl())
                .primary(productImage.isPrimary())
                .displayOrder(productImage.getDisplayOrder())
                .build();
    }
}