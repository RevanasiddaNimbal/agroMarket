package com.agri.market.admin.service;

import com.agri.market.product.dto.ProductImageRequestDto;
import com.agri.market.product.dto.ProductImageResponseDto;

import java.util.List;

public interface AdminProductImageService {

    ProductImageResponseDto uploadImage(
            String productId,
            ProductImageRequestDto request
    );

    List<ProductImageResponseDto> getProductImages(
            String productId
    );

    ProductImageResponseDto updateImage(
            String productId,
            String imageId,
            ProductImageRequestDto request
    );

    void deleteImage(
            String productId,
            String imageId
    );

    ProductImageResponseDto setPrimaryImage(
            String productId,
            String imageId
    );
}