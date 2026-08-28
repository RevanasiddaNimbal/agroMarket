package com.agri.market.product.mapper;

import com.agri.market.category.entity.Category;
import com.agri.market.product.dto.ProductResponseDto;
import com.agri.market.product.entity.Product;
import com.agri.market.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductMapper {

    public ProductResponseDto toResponseDto(Product product) {
        log.debug("Mapping product to response DTO: {}", product.getId());

        User farmer = product.getFarmer();
        Category category = product.getCategory();

        return ProductResponseDto.builder()
                .id(product.getId())
                .farmerId(farmer != null ? farmer.getId() : null)
                .categoryId(category != null ? category.getId() : null)
                .categoryName(category != null ? category.getName() : null)
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .unit(product.getUnit())
                .quantity(product.getQuantity())
                .location(product.getLocation())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}